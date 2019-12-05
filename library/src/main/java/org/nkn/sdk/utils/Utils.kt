package org.nkn.sdk.utils

import okhttp3.internal.toHexString
import org.libsodium.jni.crypto.Random
import org.libsodium.jni.encoders.Hex.HEX
import org.nkn.sdk.crypto.*


const val ADDRESS_GEN_PREFIX = "02b825"
const val ADDRESS_GEN_PREFIX_LEN: Int = ADDRESS_GEN_PREFIX.length / 2
const val UINT160_LEN = 20
const val CHECKSUM_LEN = 4
const val ADDRESS_LEN = ADDRESS_GEN_PREFIX_LEN + UINT160_LEN + CHECKSUM_LEN
const val SEED_LENGTH = 32
const val MAX_UINT_BITS = 48
const val MAX_UINT = 281474976710656


class Utils {
    companion object {
        @JvmOverloads
        @JvmStatic
        fun randomByte(len: Int = SEED_LENGTH): ByteArray {
            return Random().randomBytes(len)
        }

        @JvmStatic
        fun randomInt32(): Int {
            val b: ByteArray = randomByte(4)
            b[0] = (b[0].toInt() and 127).toByte()
            return (b[0].toInt() shl 24) + (b[1].toInt() shl 16) + (b[2].toInt() shl 8) + b[3]
        }

        @JvmStatic
        fun checkLength(data: ByteArray?, size: Int) {
            if (data == null || data.size != size) throw RuntimeException("Invalid size: " + data?.size)
        }

        @JvmStatic
        fun hexEncode(raw: ByteArray): String {
            return HEX.encode(raw)
        }

        @JvmStatic
        fun hexDecode(data: String): ByteArray {
            return HEX.decode(data)
        }

        @JvmStatic
        fun publicKeyToSignatureRedeem(publicKey: String): String {
            return UINT160_LEN.toString() + publicKey + "ac"
        }

        @JvmStatic
        fun hexStringToProgramHash(hex: String): String {
            return hexEncode(ripemd160Hex(sha256Hex(hex)))
        }

        @JvmStatic
        fun genAddressVerifyBytesFromProgramHash(programHash: String): ByteArray {
            var verifyBytes = doubleSha256Hex(ADDRESS_GEN_PREFIX + programHash)
            return verifyBytes.slice(0 until CHECKSUM_LEN).toByteArray()
        }

        @JvmStatic
        fun programHashStringToAddress(programHash: String): String {
            var addressVerifyBytes = genAddressVerifyBytesFromProgramHash(programHash)
            var addressBaseData = hexDecode(ADDRESS_GEN_PREFIX + programHash)
            return Base58.encode(addressBaseData + addressVerifyBytes)
        }

        @JvmStatic
        fun prefixByteCountToHexString(hexStr: String): String {
            var len = hexStr.length
            if (0 == len) {
                return "00"
            }
            var res = hexStr
            if (1 == len % 2) {
                res = "0$res"
                len += 1
            }

            var byteCount = (len / 2).toHexString()

            if (1 == byteCount.length % 2) {
                byteCount = "0$byteCount"
            }

            return byteCount + res
        }

        @JvmStatic
        fun genAccountContractString(signatureRedeem: String, programHash: String): String {
            var contract = ""
            contract += prefixByteCountToHexString(signatureRedeem)
            contract += prefixByteCountToHexString("00")
            contract += programHash
            return contract
        }
    }
}





