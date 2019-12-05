package org.nkn.sdk.crypto

import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import org.libsodium.jni.crypto.Random
import org.libsodium.jni.encoders.Encoder.HEX
import org.libsodium.jni.keys.SigningKey
import org.nkn.sdk.utils.Utils

const val SEED_SIZE: Int = 32
const val SECRET_KEY_SIZE: Int = 64
const val PUBLICKEY_SIZE: Int = 32

class Key {
    var seed: ByteArray
    val privateKey: ByteArray
    val publicKey: ByteArray

    val privateKeyHash: String
    val publicKeyHash: String
    val signatureRedeem: String
    val programHash: String
    private var signingKey: SigningKey

    constructor(seed: Any?) {
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
    }


    fun sign(message: ByteArray): ByteArray {
        return this.signingKey.sign(message)
    }

}