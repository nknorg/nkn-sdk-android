package org.nkn.sdk.crypto

import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import org.libsodium.jni.crypto.Random
import org.libsodium.jni.encoders.Encoder.HEX
import org.libsodium.jni.keys.SigningKey
import org.nkn.sdk.cache.sharedKeyCache
import org.nkn.sdk.const.PUBLICKEY_SIZE
import org.nkn.sdk.const.SECRET_KEY_SIZE
import org.nkn.sdk.const.SEED_SIZE
import org.nkn.sdk.utils.Utils


class Key(seed: Any?) {
    var seed: ByteArray
    val privateKey: ByteArray
    val publicKey: ByteArray

    val privateKeyHash: String
    val publicKeyHash: String
    val signatureRedeem: String
    val programHash: String
    val curveSecretKey: ByteArray
    private var signingKey: SigningKey

    init {
        NaCl.sodium()
        if (seed is ByteArray) {
            Utils.checkLength(seed, SEED_SIZE)
            this.seed = seed
        } else if (seed is String) {
            this.seed = HEX.decode(seed)
            Utils.checkLength(this.seed, SEED_SIZE)
        } else {
            this.seed = Random().randomBytes()
        }
        this.privateKey = ByteArray(SECRET_KEY_SIZE)
        this.publicKey = ByteArray(PUBLICKEY_SIZE)
        Sodium.crypto_sign_ed25519_seed_keypair(this.publicKey, this.privateKey, this.seed)
        this.privateKeyHash = HEX.encode(privateKey)
        this.publicKeyHash = HEX.encode(publicKey)
        this.signatureRedeem = Utils.publicKeyToSignatureRedeem(publicKeyHash)
        this.programHash = Utils.hexStringToProgramHash(signatureRedeem)
        this.signingKey = SigningKey(this.seed)
        curveSecretKey = Utils.convertSecretKey(this.privateKey)
    }

    fun getCacheSharedKeyByTargetPubKey(pk: ByteArray): ByteArray {
        var sharedKey = sharedKeyCache.get(pk)
        if (sharedKey == null) {
            sharedKey = Utils.computeSharedKey(this.curveSecretKey, Utils.convertPublicKey(pk))
            sharedKeyCache.put(pk, sharedKey)
        }
        return sharedKey
    }

    fun sign(message: ByteArray): ByteArray {
        return this.signingKey.sign(message)
    }

    fun encrypt(message: ByteArray, nonce: ByteArray, otherPubKey: ByteArray): ByteArray? {
        return Utils.encrypt(message, nonce, getCacheSharedKeyByTargetPubKey(otherPubKey))
    }

    fun decrypt(encryptedMessage: ByteArray, nonce: ByteArray, otherPubKey: ByteArray): ByteArray? {
        return Utils.decrypt(encryptedMessage, nonce, getCacheSharedKeyByTargetPubKey(otherPubKey))
    }
}