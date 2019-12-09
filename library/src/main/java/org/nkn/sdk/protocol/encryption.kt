package org.nkn.sdk.protocol

import org.nkn.sdk.utils.*
import org.libsodium.jni.SodiumConstants
import org.nkn.sdk.pb.PayloadsProto

fun encryptPayloads(payload: ByteArray, dest: List<*>) {
    val nonce = Utils.randomBytes(SodiumConstants.NONCE_BYTES)
    val key = Utils.randomBytes(SodiumConstants.SECRETKEY_BYTES)
    val encryptedPayload = Utils.encrypt(payload, nonce, key)
    //todo

}

fun encryptPayload(payload: ByteArray, dest: String, sharedKey: ByteArray): PayloadsProto.Message {
    val nonce = Utils.randomBytes(SodiumConstants.NONCE_BYTES)
    val encrypted = Utils.encrypt(payload, nonce, sharedKey)
    return newMessage(encrypted, true, nonce)
}
