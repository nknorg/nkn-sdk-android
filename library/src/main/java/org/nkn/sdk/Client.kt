package org.nkn.sdk

import android.provider.Settings
import kotlinx.coroutines.*
import okhttp3.internal.wait
import org.nkn.sdk.configure.*
import org.nkn.sdk.network.WsApi
import java.util.concurrent.Future


fun genIdentifier(base: String?, id: Int?): String {
    if (id == null) {
        return base!!
    }
    return "__${id}__" + (if (base.isNullOrEmpty()) "" else ".$base")
}

class Client @JvmOverloads constructor(
    seed: String,
    identifier: String?,
    val seedRpcServer: List<String>? = org.nkn.sdk.configure.seed,
    encrypt: Boolean? = ENCRYPT,
    msgHoldingSeconds: Long? = MSG_HOLDING_SECONDS,
    val reconnectIntervalMin: Long? = RECONNECT_INTERVAL_MIN,
    val reconnectIntervalMax: Long? = RECONNECT_INTERVAL_MAX,
    responseTimeout: Long? = RESPONSE_TIMEOUT,
    numSubClients: Int? = 3,
    var listener: ClientListener? = null,
    msgCacheExpire: Long? = 30000
) {
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

    inner class MultiClientListener : ClientListener() {
        override fun onConnect() {
            listener?.onConnect()
        }

        override fun onMessage(
            src: String,
            data: String,
            type: Int,
            encrypted: Boolean,
            pid: ByteArray
        ) {
            listener?.onMessage(src, data, type, encrypted, pid)
        }

        override fun onBinaryMessage(
            src: String,
            data: ByteArray,
            type: Int,
            encrypted: Boolean,
            pid: ByteArray
        ) {
            listener?.onBinaryMessage(src, data, type, encrypted, pid)
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
            var deferreds: MutableList<Deferred<Unit>> = emptyList<Deferred<Unit>>().toMutableList()
            for (i in clients) {
                deferreds.add(async { i.connect() })
            }
            deferreds.awaitAll()
        }
    }

}