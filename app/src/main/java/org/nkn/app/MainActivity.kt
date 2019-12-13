package org.nkn.app

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.nkn.sdk.Client
import org.nkn.sdk.Wallet
import org.nkn.sdk.cache.sharedKeyCache
import org.nkn.sdk.crypto.Key
import org.nkn.sdk.network.RpcApi
import org.nkn.sdk.network.WsApi
import org.nkn.sdk.utils.Utils

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)
        var client = Client(
            "32e677190540797d3252e09bea9a03d2e286a74ad54bffcba6378481c6e3e9cd",
            "nkn"
        )
        client.connect()

        fab.setOnClickListener { view ->
            GlobalScope.launch {
//                client.send("nkn.8488c5ee3010ec45989ffcbf5c74283e23d0f18c4f8e9ea2f6adb1a942dc8d74", """{"contentType":"text","isPrivate":true,"content":"hello"}""")
                Snackbar.make(view, "res: ", Snackbar.LENGTH_LONG).show()
            }
        }


    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }
}
