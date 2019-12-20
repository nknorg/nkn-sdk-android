package org.nkn.sdk


import android.util.LruCache
import org.json.JSONObject
import org.junit.Assert
import org.junit.Test
import org.nkn.sdk.network.RpcApi
import kotlinx.coroutines.*
import kotlin.system.measureTimeMillis
import org.nkn.sdk.utils.*
import org.nkn.sdk.Wallet
import org.nkn.sdk.Client
import org.nkn.sdk.ClientListener
import org.nkn.sdk.utils.Utils

class WalletTest {
    @Test
    fun transfer_test() = runBlocking {
        val wallet = Wallet.createRandom()
        val client = Client(Utils.hexEncode(wallet.seed), "identifier")
    }
}