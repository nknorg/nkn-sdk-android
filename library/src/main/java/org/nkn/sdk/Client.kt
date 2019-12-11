package org.nkn.sdk

import android.util.Log
import org.nkn.sdk.cache.multiClientCache
import kotlinx.coroutines.*
import org.json.JSONObject
import org.nkn.sdk.configure.*
import org.nkn.sdk.network.RpcApi
import org.nkn.sdk.network.WsApi
import org.nkn.sdk.protocol.PID_SIZE
import org.nkn.sdk.utils.Utils


const val TAG = "Client"
const val identifierRegex = """^__\d+__"""
fun genIdentifier(base: String?, id: Int?): String {
    if (id == null) {
        return base!!
    }
    return "__${id}__" + (if (base.isNullOrEmpty()) "" else ".$base")
}

fun removeIdentifierPrefix(addr: String): String {
    return addr.replace(Regex("$identifierRegex."), "")
}

fun getIdentifierPrefix(addr: String): String {
    return Regex("$identifierRegex.").find(addr)?.value ?: ""
}


class Client @JvmOverloads constructor(
    seed: String,
    identifier: String?,
    val seedRpcServer: List<String>? = org.nkn.sdk.configure.seed,
    encrypt: Boolean? = ENCRYPT,
    val msgHoldingSeconds: Int? = MSG_HOLDING_SECONDS,
    val reconnectIntervalMin: Long? = RECONNECT_INTERVAL_MIN,
    val reconnectIntervalMax: Long? = RECONNECT_INTERVAL_MAX,
    responseTimeout: Long? = RESPONSE_TIMEOUT,
    numSubClients: Int? = 3,
    var listener: ClientListener? = null,
    msgCacheExpire: Long? = 30000
) {
    val hashCode = this.hashCode()
    val clients: MutableList<WsApi>
    val defaultClient = WsApi(
        seed,
        identifier,
        seedRpcServer,
        encrypt,
        msgHoldingSeconds,
        reconnectIntervalMin,
        reconnectIntervalMax,
        responseTimeout,
        MultiClientListener()
    )
    val identifier: String = identifier ?: ""
    val address =
        if (this.identifier.isNullOrEmpty()) this.defaultClient.key.publicKeyHash else "${this.identifier}.${this.defaultClient.key.publicKeyHash}"
    var isReadly = false
    private var connectJobs: ArrayList<Job> = ArrayList()
    private val rpcApi = RpcApi()

    inner class MultiClientListener : ClientListener() {
        override fun onConnect() {
            isReadly = true
            if (multiClientCache.get(hashCode.toString()) != null) {
                return
            }
            Log.d(TAG, """Client listener "onConnect"""")
            multiClientCache.put(hashCode.toString(), true)
            listener?.onConnect()
        }

        override fun onMessage(
            src: String,
            data: String?,
            pid: ByteArray,
            type: Int,
            encrypted: Boolean

        ): Any? {
            val pidHash = Utils.hexEncode(pid)
            if (multiClientCache.get(pidHash) != null) {
                return false
            }
            multiClientCache.put(pidHash, true)
            var srcId = removeIdentifierPrefix(src)

            return listener?.onMessage(srcId, data, pid, type, encrypted)
        }

        override fun onBinaryMessage(
            src: String,
            data: ByteArray?,
            pid: ByteArray,
            type: Int,
            encrypted: Boolean
        ): Any? {
            val pidHash = Utils.hexEncode(pid)
            if (multiClientCache.get(pidHash) != null) {
                return false
            }
            multiClientCache.put(pidHash, true)
            var srcId = removeIdentifierPrefix(src)
            return listener?.onBinaryMessage(srcId, data, pid, type, encrypted)
        }

        override fun onClosing() {
            listener?.onClosing()
        }

        override fun onClosed() {
            listener?.onClosed()
        }

        override fun onError(e: Throwable) {
            listener?.onError(e)
        }

        override fun onBlock() {
            listener?.onBlock()
        }
    }

    init {
        clients = mutableListOf(defaultClient)
        for (i in 0 until numSubClients!!) {
            clients.add(
                WsApi(
                    seed,
                    genIdentifier(identifier, i),
                    seedRpcServer,
                    encrypt,
                    msgHoldingSeconds,
                    reconnectIntervalMin,
                    reconnectIntervalMax,
                    responseTimeout,
                    MultiClientListener()
                )
            )
        }
    }

    fun connect() {
        GlobalScope.launch {
            for (i in clients) {
                connectJobs.add(launch { i.connect() })
            }
        }

    }

    fun close() {
        GlobalScope.launch {
            for (i in clients) {
                clients.map { item -> async { item.close() } }
            }
        }
    }

    fun send(
        dest: String,
        data: String,
        pid: ByteArray? = null,
        replyToPid: ByteArray? = null,
        noReply: Boolean? = false,
        encrypt: Boolean? = true,
        msgHoldingSeconds: Int? = this.msgHoldingSeconds
    ) {
        var msgPid = pid
        if (pid == null) {
            msgPid = Utils.randomBytes(PID_SIZE)
        }
        if (isReadly) {
            GlobalScope.launch {
                clients.map { item -> async { item.send(getIdentifierPrefix(item.address) + dest, data, msgPid, replyToPid, noReply, encrypt, msgHoldingSeconds) } }
            }
        } else {
            //todo await connect
            throw Throwable("not connected yet")
        }
    }

    fun send(
        dests: Array<String>,
        data: String,
        pid: ByteArray? = null,
        replyToPid: ByteArray? = null,
        noReply: Boolean? = false,
        encrypt: Boolean? = true,
        msgHoldingSeconds: Int? = this.msgHoldingSeconds
    ) {
        var msgPid = pid
        if (pid == null) {
            msgPid = Utils.randomBytes(PID_SIZE)
        }
        if (isReadly) {
            GlobalScope.launch {
                for (i in clients.indices) {
                    async {
                        clients[i].send(
                            dests.map { dest -> getIdentifierPrefix(clients[i].address) + dest }.toTypedArray(),
                            data,
                            msgPid,
                            replyToPid,
                            noReply,
                            encrypt,
                            msgHoldingSeconds
                        )
                    }
                }
            }
        } else {
            //todo await connect
            throw Throwable("not connected yet")
        }
    }

    fun getSubscribers(topic: String, offset: Int = 0, limit: Int = 1000, meta: Boolean = false, txPool: Boolean = false): JSONObject? {
        return rpcApi.getSubscribers(topic, offset, limit, meta, txPool)
    }

    fun getSubscribersCount(topic: String): JSONObject? {
        return rpcApi.getSubscribersCount(topic)
    }

    fun getSubscription(topic: String, subscriber: String): JSONObject? {
        return rpcApi.getSubscription(topic, subscriber)
    }

}