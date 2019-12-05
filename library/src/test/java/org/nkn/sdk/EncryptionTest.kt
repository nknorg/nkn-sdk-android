package org.nkn.sdk

import org.junit.Assert
import org.junit.Test
import org.nkn.sdk.crypto.*
import org.nkn.sdk.utils.Utils


class EncryptionTest {
    @Test
    fun aesEncrypt_test() {
        var res = aesEncrypt(Utils.hexDecode("d6d4e00674b6ee0d19e41c42bf92f5e919b97f3e02f33e18acb699a101355174"),
            Utils.hexDecode("0523d457e3ed9d803691f10c37a01361a8fb1a8392596ca059734d6cabe7dadc"),
            Utils.hexDecode("05f474918f8ea2dce1c94bfaae44fbbd"))
        Assert.assertEquals(Utils.hexEncode(res), "527cf7cda271b39cb86eaf56aa3689aeee28ee8c3b5289d576a340419ad6fdf0")
    }

    @Test
    fun aesDecrypt_test() {
        var res = aesDecrypt(Utils.hexDecode("527cf7cda271b39cb86eaf56aa3689aeee28ee8c3b5289d576a340419ad6fdf0"),
            Utils.hexDecode("0523d457e3ed9d803691f10c37a01361a8fb1a8392596ca059734d6cabe7dadc"),
            Utils.hexDecode("05f474918f8ea2dce1c94bfaae44fbbd"))
        Assert.assertEquals(Utils.hexEncode(res), "d6d4e00674b6ee0d19e41c42bf92f5e919b97f3e02f33e18acb699a101355174")
    }
}