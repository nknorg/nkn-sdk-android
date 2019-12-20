package org.nkn.sdk.transaction

import com.google.protobuf.ByteString
import org.nkn.sdk.configure.NKN_ACC_MUL
import org.nkn.sdk.crypto.Account
import org.nkn.sdk.crypto.sha256Hex
import org.nkn.sdk.pb.TransactionProto
import org.nkn.sdk.utils.*

fun serializeUnsignedTx(unsignedTx: TransactionProto.UnsignedTx): ByteArray {
    return serializePayload(unsignedTx.payload) + encodeUint64(unsignedTx.nonce) +encodeUint64(unsignedTx.fee) + encodeBytes(unsignedTx.attributes.toByteArray())
}

fun signTx(account: Account, txn: TransactionProto.Transaction.Builder) {
    val unsignedTx = txn.unsignedTx
    val hex = serializeUnsignedTx(unsignedTx)
    val digest = sha256Hex(hex)
    val signature = account.key.sign(digest)
    val prgm = TransactionProto.Program.newBuilder()
    prgm.code = ByteString.copyFrom(Utils.hexDecode(account.signatureRedeem))
    prgm.parameter = ByteString.copyFrom(signatureToParameter(signature))
    txn.addPrograms(prgm.build())
}

fun newTransaction(
    account: Account,
    pld: TransactionProto.Payload,
    nonce: Long,
    fee: Double = 0.0,
    attrs: String = ""
): TransactionProto.Transaction {

    val feeValue = (fee * NKN_ACC_MUL).toLong()

    val unsignedTx = TransactionProto.UnsignedTx.newBuilder()
    unsignedTx.payload = pld
    unsignedTx.nonce = nonce
    unsignedTx.fee = feeValue

    if (attrs.isNotEmpty()) unsignedTx.attributes = ByteString.copyFrom(Utils.hexDecode(attrs))

    val txn = TransactionProto.Transaction.newBuilder()
    txn.unsignedTx = unsignedTx.build()
    signTx(account, txn)
    return txn.build()
}
