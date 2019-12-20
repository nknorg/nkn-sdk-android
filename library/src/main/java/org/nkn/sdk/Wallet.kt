package org.nkn.sdk

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.nkn.sdk.crypto.*
import org.nkn.sdk.utils.Utils
import org.nkn.sdk.error.WalletError
import org.nkn.sdk.error.WalletErrorCode
import org.nkn.sdk.network.RpcApi
import org.nkn.sdk.transaction.newSubscribe
import org.nkn.sdk.transaction.newTransaction
import org.nkn.sdk.transaction.newTransfer
import org.nkn.sdk.transaction.newUnsubscribe

val rpcApi = RpcApi()

fun createPasswordHash(pass: String): ByteArray {
    return doubleSha256(pass)
}

class Wallet(
    val account: Account,
    password: String = "",
    prevMasterKey: String? = null,
    prevIV: String? = null
) {
    companion object {
        @JvmField
        val WALLET_VERSION = 1
        @JvmField
        val MIN_COMPATIBLE_WALLET_VERSION = 1
        @JvmField
        val MAX_COMPATIBLE_WALLET_VERSION = 1

        data class KeyStore(
            val Version: Int,
            val PasswordHash: String,
            val MasterKey: String,
            val IV: String,
            val SeedEncrypted: String,
            val Address: String,
            val ProgramHash: String,
            val ContractData: String
        ) {
            fun toJson(): String {
                return Gson().toJson(this)
            }
        }

        @JvmStatic
        fun createRandom(): Wallet {
            return Wallet()
        }

        @JvmOverloads
        @JvmStatic
        fun fromSeed(seed: String, password: String? = null): Wallet {
            var account = Account(seed)
            return Wallet(account, password ?: "")
        }

        @JvmStatic
        fun fromKeystore(keystore: String, pass: String): Wallet {
            val walletJson: KeyStore
            try {
                walletJson = Gson().fromJson<KeyStore>(keystore, KeyStore::class.java)
            } catch (e: JsonSyntaxException) {
                throw WalletError(
                    WalletErrorCode.INVALID_WALLET_FORMAT,
                    WalletError.INVALID_WALLET_FORMAT
                )
            }

            val version = walletJson.Version
            if (version < MIN_COMPATIBLE_WALLET_VERSION || version > MAX_COMPATIBLE_WALLET_VERSION) {
                throw WalletError(
                    WalletErrorCode.INVALID_WALLET_VERSION,
                    WalletError.INVALID_WALLET_VERSION
                )
            }
            val pwdHash = createPasswordHash(pass)
            if (walletJson.PasswordHash != Utils.hexEncode(sha256Hex(pwdHash))) {
                throw WalletError(WalletErrorCode.WRONG_PASSWORD, WalletError.WRONG_PASSWORD)
            }
            val decryptMasterKey = aesDecrypt(
                Utils.hexDecode(walletJson.MasterKey),
                pwdHash,
                Utils.hexDecode(walletJson.IV)
            )
            val seed = aesDecrypt(
                Utils.hexDecode(walletJson.SeedEncrypted),
                decryptMasterKey,
                Utils.hexDecode(walletJson.IV)
            )
            return Wallet(Account(seed), pass, Utils.hexEncode(decryptMasterKey), walletJson.IV)
        }

        @JvmStatic
        fun getBalanceByAddress(address: String): Double {
            val json = rpcApi.getBalanceByAddr(address)
            return json?.getDouble("amount") ?: 0.0
        }

        @JvmStatic
        fun getNonceByAddress(address: String): Long? {
            val json = rpcApi.getNonceByAddr(address)
            return json?.getLong("nonce")
        }


    }

    val seed: ByteArray
    val passwordHash: ByteArray
    val iv: ByteArray
    val masterKey: ByteArray
    val seedEncrypted: ByteArray
    val version: Int = WALLET_VERSION

    val keyStore: KeyStore

    init {
        this.seed = this.account.key.seed
        val masterKey =
            if (prevMasterKey != null) Utils.hexDecode(prevMasterKey) else genAESPassword()
        val pwdHash = createPasswordHash(password)
        this.passwordHash = sha256Hex(pwdHash)
        this.iv = if (prevIV != null) Utils.hexDecode(prevIV) else genAESIV()
        this.masterKey = aesEncrypt(masterKey, pwdHash, this.iv)
        this.seedEncrypted = aesEncrypt(this.seed, masterKey, this.iv)
        this.keyStore = KeyStore(
            this.version,
            Utils.hexEncode(this.passwordHash),
            Utils.hexEncode(this.masterKey),
            Utils.hexEncode(this.iv),
            Utils.hexEncode(this.seedEncrypted),
            this.account.address,
            this.account.key.programHash,
            this.account.contract
        )
    }

    val address: String = this.account.address
    val privateKey: ByteArray = this.account.key.privateKey
    val privateKeyHash: String = this.account.key.privateKeyHash
    val publicKey: ByteArray = this.account.key.publicKey
    val publicKeyHash: String = this.account.key.publicKeyHash
    val programHash: String = this.account.key.programHash
    val signatureRedeem: String = this.account.key.signatureRedeem
    val contractData: String = this.account.contract


    constructor() : this(Account())

    fun toJson(): String {
        return Gson().toJson(
            this.keyStore
        )
    }

    fun verifyPassword(pass: String): Boolean {
        val passwordHash = createPasswordHash(pass)
        return this.passwordHash.contentEquals(sha256Hex(passwordHash))
    }

    @JvmOverloads
    fun encrypt(pass: String? = null): String {
        if (pass == null) {
            return this.toJson()
        } else {
            val wallet = fromSeed(Utils.hexEncode(this.seed), pass)
            return wallet.toJson()
        }
    }

    fun getBalance(): Double {
        return getBalanceByAddress(this.address)
    }

    fun getNonce(): Long? {
        return getNonceByAddress(this.address)
    }

    @JvmOverloads
    fun transferTo(
        toAddress: String,
        amount: Double,
        fee: Double = 0.0,
        nonce: Long? = null,
        attrs: String = ""
    ): String? {
        if (!Utils.verifyAddress(toAddress)) {
            throw WalletError(WalletErrorCode.INVALID_ADDRESS, WalletError.INVALID_ADDRESS)
        }
        val balance = this.getBalance()
        if (balance < amount) {
            throw WalletError(WalletErrorCode.NOT_ENOUGH_BALANCE, WalletError.NOT_ENOUGH_BALANCE)
        }

        val nextNonce = nonce ?: this.getNonce() ?: 0
        val pld = newTransfer(
            this.programHash,
            Utils.addressStringToProgramHash(toAddress),
            amount
        )

        val txn = newTransaction(this.account, pld, nextNonce, fee, attrs)
        return rpcApi.sendRawTransaction(Utils.hexEncode(txn.toByteArray()))
    }

    @JvmOverloads
    fun subscribe(
        topic: String,
        duration: Int,
        identifier: String? = "",
        meta: String? = "",
        nonce: Long? = null,
        fee: Double? = 0.0,
        attrs: String? = ""
    ): String? {
        val nextNonce = nonce ?: this.getNonce() ?: 0
        val pld = newSubscribe(this.publicKeyHash, identifier ?: "", topic, duration, meta ?: "")
        val txn = newTransaction(this.account, pld, nextNonce, fee ?: 0.0, attrs ?: "")
        return rpcApi.sendRawTransaction(Utils.hexEncode(txn.toByteArray()))
    }

    @JvmOverloads
    fun unsubscribe(
        topic: String,
        identifier: String? = "",
        nonce: Long? = null,
        fee: Double? = 0.0,
        attrs: String? = ""
    ): String? {
        val nextNonce = nonce ?: this.getNonce() ?: 0
        val pld = newUnsubscribe(this.publicKeyHash, identifier ?: "", topic)
        val txn = newTransaction(this.account, pld, nextNonce, fee ?: 0.0, attrs ?: "")
        return rpcApi.sendRawTransaction(Utils.hexEncode(txn.toByteArray()))
    }

}