package org.nkn.sdk.network

import android.util.Log
import okhttp3.*
import okio.ByteString
import org.json.JSONObject
import org.nkn.sdk.ClientListener
import org.nkn.sdk.configure.*
import org.nkn.sdk.const.SIGNATURE_SIZE
import org.nkn.sdk.const.StatusCode
import org.nkn.sdk.crypto.Key
import org.nkn.sdk.pb.ClientMessageProto
import org.nkn.sdk.pb.PayloadsProto
import org.nkn.sdk.protocol.*
import org.nkn.sdk.utils.Utils
import java.nio.ByteBuffer
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule
import kotlin.random.Random

const val TAG = "WsApi"

const val MAX_CLIENT_MESSAGE_SIZE = 4000000

class WsApi @JvmOverloads constructor(
    seed: String,
    identifier: String?,
    val seedRpcServer: List<String>? = org.nkn.sdk.configure.seed,
    encrypt: Boolean? = ENCRYPT,
    val msgHoldingSeconds: Int? = MSG_HOLDING_SECONDS,
    val reconnectIntervalMin: Long? = RECONNECT_INTERVAL_MIN,
    val reconnectIntervalMax: Long? = RECONNECT_INTERVAL_MAX,
    responseTimeout: Long? = RESPONSE_TIMEOUT,
    var listener: ClientListener? = null
) {
    var ws: WebSocket? = null
    var reconnectInterval: Long = reconnectIntervalMin!!
    val key: Key = Key(seed)
    val curveSecretKey = Utils.convertSecretKey(key.privateKey)
    val identifier: String = identifier ?: ""
    val address =
        if (this.identifier.isNullOrEmpty()) this.key.publicKeyHash else "${this.identifier}.${this.key.publicKeyHash}"
    var sigChainBlockHash: String? = null
    var node: JSONObject? = null
    var isReadly = false
    private val client = OkHttpClient()
    private var shouldReconnect = false

    inner class WsListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            Log.d(TAG, """"addr: $address, WebSocket listener "onOpen"""")
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            Log.d(TAG, """"addr: $address, WebSocket listener "onMessage", text: $text""")
            val message = JSONObject(text)
            if (message.has("Error") && message.getInt("Error") != StatusCode.SUCCESS.code) {
                if (message.getInt("Error") == StatusCode.WRONG_NODE.code) {
                    if(message.getJSONObject("Result").getString("addr") == node!!.getString("addr"))
                        return
                    close()
                    ws = null
                    createWebSocketConnection(message.getJSONObject("Result"))
                } else if (message.getString("Action") == "setClient") {
                    ws?.close(1000, null)
                }
                return
            }
            when (message.getString("Action")) {
                "setClient" -> {
                    sigChainBlockHash = message.getJSONObject("Result").getString("sigChainBlockHash")
                    isReadly = true
                    listener?.onConnect()
                }
                "updateSigChainBlockHash" -> sigChainBlockHash = message.getString("Result")
                "sendRawBlock" -> listener?.onBlock()
                else -> Log.e(TAG, "Unknown msg type: $message")
            }

        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            Log.d(TAG, """"addr: $address, WebSocket listener "onBinaryMessage", bytes: ${bytes.size()}""")
            val handled = handleBinaryMessage(bytes.toByteArray())
            if (!handled) Log.d(TAG, "Unhandled msg")
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, """"addr: $address, WebSocket listener "onClosing", code: $code, reason: $reason""")
            listener?.onClosing()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            Log.d(TAG, """"addr: $address, WebSocket listener "onClosed", code: $code, reason: $reason""")
            if (shouldReconnect) reconnect() else listener?.onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            Log.d(TAG, """"addr: $address, WebSocket listener "onFailure", error: $t, reason: $response""")
            if (shouldReconnect) reconnect() else listener?.onError(t)
        }
    }


    fun handleInboundMsg(raw: ByteArray): Boolean {
        val msg = ClientMessageProto.InboundMessage.parseFrom(raw)
        if (msg.prevSignature.size() > 0) {
            var receipt = newReceipt(msg.prevSignature.toByteArray(), key)
            this.ws?.send(ByteString.of(ByteBuffer.wrap(receipt.toByteArray())))
        }
        val pldMsg = PayloadsProto.Message.parseFrom(msg.payload)
        val pldBytes = if (pldMsg.encrypted) {
            decryptPayload(pldMsg, Utils.getPublicKeyByClientAddr(msg.src), key)
        } else {
            pldMsg.payload.toByteArray()
        }

        val payload = PayloadsProto.Payload.parseFrom(pldBytes)
        var data: String? = null
        when (payload.type) {
            PayloadsProto.PayloadType.TEXT -> {
                var textData = PayloadsProto.TextData.parseFrom(payload.data)
                data = textData.text
            }
            PayloadsProto.PayloadType.ACK -> {
                data = null
            }
        }

        when (payload.type) {
            PayloadsProto.PayloadType.TEXT, PayloadsProto.PayloadType.BINARY -> {
                Log.i(TAG, """addr: $address, receive message: $data""")
                val response = listener?.onMessage(msg.src, data, payload.pid.toByteArray(), payload.typeValue, pldMsg.encrypted)
                Log.d(TAG, """addr: $address, response: $response""")
                if (response is Boolean && !response) {
                    return false
                } else if (response != null && response is String) {
                    this.send(msg.src, response, replyToPid = payload.pid.toByteArray(), encrypt = pldMsg.encrypted, msgHoldingSeconds = 0, noReply = true)
                } else {
                    this.sendACK(msg.src, payload.pid.toByteArray(), pldMsg.encrypted)
                }

                return true
            }
        }

        return false
    }

    fun handleBinaryMessage(raw: ByteArray): Boolean {
        val clientMessage = ClientMessageProto.ClientMessage.parseFrom(raw)
        when (clientMessage.messageType) {
            ClientMessageProto.ClientMessageType.INBOUND_MESSAGE -> return handleInboundMsg(clientMessage.message.toByteArray())
            else -> return false
        }
    }

    fun handleConnect(url: String) {
        Log.d(TAG, "addr: $address, connect to ws://$url")
        val request = Request.Builder()
            .url("ws://$url")
            .build()

        this.ws = client.newBuilder()
            .pingInterval(10, TimeUnit.SECONDS)
            .build().newWebSocket(request, WsListener())
        this.shouldReconnect = true
    }

    fun createWebSocketConnection(nodeInfo: JSONObject) {
        if (!nodeInfo.has("addr")) {
            Log.e(TAG, "No address in node info $nodeInfo")
            this.reconnect()
            return
        }
        this.node = nodeInfo
        this.handleConnect(nodeInfo.getString("addr"))
        Log.d(TAG, "send setClient :${JSONObject(mapOf("Action" to "setClient", "Addr" to this.address))}")
        this.ws!!.send(JSONObject(mapOf("Action" to "setClient", "Addr" to this.address)).toString())
    }

    fun connect() {
        val rpcAddr = seedRpcServer?.get(Random.nextInt(0, seedRpcServer.size))
        val rpcApi = RpcApi(rpcAddr)

        try {
            val nodeInfo = rpcApi.getWsAddr(address)
            if (nodeInfo == null) {
                Log.e(TAG, "get ws addr is null")
                this.connect()
                return
            }
            this.createWebSocketConnection(nodeInfo)
        } catch (e: Throwable) {
            Log.e(TAG, "RPC call failed, $e")
            this.connect()
        }
    }

    fun reconnect() {
        if (shouldReconnect) {
            Log.i(TAG, "Reconnecting in ${reconnectInterval / 1000} s...")
            Timer().schedule(reconnectInterval) {
                connect()
            }
            reconnectInterval *= 2
            if (reconnectInterval > this.reconnectIntervalMax!!) {
                reconnectInterval = this.reconnectIntervalMax
            }
        } else {
            this.listener?.onClosed()
        }
    }

    fun close() {
        this.shouldReconnect = false
        this.ws?.close(1000, null)
    }

    fun messageFromPayload(payload: PayloadsProto.Payload, encrypt: Boolean, dest: String): PayloadsProto.Message {
        if (encrypt) {
            return encryptPayload(payload.toByteArray(), dest, key)
        }
        return newMessage(payload.toByteArray(), false)
    }

    fun messageFromPayloads(payload: PayloadsProto.Payload, encrypt: Boolean, dests: Array<String>): Array<PayloadsProto.Message> {
        return encryptPayloads(payload.toByteArray(), dests, key)
    }

    fun sendACK(dests: Array<String>, pid: ByteArray, encrypt: Boolean) {
        // todo multi
//        if (dest is Array<*>) {
//            if (dest.size == 0) return
//            if (dest.size == 1) {
//                sendACK(dest[0], pid, encrypt)
//                return
//            }
//            if (dest.size > 1 && encrypt) {
//                Log.i(TAG, "Encrypted ACK with multicast is not supported, fallback to unicast.")
//                for (i in 0 until dest.size) {
//                    sendACK(dest[i], pid, encrypt)
//                }
//                return
//            }
//        }
    }

    @JvmOverloads
    fun sendACK(dest: String, pid: ByteArray, encrypt: Boolean) {
        val payload = newAckPayload(pid, null)
        val pldMessage = messageFromPayload(payload, encrypt, dest)
        val obMsg = newOutboundMessage(dest, pldMessage.toByteArray(), 0, this.address, this.key, Utils.hexDecode(this.node!!.getString("pubkey")), this.sigChainBlockHash)
        this.ws?.send(ByteString.of(ByteBuffer.wrap(obMsg.toByteArray())))
    }

    fun sendMsg(dests: Any, data: Any, encrypt: Boolean, maxHoldingSeconds: Int, replyToPid: ByteArray?, msgPid: ByteArray?): ByteArray? {
        if (!isReadly) {
            return null
        }
        val payload = if (data is String) newTextPayload(data, replyToPid, msgPid) else newBinaryPayload(data as ByteArray, replyToPid, msgPid)
        when (dests) {
            is String -> {
                val pldMsg = this.messageFromPayload(payload, encrypt, dests)
                val obMsg = newOutboundMessage(
                    dests,
                    pldMsg.toByteArray(),
                    maxHoldingSeconds,
                    this.address,
                    this.key,
                    Utils.hexDecode(this.node!!.getString("pubkey")),
                    this.sigChainBlockHash
                )
                this.ws?.send(ByteString.of(ByteBuffer.wrap(obMsg.toByteArray())))
                return payload.pid.toByteArray()
            }
            is Array<*> -> {
                val pldMsg = this.messageFromPayloads(payload, encrypt, dests as Array<String>)
                val msgs: ArrayList<ClientMessageProto.ClientMessage> = ArrayList()
                var destList: ArrayList<String> = ArrayList()
                var pldList: ArrayList<ByteArray> = ArrayList()
                var totalSize = 0
                for (i in pldMsg.indices) {
                    val size = pldMsg[i].toByteArray().size + dests[i].length + SIGNATURE_SIZE
                    if (size > MAX_CLIENT_MESSAGE_SIZE) {
                        throw Throwable("message size is greater than $MAX_CLIENT_MESSAGE_SIZE bytes")
                    }
                    if (totalSize + size > MAX_CLIENT_MESSAGE_SIZE) {
                        msgs.add(
                            newOutboundMessage(
                                destList.toTypedArray(),
                                pldList.toTypedArray(),
                                maxHoldingSeconds,
                                this.address,
                                this.key,
                                Utils.hexDecode(this.node!!.getString("pubkey")),
                                this.sigChainBlockHash
                            )
                        )
                        destList = ArrayList()
                        pldList = ArrayList()
                        totalSize = 0
                    }
                    destList.add(dests[i])
                    pldList.add(pldMsg[i].toByteArray())
                    totalSize += size
                }

                msgs.add(
                    newOutboundMessage(
                        destList.toTypedArray(),
                        pldList.toTypedArray(),
                        maxHoldingSeconds,
                        this.address,
                        this.key,
                        Utils.hexDecode(this.node!!.getString("pubkey")),
                        this.sigChainBlockHash
                    )
                )

                if (msgs.size > 1) {
                    Log.i(TAG, "Client message size is greater than ${MAX_CLIENT_MESSAGE_SIZE} bytes, split into ${msgs.size} batches.")
                }
                msgs.forEach { msg -> this.ws?.send(ByteString.of(ByteBuffer.wrap(msg.toByteArray()))) }
                return payload.pid.toByteArray()
            }
            else -> {
                throw Throwable("dest type must be String or Array<String>")
            }
        }

        return null
    }

    @JvmOverloads
    fun send(
        dest: String,
        data: String,
        pid: ByteArray? = null,
        replyToPid: ByteArray? = null,
        noReply: Boolean? = false,
        encrypt: Boolean? = true,
        msgHoldingSeconds: Int? = this.msgHoldingSeconds
    ) {
        this.sendMsg(dest, data, encrypt!!, msgHoldingSeconds!!, replyToPid, pid)
    }

    @JvmOverloads
    fun send(
        dests: Array<String>,
        data: String,
        pid: ByteArray? = null,
        replyToPid: ByteArray? = null,
        noReply: Boolean? = false,
        encrypt: Boolean? = true,
        msgHoldingSeconds: Int? = this.msgHoldingSeconds
    ) {
        this.sendMsg(dests, data, encrypt!!, msgHoldingSeconds!!, replyToPid, pid)
    }

//    @JvmOverloads
//    fun send(
//        dests: Array<String>,
//        data: Array<String>,
//        pid: ByteArray? = null,
//        replyToPid: ByteArray? = null,
//        noReply: Boolean? = false,
//        encrypt: Boolean = true,
//        msgHoldingSeconds: Int? = this.msgHoldingSeconds
//    ) {
//
//    }
}

