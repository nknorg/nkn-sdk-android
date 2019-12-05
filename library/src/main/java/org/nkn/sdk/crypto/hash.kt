package org.nkn.sdk.crypto

import org.bouncycastle.crypto.digests.RIPEMD160Digest
import org.libsodium.jni.encoders.Hex.HEX
import java.security.MessageDigest

fun sha256(raw: Any): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    var bytes: ByteArray = when (raw) {
        is ByteArray -> raw
        is String -> raw.toByteArray()
        else -> throw  IllegalArgumentException("raw must be ByteArray or String")
    }
    return md.digest(bytes)
}

fun doubleSha256(raw: Any): ByteArray {
    return sha256(sha256(raw))
}

fun sha256Hex(hex: Any): ByteArray {
    val md = MessageDigest.getInstance("SHA-256")
    var bytes: ByteArray = when (hex) {
        is ByteArray -> hex
        is String -> HEX.decode(hex)
        else -> throw  IllegalArgumentException("raw must be ByteArray or String")
    }
    return md.digest(bytes)
}

fun doubleSha256Hex(raw: Any): ByteArray {
    return sha256Hex(sha256Hex(raw))
}

fun ripemd160Hex(raw: Any): ByteArray {
    val r160Digest = RIPEMD160Digest()
    var bytes: ByteArray = when (raw) {
        is ByteArray -> raw
        is String -> HEX.decode(raw)
        else -> throw  IllegalArgumentException("raw must be ByteArray or String")
    }
    r160Digest.update(bytes, 0, bytes.size)
    var r160: ByteArray = ByteArray(r160Digest.digestSize)
    r160Digest.doFinal(r160, 0)
    return r160
}