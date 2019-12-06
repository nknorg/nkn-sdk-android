package org.nkn.sdk


import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.nkn.sdk.network.RpcApi
import kotlinx.coroutines.*
import org.nkn.sdk.pb.TransactionProto
import kotlin.system.measureTimeMillis
import org.nkn.sdk.utils.*

class UtilsTest {
    @Test
    fun encodeUint_test() {
        Assert.assertEquals(Utils.hexEncode(encodeUint8(0)), "00")
        Assert.assertEquals(Utils.hexEncode(encodeUint8(44)), "2c")
        Assert.assertEquals(Utils.hexEncode(encodeUint8(55)), "37")
        Assert.assertEquals(Utils.hexEncode(encodeUint8(1)), "01")

        Assert.assertEquals(Utils.hexEncode(encodeUint64(0)), "0000000000000000")
        Assert.assertEquals(Utils.hexEncode(encodeUint64(44)), "2c00000000000000")
        Assert.assertEquals(Utils.hexEncode(encodeUint64(55)), "3700000000000000")
        Assert.assertEquals(Utils.hexEncode(encodeUint64(1)), "0100000000000000")
    }

    @Test
    fun test() {
        val payload = TransactionProto.Payload.newBuilder()
        payload.type = TransactionProto.PayloadType.TRANSFER_ASSET_TYPE
        println( payload.typeValue)
    }
}