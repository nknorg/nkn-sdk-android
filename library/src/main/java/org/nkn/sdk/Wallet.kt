package org.nkn.sdk

import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import org.nkn.sdk.crypto.*
import org.nkn.sdk.utils.Utils
import org.nkn.sdk.error.*
import org.nkn.sdk.error.WalletError
import org.nkn.sdk.error.WalletErrorCode
import java.util.*

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
                throw WalletError(WalletErrorCode.INVALID_WALLET_FORMAT, INVALID_WALLET_FORMAT)
            }

            val version = walletJson.Version
            if (version < MIN_COMPATIBLE_WALLET_VERSION || version > MAX_COMPATIBLE_WALLET_VERSION) {
                throw WalletError(WalletErrorCode.INVALID_WALLET_VERSION, INVALID_WALLET_VERSION)
            }
            val pwdHash = createPasswordHash(pass)
            if (walletJson.PasswordHash != Utils.hexEncode(sha256Hex(pwdHash))) {
                throw WalletError(WalletErrorCode.WRONG_PASSWORD, WRONG_PASSWORD)
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
    }

    val seed: ByteArray
    val passwordHash: ByteArray
    val iv: ByteArray
    val masterKey: ByteArray
    var seedEncrypted: ByteArray
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


}