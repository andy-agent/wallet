package com.v2ray.ang.payment.wallet

import org.bitcoinj.crypto.MnemonicCode
import java.security.SecureRandom

object WalletMnemonicGenerator {
    fun generate12WordMnemonic(): String {
        val entropy = ByteArray(16)
        SecureRandom().nextBytes(entropy)
        return MnemonicCode.INSTANCE.toMnemonic(entropy).joinToString(" ")
    }
}
