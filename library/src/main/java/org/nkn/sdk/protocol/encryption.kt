package org.nkn.sdk.protocol

import org.nkn.sdk.const.KEY_SIZE
import org.nkn.sdk.const.NONCE_SIZE
import org.nkn.sdk.const.SECRETBOX_NONCE_SIZE
import org.nkn.sdk.crypto.Key
import org.nkn.sdk.pb.PayloadsProto
import org.nkn.sdk.utils.Utils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.util.zip.DataFormatException
import java.util.zip.Deflater
import java.util.zip.Inflater


fun encryptPayloads(payload: ByteArray, dests: Array<String>, key: Key): Array<PayloadsProto.Message> {
    val nonce = Utils.randomBytes(NONCE_SIZE)
    val k = Utils.randomBytes(KEY_SIZE)
    val encryptedPayload = Utils.encrypt(payload, nonce, k) ?: throw Throwable("Encrypted payload failed.")
    val msgs = ArrayList<PayloadsProto.Message>()
    for (i in dests.indices) {
        val msgNonce = Utils.randomBytes(NONCE_SIZE)
        val sharedKey = key.getCacheSharedKeyByTargetPubKey(Utils.getPublicKeyByClientAddr(dests[i]))
        val encryptedMessage = Utils.encrypt(k, msgNonce, sharedKey)
        val mergedNonce = msgNonce + nonce
        val msg = newMessage(encryptedPayload, true, mergedNonce, encryptedMessage)
        msgs.add(msg)
    }
    return msgs.toTypedArray()
}

fun encryptPayload(payload: ByteArray, dest: String, key: Key): PayloadsProto.Message {
    val sharedKey = key.getCacheSharedKeyByTargetPubKey(Utils.getPublicKeyByClientAddr(dest))
    val nonce = Utils.randomBytes(NONCE_SIZE)
    val encrypted = Utils.encrypt(payload, nonce, sharedKey) ?: throw Throwable("Encrypted payload failed.")
    return newMessage(encrypted, true, nonce)
}

fun decryptPayload(msg: PayloadsProto.Message, srcPubKey: ByteArray, key: Key): ByteArray {
    val rawPayload = msg.payload.toByteArray()
    val nonce = msg.nonce.toByteArray()
    val encryptedKey = msg.encryptedKey.toByteArray()
    val decryptedPayload: ByteArray = if (encryptedKey != null && encryptedKey.isNotEmpty()) {
        if (nonce.size != NONCE_SIZE + SECRETBOX_NONCE_SIZE) {
            throw Throwable("Invalid nonce length.")
        }
        val sharedKey = key.decrypt(encryptedKey, nonce.sliceArray(0 until NONCE_SIZE), srcPubKey)
            ?: throw Throwable("Decrypt shared key failed.")
        Utils.decrypt(rawPayload, nonce.sliceArray( NONCE_SIZE until nonce.size), sharedKey)
            ?: throw Throwable("Decrypt message failed.")
    } else {
        if (nonce.size != NONCE_SIZE) {
            throw Throwable("Invalid nonce length.")
        }
        key.decrypt(rawPayload, nonce, srcPubKey)
            ?: throw Throwable("Decrypt message failed.")
    }
    return decryptedPayload
}

@Throws(IOException::class)
fun compress(data: ByteArray): ByteArray? {
    val deflater = Deflater()
    deflater.setInput(data)
    val outputStream = ByteArrayOutputStream(data.size)
    deflater.finish()
    val buffer = ByteArray(4096)
    while (!deflater.finished()) {
        val count: Int = deflater.deflate(buffer) // returns the generated code... index
        outputStream.write(buffer, 0, count)
    }
    outputStream.close()
    val output: ByteArray = outputStream.toByteArray()
    return output
}

@Throws(IOException::class, DataFormatException::class)
fun decompress(data: ByteArray): ByteArray? {
    val inflater = Inflater()
    inflater.setInput(data)
    val outputStream = ByteArrayOutputStream(data.size)
    val buffer = ByteArray(4096)
    while (!inflater.finished()) {
        val count: Int = inflater.inflate(buffer)
        outputStream.write(buffer, 0, count)
    }
    outputStream.close()
    val output = outputStream.toByteArray()
    return output
}