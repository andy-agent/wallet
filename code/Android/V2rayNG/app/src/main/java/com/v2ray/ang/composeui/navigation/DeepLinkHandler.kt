package com.v2ray.ang.composeui.navigation

import android.content.Intent
import android.net.Uri

class DeepLinkHandler {
    sealed class DeepLinkType {
        data class Navigation(val route: String) : DeepLinkType()
        data class Invalid(val reason: String) : DeepLinkType()
    }

    fun parse(uri: Uri): DeepLinkType {
        val first = uri.pathSegments.firstOrNull()
        return when (first) {
            null -> DeepLinkType.Invalid("Empty deep link path")
            "vpn" -> DeepLinkType.Navigation(Routes.VPN_HOME)
            "wallet" -> DeepLinkType.Navigation(Routes.WALLET_HOME)
            "order" -> DeepLinkType.Navigation(Routes.ORDER_LIST)
            else -> DeepLinkType.Invalid("Unsupported path: $first")
        }
    }

    fun parse(intent: Intent): DeepLinkType {
        val data = intent.data ?: return DeepLinkType.Invalid("No data in intent")
        return parse(data)
    }

    fun buildDeepLink(route: String): Uri =
        Uri.parse("${Routes.DeepLinks.BASE_URI}/$route")
}
