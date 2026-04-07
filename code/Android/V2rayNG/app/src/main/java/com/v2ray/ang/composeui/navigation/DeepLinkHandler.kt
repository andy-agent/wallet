package com.v2ray.ang.composeui.navigation

import android.content.Intent
import android.net.Uri

class DeepLinkHandler {
    sealed class DeepLinkType {
        data class Navigation(val route: String) : DeepLinkType()
        data class Invalid(val reason: String) : DeepLinkType()
    }

    fun parse(uri: Uri): DeepLinkType {
        val segments = uri.pathSegments.filter { it.isNotBlank() }
        val first = segments.firstOrNull()
        return when (first) {
            null -> DeepLinkType.Invalid("Empty deep link path")
            "vpn" -> DeepLinkType.Navigation(Routes.appShell(ShellTab.HOME))
            "wallet" -> {
                val assetId = segments.getOrNull(1)
                if (assetId.isNullOrBlank()) {
                    DeepLinkType.Navigation(Routes.appShell(ShellTab.WALLET))
                } else {
                    DeepLinkType.Navigation(Routes.assetDetail(assetId))
                }
            }
            "order" -> {
                val orderId = segments.getOrNull(1)
                if (orderId.isNullOrBlank()) {
                    DeepLinkType.Navigation(Routes.ORDER_LIST)
                } else {
                    DeepLinkType.Navigation(Routes.orderDetail(orderId))
                }
            }
            "invite" -> DeepLinkType.Navigation(Routes.INVITE_CENTER)
            "legal" -> {
                val documentId = segments.getOrNull(1)
                if (documentId.isNullOrBlank()) {
                    DeepLinkType.Navigation(Routes.LEGAL_DOCUMENTS)
                } else {
                    DeepLinkType.Navigation(Routes.legalDocumentDetail(documentId))
                }
            }
            else -> DeepLinkType.Invalid("Unsupported path: $first")
        }
    }

    fun parse(intent: Intent): DeepLinkType {
        val data = intent.data ?: return DeepLinkType.Invalid("No data in intent")
        return parse(data)
    }

    fun buildDeepLink(route: String): Uri =
        Uri.parse("${Routes.DeepLinks.BASE_URI}/${Routes.normalize(route)}")
}
