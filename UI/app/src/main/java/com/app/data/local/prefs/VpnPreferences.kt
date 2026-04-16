package com.app.data.local.prefs

import android.content.Context

class VpnPreferences(@Suppress("UNUSED_PARAMETER") context: Context) {
    var selectedNodeId: String? = null
    var subscriptionUrl: String = "mock://subscription/vpn01"
    var lastUpdatedAt: Long = System.currentTimeMillis()
}
