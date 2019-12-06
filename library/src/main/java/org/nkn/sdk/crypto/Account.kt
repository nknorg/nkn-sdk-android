package org.nkn.sdk.crypto

import org.nkn.sdk.utils.Utils

class Account {
    val key: Key
    val address: String
    val contract: String
    val signatureRedeem: String

    constructor(seed: Any? = null) {
        key = Key(seed)
        address = Utils.programHashStringToAddress(key.programHash)
        contract = Utils.genAccountContractString(key.signatureRedeem, key.programHash)
        signatureRedeem = key.signatureRedeem
    }

}