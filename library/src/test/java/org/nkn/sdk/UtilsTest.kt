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
    fun getPublicKeyByClientAddr_test() {
        var res = Utils.getPublicKeyByClientAddr("heron.25ac590eaca614a0ba4c4387d8514a0b54e948d120c6ff49564e7830c9dec929")
        Assert.assertEquals(Utils.hexEncode(res), "25ac590eaca614a0ba4c4387d8514a0b54e948d120c6ff49564e7830c9dec929")
    }
}