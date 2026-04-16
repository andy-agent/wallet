package com.app.data.local.prefs

import android.content.Context

class WalletPreferences(@Suppress("UNUSED_PARAMETER") context: Context) {
    var hasWallet: Boolean = false
    var walletName: String = "Glow Wallet"
    var mnemonicBackedUp: Boolean = false
}
