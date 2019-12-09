package org.nkn.app

import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem

import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.nkn.sdk.Client
import org.nkn.sdk.Wallet
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
            Utils.hexEncode(Wallet.createRandom().seed),
            "nkn"
        )
        client.connect()

        fab.setOnClickListener { view ->
            GlobalScope.launch {
                client.send("heron.25ac590eaca614a0ba4c4387d8514a0b54e948d120c6ff49564e7830c9dec929", """{"contentType":"text","isPrivate":true,"content":"hello"}""")
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
