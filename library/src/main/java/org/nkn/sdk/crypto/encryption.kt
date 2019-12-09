package org.nkn.sdk.crypto

import org.libsodium.jni.crypto.SecretBox
import org.nkn.sdk.utils.Utils
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

fun genAESIV(): ByteArray {
    return Utils.randomBytes(16)
}

fun genAESPassword(): ByteArray {
    return Utils.randomBytes(32)
}

fun aesEncrypt(
    data: ByteArray,
    key: ByteArray,
    iv: ByteArray,
    isSimplePassword: Boolean = false
): ByteArray {
    var password = if (isSimplePassword) doubleSha256(key) else key
    var c = Cipher.getInstance("AES/CBC/NoPadding")
    c.init(Cipher.ENCRYPT_MODE, SecretKeySpec(password, "AES"), IvParameterSpec(iv))
    return c.doFinal(data)
}

fun aesDecrypt(
    data: ByteArray,
    key: ByteArray,
    iv: ByteArray,
    isSimplePassword: Boolean = false
): ByteArray {
    var password = if (isSimplePassword) doubleSha256(key) else key
    var c = Cipher.getInstance("AES/CBC/NoPadding")
    c.init(Cipher.DECRYPT_MODE, SecretKeySpec(password, "AES"), IvParameterSpec(iv))
    return c.doFinal(data)
}

fun decrypt(encrypted: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray {
    return SecretBox(key).decrypt(nonce, encrypted)
}

fun encrypt(message: ByteArray, nonce: ByteArray, key: ByteArray): ByteArray {
    return SecretBox(key).encrypt(nonce, message)
}