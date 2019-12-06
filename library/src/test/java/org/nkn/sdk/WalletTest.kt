package org.nkn.sdk


import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.nkn.sdk.network.RpcApi
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import org.nkn.sdk.utils.*

class WalletTest {
    @Test
    fun transfer_test() = runBlocking {
        println(Utils.hexEncode(encodeUint8(0)))
    }
}