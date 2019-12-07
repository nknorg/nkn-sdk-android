package org.nkn.sdk.protocol

import com.google.protobuf.ByteString
import org.nkn.sdk.crypto.Key
import org.nkn.sdk.crypto.sha256Hex
import org.nkn.sdk.pb.ClientMessageProto
import org.nkn.sdk.pb.SigChainProto
import org.nkn.sdk.utils.*

fun serializeSigChainElem(sigChainElem: SigChainProto.SigChainElem): ByteArray {
    return encodeBytes(sigChainElem.id.toByteArray()) + encodeBytes(sigChainElem.nextPubkey.toByteArray()) + encodeBool(sigChainElem.mining)
}

fun newClientMessage(messageType: ClientMessageProto.ClientMessageType, message: ByteArray): ClientMessageProto.ClientMessage {
    val msg = ClientMessageProto.ClientMessage.newBuilder()
    msg.messageType = messageType
    msg.message = ByteString.copyFrom(message)
    return msg.build()
}

fun newReceipt(prevSignature: ByteArray, key: Key): ClientMessageProto.ClientMessage {
    val sigChainElem = SigChainProto.SigChainElem.newBuilder()
    val sigChainElemSerialized = serializeSigChainElem(sigChainElem.build())
    val digest = sha256Hex(sha256Hex(prevSignature) + sigChainElemSerialized)
    val signature = key.sign(digest)
    val msg = ClientMessageProto.Receipt.newBuilder()
    msg.prevSignature = ByteString.copyFrom(prevSignature)
    msg.signature = ByteString.copyFrom(signature)
    return newClientMessage(ClientMessageProto.ClientMessageType.RECEIPT, msg.build().toByteArray())

}