package org.nkn.sdk.protocol

import com.google.protobuf.ByteString
import org.nkn.sdk.crypto.Key
import org.nkn.sdk.crypto.sha256
import org.nkn.sdk.crypto.sha256Hex
import org.nkn.sdk.pb.ClientMessageProto
import org.nkn.sdk.pb.PayloadsProto
import org.nkn.sdk.pb.SigChainProto
import org.nkn.sdk.utils.*

const val PID_SIZE = 8

fun serializeSigChainMetadata(sigChain: SigChainProto.SigChain): ByteArray {
    return encodeUint32(sigChain.nonce) +
            encodeUint32(sigChain.dataSize) +
            encodeBytes(sigChain.blockHash.toByteArray()) +
            encodeBytes(sigChain.srcId.toByteArray()) +
            encodeBytes(sigChain.srcPubkey.toByteArray()) +
            encodeBytes(sigChain.destId.toByteArray()) +
            encodeBytes(sigChain.destPubkey.toByteArray())
}

fun serializeSigChainElem(sigChainElem: SigChainProto.SigChainElem): ByteArray {
    return encodeBytes(sigChainElem.id.toByteArray()) + encodeBytes(sigChainElem.nextPubkey.toByteArray()) + encodeBool(sigChainElem.mining)
}

fun newPayload(type: PayloadsProto.PayloadType, replyToPid: ByteArray?, data: ByteArray?, msgPid: ByteArray?): PayloadsProto.Payload {
    val payload = PayloadsProto.Payload.newBuilder()
    payload.type = type
    if (replyToPid != null) {
        payload.replyToPid = ByteString.copyFrom(replyToPid)
    } else if (msgPid != null) {
        payload.pid = ByteString.copyFrom(msgPid)
    } else {
        payload.pid = ByteString.copyFrom(Utils.randomBytes(PID_SIZE))
    }

    if (data != null) payload.data = ByteString.copyFrom(data)
    return payload.build()
}

fun newClientMessage(messageType: ClientMessageProto.ClientMessageType, message: ByteArray, compressionType: ClientMessageProto.CompressionType): ClientMessageProto.ClientMessage {
    val msg = ClientMessageProto.ClientMessage.newBuilder()
    msg.messageType = messageType
    msg.compressionType = compressionType
    msg.message = when (compressionType) {
        ClientMessageProto.CompressionType.COMPRESSION_ZLIB -> {
            //todo zlib
            throw Throwable("todo zlib")
        }
        ClientMessageProto.CompressionType.COMPRESSION_NONE -> ByteString.copyFrom(message)
        else -> throw Throwable("unknown compression type $compressionType")
    }

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
    return newClientMessage(ClientMessageProto.ClientMessageType.RECEIPT, msg.build().toByteArray(), ClientMessageProto.CompressionType.COMPRESSION_NONE)
}

fun newMessage(payload: ByteArray, encrypted: Boolean, nonce: ByteArray? = null): PayloadsProto.Message {
    val msg = PayloadsProto.Message.newBuilder()
    msg.payload = ByteString.copyFrom(payload)
    msg.encrypted = encrypted
    if (encrypted) {
        msg.nonce = ByteString.copyFrom(nonce)
    }
    return msg.build()
}

fun newAckPayload(replyToPid: ByteArray, msgPid: ByteArray?): PayloadsProto.Payload {
    return newPayload(PayloadsProto.PayloadType.ACK, replyToPid, null, msgPid)
}

fun newBinaryPayload(data: ByteArray, replyToPid: ByteArray?, msgPid: ByteArray?): PayloadsProto.Payload {
    return newPayload(PayloadsProto.PayloadType.BINARY, replyToPid, data, msgPid)
}

fun newTextPayload(text: String, replyToPid: ByteArray?, msgPid: ByteArray?): PayloadsProto.Payload {
    val data = PayloadsProto.TextData.newBuilder()
    data.text = text
    return newPayload(PayloadsProto.PayloadType.TEXT, replyToPid, data.build().toByteArray(), msgPid)
}

fun newOutboundMessage(
    dest: Any,
    payload: Any,
    maxHoldingSeconds: Int,
    srcAddr: String,
    key: Key,
    pubkey: ByteArray,
    sigChainBlockHash: String? = null
): ClientMessageProto.ClientMessage {
    var dests: Array<String>? = null
    if (dest is String) {
        dests = arrayOf(dest)
    } else if (dest !is Array<*>) {
        throw Throwable("dest type must be String or Array<String>")
    }

    if (dests.isNullOrEmpty()) {
        throw Throwable("no destination")
    }

    var payloads: Array<ByteArray>? = null
    if (payload is ByteArray) {
        payloads = arrayOf(payload)
    } else if (payload !is Array<*>) {
        throw Throwable("payload type must be String or Array<ByteArray>")
    }
    if (payloads.isNullOrEmpty()) {
        throw Throwable("no payloads")
    }
    if (payloads.size > 1 && payloads.size != dests.size) {
        throw Throwable("invalid payload count")
    }


    val sigChainElem = SigChainProto.SigChainElem.newBuilder()
    sigChainElem.nextPubkey = ByteString.copyFrom(pubkey)
    var sigChainElemSerialized = serializeSigChainElem(sigChainElem.build())

    val sigChain = SigChainProto.SigChain.newBuilder()
    sigChain.nonce = Utils.randomInt32()
    sigChain.dataSize = payloads.size
    if (sigChainBlockHash != null) {
        sigChain.blockHash = ByteString.copyFrom(Utils.hexDecode(sigChainBlockHash))
    }
    sigChain.srcId = ByteString.copyFrom(addr2Id(srcAddr))
    sigChain.srcPubkey = ByteString.copyFrom(key.publicKey)

    var signatures: ArrayList<ByteArray> = ArrayList()
    for (i in dests.indices) {
        sigChain.destId = ByteString.copyFrom(addr2Id(dests[i]))
        sigChain.destPubkey = ByteString.copyFrom(Utils.getPublicKeyByClientAddr(dests[i]))
        if (!payloads.isNullOrEmpty()) {
            sigChain.dataSize = payloads[i].size
        } else {
            sigChain.dataSize = payloads[0].size
        }
        val hex = serializeSigChainMetadata(sigChain.build())
        val digest = sha256Hex(sha256Hex(hex) + sigChainElemSerialized)
        val signature = key.sign(digest)
        signatures.add(signature)
    }

    val obMsg = ClientMessageProto.OutboundMessage.newBuilder()
    obMsg.addAllDests(dests.toList())
    obMsg.addAllPayloads(payloads.map { item -> ByteString.copyFrom(item) }.toList())
    obMsg.maxHoldingSeconds = maxHoldingSeconds
    obMsg.nonce = sigChain.nonce
    obMsg.blockHash = sigChain.blockHash
    obMsg.addAllSignatures(signatures.map { item -> ByteString.copyFrom(item) }.toList())

    val compressionType = if (payloads.size > 1) ClientMessageProto.CompressionType.COMPRESSION_ZLIB else ClientMessageProto.CompressionType.COMPRESSION_NONE

    return newClientMessage(ClientMessageProto.ClientMessageType.OUTBOUND_MESSAGE, obMsg.build().toByteArray(), compressionType)
}

fun addr2Id(addr: String): ByteArray {
    return sha256(addr)
}