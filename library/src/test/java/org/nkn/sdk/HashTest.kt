package org.nkn.sdk

import org.junit.Assert
import org.junit.Test
import org.nkn.sdk.crypto.*
import org.nkn.sdk.utils.Utils


class HashTest {
    @Test
    fun sha256_test() {
        var res = Utils.hexEncode(sha256("123456"))

        Assert.assertEquals(res, "8d969eef6ecad3c29a3a629280e686cf0c3f5d5a86aff3ca12020c923adc6c92")
    }
    @Test
    fun doubleSha256_test() {
        var res = Utils.hexEncode(doubleSha256("123456"))
        Assert.assertEquals(res, "ff7f73b854845fc02aa13b777ac090fb1d9ebfe16c8950c7d26499371dd0b479")
    }
    @Test
    fun sha256Hex_test() {
        var res = Utils.hexEncode(sha256Hex("123456"))
        Assert.assertEquals(res, "bf7cbe09d71a1bcc373ab9a764917f730a6ed951ffa1a7399b7abd8f8fd73cb4")
    }
    @Test
    fun doubleSha256Hex_test() {
        var res = Utils.hexEncode(doubleSha256Hex("123456"))
        Assert.assertEquals(res, "00574e0a61d00de8bb60d6aad57d3c105268b70a81a68979afc63b5d4809c25e")
    }

    @Test
    fun ripemd160Hex_test(){
        var res = Utils.hexEncode(ripemd160Hex("123456"))
        Assert.assertEquals(res, "99b6f3a3b7d96110deda57fa6c35153729eea168")
    }
}