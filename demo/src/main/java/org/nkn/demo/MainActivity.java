package org.nkn.demo;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import org.libsodium.jni.encoders.Hex;
import org.nkn.sdk.Wallet;

import org.nkn.sdk.crypto.*;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void onBtnSign(View view) {
//        EditText editText = (EditText) findViewById(R.id.editText);
//        int n = Integer.parseInt(editText.getText().toString());

        long startTime = System.nanoTime();

        Wallet wallet = Wallet.fromKeystore("{\"Version\":1,\"PasswordHash\":\"7cbfc5a7f08fe352f36e134628c6f1abde36a9346f0b3d207e335d688a9c123a\",\"MasterKey\":\"0b204ce157e1e0feff7b561a8992da8f5ac7b4a111134cf0caa392714dcf94a4\",\"IV\":\"518e14692fda73b79e8e4c9d4417edeb\",\"SeedEncrypted\":\"5589a09f9a34f63224f790e81774d52980a93b9f1036a69ebf9d3972f9c8a0df\",\"Address\":\"NKNJGjvEP9wPzogTfcrQcsQJqKcFjHtS8w5v\",\"ProgramHash\":\"4a7247caee28a90ac0854a67ae943c6adbcf391f\",\"ContractData\":\"222020e159dac75ef10624b541a7d579a37a8cbf7af77100f18897baf44d896508e5ac01004a7247caee28a90ac0854a67ae943c6adbcf391f\"}", "123456");

        System.out.println("=============="+wallet.verifyPassword("123456"));
        long consumingTime = System.nanoTime() - startTime;
        Toast.makeText(getApplicationContext(), "ts:" + (consumingTime / 1000000000.0), Toast.LENGTH_LONG).show();
    }

}
