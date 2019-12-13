# nkn-sdk-android

## Install
### 1.download nkn-sdk aar
> download aar [https://github.com/nknorg/nkn-sdk-android/releases](https://github.com/nknorg/nkn-sdk-android/releases)

or

> Add library to dependencies:
```gradle
// build.gradle
allprojects {
  repositories {
      maven { url 'https://jitpack.io' }
  }
}
...
dependencies {
  ...
  implementation 'com.github.nknorg:nkn-sdk-android:VERSION'
}
```

### 2. add libsodium-jni-aar to dependencies:
```gradle
// build.gradle
dependencies {
   ...
   implementation 'com.github.joshjdevl.libsodiumjni:libsodium-jni-aar:2.0.1'
}
```

### 3. To fix the warning [allowBackup](src/main/AndroidManifest.xml), add `xmlns:tools="http://schemas.android.com/tools"` and `tools:replace="android:allowBackup"` to your Manifest:
```xml
<!-- AndroidManifest.xml -->
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
         xmlns:tools="http://schemas.android.com/tools"
         package="com.name.myapp">
   <application
           ...
           tools:replace="android:allowBackup">
       <activity android:name=".MainActivity">
           ...
       </activity>
   </application>
</manifest>
```

### Usage
#### Kotlin
```kotlin
import org.nkn.sdk.Wallet
import org.nkn.sdk.Client

// wallet
// creat new NKN Wallet
val wallet = Wallet.createRandom() 
// get NKN Wallet balance
val balance = wallet.getBalance()
// get nonce
val nonce = wallet.getNonce()
// encrypt a keystore with password
val keystore = wallet.encrypt("PASSWORD")
// load wallet from keystore 
val wallet = Wallet.fromKeystore("{KEYSTORE}", "PASSWORD")
// load wallet from seed
val wallet = Wallet.fromSeed("SEED", ["PASSWORD"])

// client
val seed = "WALLET SEED"
val id = "CLIENT ID"
inner class MessageListener : ClientListener() {
    override fun onBinaryMessage(src: String, data: ByteArray?, pid: ByteArray, type: Int, encrypted: Boolean): Any? {
        return null
    }

    override fun onBlock() {

    }

    override fun onClosed() {

    }

    override fun onClosing() {

    }

    override fun onConnect() {
        // connect already, now can send message
    }

    override fun onError(e: Throwable) {

    }

    override fun onMessage(src: String, data: String?, pid: ByteArray, type: Int, encrypted: Boolean): Any? {
        // onMessage do something
        return false
    }

}

var client = Client(seed, id, listener = MessageListener())
client.connect()

// get client address
val address = client.address

// send message
client.send(to, message)
```
