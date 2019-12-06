package org.nkn.sdk.error


enum class RpcErrorCode(val code: Int) {
    UNKNOWN_ERROR(0)

}

class RpcError(val code: RpcErrorCode, override val message: String) : Throwable() {
    companion object {
        const val UNKNOWN_ERROR = "unknown error"
    }
}