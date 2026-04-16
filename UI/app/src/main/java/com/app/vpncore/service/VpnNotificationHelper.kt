package com.app.vpncore.service

import android.content.Context

object VpnNotificationHelper {
    fun notifyConnecting(@Suppress("UNUSED_PARAMETER") context: Context, @Suppress("UNUSED_PARAMETER") nodeName: String) = Unit
    fun notifyConnected(@Suppress("UNUSED_PARAMETER") context: Context, @Suppress("UNUSED_PARAMETER") nodeName: String) = Unit
    fun notifyDisconnected(@Suppress("UNUSED_PARAMETER") context: Context) = Unit
}
