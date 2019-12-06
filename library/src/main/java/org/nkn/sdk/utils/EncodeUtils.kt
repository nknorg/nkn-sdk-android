package org.nkn.sdk.utils

fun encodeUint64(l: Long): ByteArray {
    return byteArrayOf(
        (l and 0xFF).toByte(),
        (l and 0xFF00 shr 8).toByte(),
        (l and 0xFF0000 shr 16).toByte(),
        (l and -0x1000000 shr 24).toByte(),
        (l and 0xFF00000000L shr 32).toByte(),
        (l and 0xFF0000000000L shr 40).toByte(),
        (l and 0xFF000000000000L shr 48).toByte(),
        (l and -0x100000000000000L shr 56).toByte()
    )

}

fun encodeUint32(i: Int): ByteArray {
    return byteArrayOf(
        (i and 0xFF).toByte(),
        (i and 0xFF00 shr 8).toByte(),
        (i and 0xFF0000 shr 16).toByte(),
        (i and -0x1000000 shr 24).toByte()
    )

}

fun encodeUint16(i: Int): ByteArray {
    return byteArrayOf(
        (i and 0xFF).toByte(),
        (i and 0xFF00 shr 8).toByte()
    )

}

fun encodeUint8(i: Int): ByteArray {
    return byteArrayOf(
        (i and 0xFF).toByte()
    )

}

fun encodeUint(n: Long): ByteArray {
    return if (n >= 0) {
        if (n < 0xfd) {
            encodeUint8(n.toInt())
        } else if (n <= 0xffff) {
            byteArrayOf(0xfd.toByte()) + encodeUint16(n.toInt())
        } else if (n <= 0xffffffffL) {
            byteArrayOf(0xfe.toByte()) + encodeUint32(n.toInt())
        } else {
            byteArrayOf(0xff.toByte()) + encodeUint64(n)
        }
    } else {
        byteArrayOf(0xff.toByte()) + encodeUint64(n)
    }
}

fun encodeBytes(value: ByteArray): ByteArray {
    return encodeUint(value.size.toLong()) + value
}

fun encodeString(value: ByteArray): ByteArray {
    return encodeUint(value.size.toLong()) + value
}

fun encodeBool(b: Boolean): ByteArray {
    return encodeUint8(if (b) 1 else 0)
}

fun signatureToParameter(signed: ByteArray): ByteArray {
    return byteArrayOf(signed.size.toByte()) + signed
}
