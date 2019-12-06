package org.nkn.sdk

abstract class ClientListener {
    abstract fun onConnect()
    abstract fun onMessage(src: String, data: String, type: Int, encrypted: Boolean, pid: ByteArray)
    abstract fun onBinaryMessage(
        src: String,
        data: ByteArray,
        type: Int,
        encrypted: Boolean,
        pid: ByteArray
    )

    abstract fun onClosing()
    abstract fun onClosed()
    abstract fun onError(e: Throwable)
    abstract fun onBlock()
}