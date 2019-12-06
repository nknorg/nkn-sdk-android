package org.nkn.sdk.network

import okhttp3.*
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import org.nkn.sdk.configure.RPC_ADDR
import org.nkn.sdk.error.RpcError
import org.nkn.sdk.error.RpcErrorCode
import kotlinx.coroutines.*
import org.json.JSONTokener

class RpcApi(val rpcAddr: String? = null) {
    constructor() : this(RPC_ADDR)

    private val client = OkHttpClient()
    private val params = mapOf("jsonrpc" to "2.0", "id" to "nkn-sdk-android")

    fun request(method: String, params: Map<String, Any>): JSONObject? {
        val url = this.rpcAddr
        val body = mutableMapOf<String, Any?>()
        body.putAll(this.params)
        body["method"] = method
        body["params"] = params

        val request = Request.Builder()
            .url(url!!)
            .post(JSONObject(body.toMap()).toString().toRequestBody())
            .build()

        val res = client.newCall(request).execute()

        if (res.code != 200) {
            throw RpcError(RpcErrorCode.UNKNOWN_ERROR, RpcError.UNKNOWN_ERROR)
        }
        val json = JSONObject(res.body!!.string())

        if (json.has("error"))
            return json.getJSONObject("error")
        return json
    }

    fun getBalanceByAddr(address: String): JSONObject? {
        return this.request("getbalancebyaddr", mapOf("address" to address))
            ?.getJSONObject("result")
    }

    fun getNonceByAddr(address: String): JSONObject? {
        return this.request("getnoncebyaddr", mapOf("address" to address))?.getJSONObject("result")
    }

    fun getBlockCount(name: String): JSONObject? {
        return this.request("getblockcount", mapOf("name" to name))?.getJSONObject("result")
    }

    fun getAddressByName(name: String): JSONObject? {
        return this.request("getaddressbyname", mapOf("name" to name))?.getJSONObject("result")
    }

    fun sendRawTransaction(tx: String): String? {
        return this.request("sendrawtransaction", mapOf("tx" to tx))?.getString("result")
    }

    fun getWsAddr(address: String): JSONObject? {
        return this.request("getwsaddr", mapOf("address" to address))?.getJSONObject("result")
    }

    fun getSubscribers(
        topic: String,
        offset: Int = 0,
        limit: Int = 0,
        meta: Boolean = false,
        txPool: Boolean = false
    ): JSONObject? {
        return this.request(
            "getsubscribers",
            mapOf(
                "topic" to topic,
                "offset" to offset,
                "limit" to limit,
                "meta" to meta,
                "txPool" to txPool
            )
        )?.getJSONObject("result")
    }

    fun getSubscribersCount(topic: String): JSONObject? {
        return this.request("getsubscriberscount", mapOf("topic" to topic))?.getJSONObject("result")
    }

    fun getSubscription(topic: String, subscriber: String): JSONObject? {
        return this.request("getsubscription", mapOf("topic" to topic, "subscriber" to subscriber))
            ?.getJSONObject("result")
    }
}