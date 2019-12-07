package org.nkn.sdk

import android.provider.Settings
import android.util.Log
import kotlinx.coroutines.*
import okhttp3.internal.wait
import org.nkn.sdk.configure.*
import org.nkn.sdk.network.WsApi
import java.util.concurrent.Future


const val TAG = "Client"

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
            Log.d(TAG, """Client listener "onConnect"""")
            listener?.onConnect()
        }

        override fun onMessage(
            src: String,
            data: String?,
            pid: ByteArray,
            type: Int,
            encrypted: Boolean

        ) {
            listener?.onMessage(src, data, pid, type, encrypted)
        }

        override fun onBinaryMessage(
            src: String,
            data: ByteArray?,
            pid: ByteArray,
            type: Int,
            encrypted: Boolean
        ) {
            listener?.onBinaryMessage(src, data, pid, type, encrypted)
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