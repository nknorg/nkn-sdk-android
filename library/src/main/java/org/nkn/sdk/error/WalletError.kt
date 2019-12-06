package org.nkn.sdk.error


enum class WalletErrorCode(val code: Int) {
    UNKNOWN_ERROR(0),
    NOT_ENOUGH_BALANCE(1),
    INVALID_ADDRESS(2),
    WRONG_PASSWORD(3),
    INVALID_WALLET_FORMAT(4),
    INVALID_WALLET_VERSION(5),
    INVALID_ARGUMENT(6),
    INVALID_RESPONSE(7),
    NO_RPC_SERVER(8),
    SERVER_ERROR(9),

}

class WalletError(val code: WalletErrorCode, override val message: String) : Throwable() {
    companion object {
        const val UNKNOWN_ERROR = "unknown error"
        const val NOT_ENOUGH_BALANCE = "not enough balance"
        const val INVALID_ADDRESS = "invalid wallet address"
        const val WRONG_PASSWORD = "invalid password"
        const val INVALID_WALLET_FORMAT = "invalid wallet format"
        const val INVALID_WALLET_VERSION = "invalid wallet verison"
        const val INVALID_ARGUMENT = "invalid argument"
        const val INVALID_RESPONSE = "invalid response from server"
        const val NO_RPC_SERVER = "RPC server address is not set"
        const val SERVER_ERROR = "error from server"
    }
}