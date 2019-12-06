package org.nkn.sdk


import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.nkn.sdk.network.RpcApi
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis

class RpcApiTest {
    @Test
    fun rpcApi_test() = runBlocking {
        var api = RpcApi()
        var res = api.request("getbalancebyaddr", mapOf("address" to "NKNVCZYpUk94xe3p3miNGSoQnkidQUfPMQxx"))
        println(res)

    }
}