package org.nkn.sdk.transaction

import com.google.protobuf.ByteString
import org.nkn.sdk.configure.NKN_ACC_MUL
import org.nkn.sdk.pb.TransactionProto
import org.nkn.sdk.pb.TransactionProto.TransferAsset
import org.nkn.sdk.utils.Utils
import org.nkn.sdk.utils.*

fun newTransfer(sender: String, recipient: String, amount: Double): TransactionProto.Payload {
    val coin = (amount * NKN_ACC_MUL).toLong()
    val transfer = TransferAsset.newBuilder()
    transfer.sender = ByteString.copyFrom(Utils.hexDecode(sender))
    transfer.recipient = ByteString.copyFrom(Utils.hexDecode(recipient))
    transfer.amount = coin
    val pld = TransactionProto.Payload.newBuilder()
    pld.type = TransactionProto.PayloadType.TRANSFER_ASSET_TYPE
    pld.data = transfer.build().toByteString()
    return pld.build()
}

fun newSubscribe(
    subscriber: String, identifier: String, topic: String,
    duration: Int, meta: String
): TransactionProto.Payload {
    val subscribe = TransactionProto.Subscribe.newBuilder()
    subscribe.subscriber = ByteString.copyFrom(Utils.hexDecode(subscriber))
    subscribe.identifier = identifier
    subscribe.topic = topic
    subscribe.duration = duration
    subscribe.meta = meta

    val pld = TransactionProto.Payload.newBuilder()
    pld.type = TransactionProto.PayloadType.SUBSCRIBE_TYPE
    pld.data = subscribe.build().toByteString()
    return pld.build()
}

fun newUnsubscribe(
    subscriber: String,
    identifier: String,
    topic: String
): TransactionProto.Payload {
    val unsubscribe = TransactionProto.Unsubscribe.newBuilder()
    unsubscribe.subscriber = ByteString.copyFrom(Utils.hexDecode(subscriber))
    unsubscribe.identifier = identifier
    unsubscribe.topic = topic

    val pld = TransactionProto.Payload.newBuilder()
    pld.type = TransactionProto.PayloadType.UNSUBSCRIBE_TYPE
    pld.data = unsubscribe.build().toByteString()
    return pld.build()
}

fun serializePayload(payload: TransactionProto.Payload): ByteArray {
    return encodeUint32(payload.typeValue) + encodeBytes(payload.data.toByteArray())
}