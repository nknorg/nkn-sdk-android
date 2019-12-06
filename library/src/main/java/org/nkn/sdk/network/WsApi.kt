package org.nkn.sdk.network

import okhttp3.*
import okhttp3.WebSocketListener
import okio.ByteString
import org.json.JSONObject
import org.nkn.sdk.ClientListener
import org.nkn.sdk.configure.*
import org.nkn.sdk.crypto.Key
import org.nkn.sdk.utils.Utils
import java.time.Duration
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.schedule
import kotlin.random.Random


class WsApi @JvmOverloads constructor(
    seed: String,
    identifier: String?,
    val seedRpcServer: List<String>? = org.nkn.sdk.configure.seed,
    encrypt: Boolean? = ENCRYPT,
    msgHoldingSeconds: Long? = MSG_HOLDING_SECONDS,
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
        if (this.identifier.isNullOrEmpty()) "" else "${this.identifier}.${this.key.publicKeyHash}"
    private val client = OkHttpClient()
    private var shouldReconnect = false

    inner class WsListener : WebSocketListener() {
        override fun onOpen(webSocket: WebSocket, response: Response) {
            println("-------------------on open---------------")
            listener?.onConnect()
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            println("================ on t message ================")
            println(text)

        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            println("================ on b message ================")
            println(bytes)
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            println("=============== on closing ============ $reason")
            listener?.onClosing()
        }

        override fun onClosed(webSocket: WebSocket, code: Int, reason: String) {
            println("=============== on closed ============ $reason")
            if (shouldReconnect) reconnect() else listener?.onClosed()
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            println("============= on failure ============ $t")
            if (shouldReconnect) reconnect() else listener?.onError(t)
        }
    }


    fun handleConnect(url: String) {
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
            println("No address in node info $nodeInfo")
            this.reconnect()
            return
        }
        this.handleConnect(nodeInfo.getString("addr"))
        println("send setClient :${JSONObject(mapOf("Action" to "setClient", "Addr" to this.address))}")
        this.ws!!.send(JSONObject(mapOf("Action" to "setClient", "Addr" to this.address)).toString())
    }

    fun connect() {
        val rpcAddr = seedRpcServer?.get(Random.nextInt(0, seedRpcServer.size))
        val rpcApi = RpcApi(rpcAddr)
        val nodeInfo = rpcApi.getWsAddr(address)
        if (nodeInfo == null) {
            println("get ws addr is null")
            this.reconnect()
            return
        }
        try {
            this.createWebSocketConnection(nodeInfo)
        } catch (e: Throwable) {
            println("RPC call failed, $e")
            this.reconnect()
        }


    }

    fun reconnect() {
        if (shouldReconnect) {
            println("Reconnecting in ${reconnectInterval / 1000} s...")
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

}

