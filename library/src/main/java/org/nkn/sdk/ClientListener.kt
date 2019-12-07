package org.nkn.sdk

abstract class ClientListener {
    abstract fun onConnect()
    abstract fun onMessage(src: String, data: String?, pid: ByteArray, type: Int, encrypted: Boolean): Any?
    abstract fun onBinaryMessage(
        src: String,
        data: ByteArray?,
        pid: ByteArray,
        type: Int,
        encrypted: Boolean
    ): Any?
    abstract fun onClosing()
    abstract fun onClosed()
    abstract fun onError(e: Throwable)
    abstract fun onBlock()
}