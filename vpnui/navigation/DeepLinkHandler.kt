package com.cryptovpn.navigation

import android.content.Intent
import android.net.Uri
import androidx.core.net.toUri

/**
 * 深层链接处理器
 * 
 * 处理所有外部深层链接，转换为内部导航
 */
class DeepLinkHandler {
    
    companion object {
        // 支持的深层链接scheme
        const val SCHEME_APP = "cryptovpn"
        const val SCHEME_HTTPS = "https"
        
        // 支持的host
        const val HOST_APP = "app"
        const val HOST_WEB = "cryptovpn.com"
    }
    
    /**
     * 深层链接类型
     */
    sealed class DeepLinkType {
        data class Navigation(val route: String, val params: Map<String, String?> = emptyMap()) : DeepLinkType()
        data class Action(val action: String, val data: Map<String, String> = emptyMap()) : DeepLinkType()
        data class Invalid(val reason: String) : DeepLinkType()
    }
    
    /**
     * 解析深层链接
     */
    fun parse(uri: Uri): DeepLinkType {
        // 验证scheme
        if (uri.scheme != SCHEME_APP && uri.scheme != SCHEME_HTTPS) {
            return DeepLinkType.Invalid("Unsupported scheme: ${uri.scheme}")
        }
        
        // 验证host
        if (uri.scheme == SCHEME_APP && uri.host != HOST_APP) {
            return DeepLinkType.Invalid("Invalid host for app scheme: ${uri.host}")
        }
        
        if (uri.scheme == SCHEME_HTTPS && uri.host != HOST_WEB) {
            return DeepLinkType.Invalid("Invalid host for https scheme: ${uri.host}")
        }
        
        // 获取路径
        val pathSegments = uri.pathSegments
        if (pathSegments.isEmpty()) {
            return DeepLinkType.Invalid("Empty path")
        }
        
        val route = pathSegments[0]
        val params = uri.queryParameterNames.associateWith { uri.getQueryParameter(it) }
        
        return when (route) {
            // 启动
            Routes.Splash.ROUTE -> DeepLinkType.Navigation(Routes.Splash.FULL_ROUTE, params)
            
            // 认证
            Routes.EmailLogin.ROUTE -> DeepLinkType.Navigation(
                Routes.EmailLogin.FULL_ROUTE,
                mapOf(Routes.EmailLogin.PARAM_REDIRECT to params[Routes.EmailLogin.PARAM_REDIRECT])
            )
            Routes.EmailRegister.ROUTE -> DeepLinkType.Navigation(
                Routes.EmailRegister.FULL_ROUTE,
                mapOf(Routes.EmailRegister.PARAM_INVITE_CODE to params[Routes.EmailRegister.PARAM_INVITE_CODE])
            )
            Routes.ResetPassword.ROUTE -> DeepLinkType.Navigation(
                Routes.ResetPassword.FULL_ROUTE,
                mapOf(
                    Routes.ResetPassword.PARAM_EMAIL to params[Routes.ResetPassword.PARAM_EMAIL],
                    Routes.ResetPassword.PARAM_TOKEN to params[Routes.ResetPassword.PARAM_TOKEN]
                )
            )
            
            // VPN
            Routes.VpnHome.ROUTE -> DeepLinkType.Navigation(
                Routes.VpnHome.FULL_ROUTE,
                mapOf(Routes.VpnHome.PARAM_AUTO_CONNECT to params[Routes.VpnHome.PARAM_AUTO_CONNECT])
            )
            Routes.Plans.ROUTE -> DeepLinkType.Navigation(
                Routes.Plans.FULL_ROUTE,
                mapOf(Routes.Plans.PARAM_SELECTED_PLAN to params[Routes.Plans.PARAM_SELECTED_PLAN])
            )
            Routes.OrderDetail.ROUTE -> {
                val orderId = pathSegments.getOrNull(1)
                if (orderId != null) {
                    DeepLinkType.Navigation(
                        "${Routes.OrderDetail.ROUTE}/$orderId",
                        emptyMap()
                    )
                } else {
                    DeepLinkType.Invalid("Missing orderId")
                }
            }
            Routes.OrderList.ROUTE -> DeepLinkType.Navigation(
                Routes.OrderList.FULL_ROUTE,
                mapOf(Routes.OrderList.PARAM_STATUS_FILTER to params[Routes.OrderList.PARAM_STATUS_FILTER])
            )
            
            // 钱包
            Routes.WalletHome.ROUTE -> DeepLinkType.Navigation(
                Routes.WalletHome.FULL_ROUTE,
                mapOf(Routes.WalletHome.PARAM_HIGHLIGHT_ASSET to params[Routes.WalletHome.PARAM_HIGHLIGHT_ASSET])
            )
            Routes.AssetDetail.ROUTE -> {
                val assetId = pathSegments.getOrNull(1)
                if (assetId != null) {
                    DeepLinkType.Navigation(
                        "${Routes.AssetDetail.ROUTE}/$assetId",
                        mapOf(Routes.AssetDetail.PARAM_CHAIN_ID to params[Routes.AssetDetail.PARAM_CHAIN_ID])
                    )
                } else {
                    DeepLinkType.Invalid("Missing assetId")
                }
            }
            Routes.Send.ROUTE -> {
                val assetId = pathSegments.getOrNull(1)
                if (assetId != null) {
                    DeepLinkType.Navigation(
                        "${Routes.Send.ROUTE}/$assetId",
                        mapOf(
                            Routes.Send.PARAM_CHAIN_ID to params[Routes.Send.PARAM_CHAIN_ID],
                            Routes.Send.PARAM_TO_ADDRESS to params[Routes.Send.PARAM_TO_ADDRESS],
                            Routes.Send.PARAM_AMOUNT to params[Routes.Send.PARAM_AMOUNT]
                        )
                    )
                } else {
                    DeepLinkType.Invalid("Missing assetId")
                }
            }
            Routes.Receive.ROUTE -> {
                val assetId = pathSegments.getOrNull(1)
                if (assetId != null) {
                    DeepLinkType.Navigation(
                        "${Routes.Receive.ROUTE}/$assetId",
                        mapOf(Routes.Receive.PARAM_CHAIN_ID to params[Routes.Receive.PARAM_CHAIN_ID])
                    )
                } else {
                    DeepLinkType.Invalid("Missing assetId")
                }
            }
            
            // 增长
            Routes.InviteCenter.ROUTE -> DeepLinkType.Navigation(
                Routes.InviteCenter.FULL_ROUTE,
                mapOf(Routes.InviteCenter.PARAM_HIGHLIGHT_TAB to params[Routes.InviteCenter.PARAM_HIGHLIGHT_TAB])
            )
            Routes.CommissionLedger.ROUTE -> DeepLinkType.Navigation(
                Routes.CommissionLedger.FULL_ROUTE,
                mapOf(Routes.CommissionLedger.PARAM_PERIOD to params[Routes.CommissionLedger.PARAM_PERIOD])
            )
            Routes.Withdraw.ROUTE -> DeepLinkType.Navigation(
                Routes.Withdraw.FULL_ROUTE,
                mapOf(
                    Routes.Withdraw.PARAM_CURRENCY to params[Routes.Withdraw.PARAM_CURRENCY],
                    Routes.Withdraw.PARAM_MAX_AMOUNT to params[Routes.Withdraw.PARAM_MAX_AMOUNT]
                )
            )
            
            // 我的
            Routes.Profile.ROUTE -> DeepLinkType.Navigation(Routes.Profile.FULL_ROUTE, emptyMap())
            Routes.LegalDocuments.ROUTE -> DeepLinkType.Navigation(
                Routes.LegalDocuments.FULL_ROUTE,
                mapOf(Routes.LegalDocuments.PARAM_CATEGORY to params[Routes.LegalDocuments.PARAM_CATEGORY])
            )
            Routes.LegalDocumentDetail.ROUTE -> {
                val docId = pathSegments.getOrNull(1)
                if (docId != null) {
                    DeepLinkType.Navigation(
                        "${Routes.LegalDocumentDetail.ROUTE}/$docId",
                        mapOf(Routes.LegalDocumentDetail.PARAM_DOC_TYPE to params[Routes.LegalDocumentDetail.PARAM_DOC_TYPE])
                    )
                } else {
                    DeepLinkType.Invalid("Missing docId")
                }
            }
            
            // 特殊动作
            "connect" -> DeepLinkType.Action("connect_vpn", params.filterValues { it != null }.mapValues { it.value!! })
            "disconnect" -> DeepLinkType.Action("disconnect_vpn", emptyMap())
            "payment" -> DeepLinkType.Action("process_payment", params.filterValues { it != null }.mapValues { it.value!! })
            
            else -> DeepLinkType.Invalid("Unknown route: $route")
        }
    }
    
    /**
     * 从Intent解析深层链接
     */
    fun parse(intent: Intent): DeepLinkType {
        val data = intent.data
        return if (data != null) {
            parse(data)
        } else {
            DeepLinkType.Invalid("No data in intent")
        }
    }
    
    /**
     * 构建深层链接URI
     */
    fun buildDeepLink(route: String, params: Map<String, String?> = emptyMap()): Uri {
        val uriBuilder = Uri.parse(Routes.DEEP_LINK_BASE).buildUpon()
            .appendPath(route)
        
        params.forEach { (key, value) ->
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value)
            }
        }
        
        return uriBuilder.build()
    }
    
    /**
     * 构建HTTPS深层链接
     */
    fun buildHttpsDeepLink(path: String, params: Map<String, String?> = emptyMap()): Uri {
        val uriBuilder = Uri.parse(Routes.DEEP_LINK_HTTPS).buildUpon()
            .appendPath(path)
        
        params.forEach { (key, value) ->
            if (value != null) {
                uriBuilder.appendQueryParameter(key, value)
            }
        }
        
        return uriBuilder.build()
    }
}

/**
 * 深层链接工厂
 * 用于生成各种深层链接
 */
object DeepLinkFactory {
    private const val BASE_URI = Routes.DEEP_LINK_BASE
    
    // ==================== 启动 ====================
    fun splash(): Uri = "$BASE_URI/${Routes.Splash.ROUTE}".toUri()
    
    fun forceUpdate(version: String, downloadUrl: String): Uri {
        return "$BASE_URI/${Routes.ForceUpdate.ROUTE}?" +
                "${Routes.ForceUpdate.PARAM_VERSION}=$version&" +
                "${Routes.ForceUpdate.PARAM_DOWNLOAD_URL}=${Uri.encode(downloadUrl)}".toUri()
    }
    
    // ==================== 认证 ====================
    fun emailLogin(redirect: String? = null): Uri {
        return if (redirect != null) {
            "$BASE_URI/${Routes.EmailLogin.ROUTE}?${Routes.EmailLogin.PARAM_REDIRECT}=${Uri.encode(redirect)}".toUri()
        } else {
            "$BASE_URI/${Routes.EmailLogin.ROUTE}".toUri()
        }
    }
    
    fun emailRegister(inviteCode: String? = null): Uri {
        return if (inviteCode != null) {
            "$BASE_URI/${Routes.EmailRegister.ROUTE}?${Routes.EmailRegister.PARAM_INVITE_CODE}=$inviteCode".toUri()
        } else {
            "$BASE_URI/${Routes.EmailRegister.ROUTE}".toUri()
        }
    }
    
    fun resetPassword(email: String, token: String): Uri {
        return "$BASE_URI/${Routes.ResetPassword.ROUTE}?" +
                "${Routes.ResetPassword.PARAM_EMAIL}=$email&" +
                "${Routes.ResetPassword.PARAM_TOKEN}=$token".toUri()
    }
    
    // ==================== VPN ====================
    fun vpnHome(autoConnect: Boolean = false): Uri {
        return if (autoConnect) {
            "$BASE_URI/${Routes.VpnHome.ROUTE}?${Routes.VpnHome.PARAM_AUTO_CONNECT}=true".toUri()
        } else {
            "$BASE_URI/${Routes.VpnHome.ROUTE}".toUri()
        }
    }
    
    fun plans(selectedPlan: String? = null): Uri {
        return if (selectedPlan != null) {
            "$BASE_URI/${Routes.Plans.ROUTE}?${Routes.Plans.PARAM_SELECTED_PLAN}=$selectedPlan".toUri()
        } else {
            "$BASE_URI/${Routes.Plans.ROUTE}".toUri()
        }
    }
    
    fun orderDetail(orderId: String): Uri {
        return "$BASE_URI/${Routes.OrderDetail.ROUTE}/$orderId".toUri()
    }
    
    fun orderList(statusFilter: String? = null): Uri {
        return if (statusFilter != null) {
            "$BASE_URI/${Routes.OrderList.ROUTE}?${Routes.OrderList.PARAM_STATUS_FILTER}=$statusFilter".toUri()
        } else {
            "$BASE_URI/${Routes.OrderList.ROUTE}".toUri()
        }
    }
    
    // ==================== 钱包 ====================
    fun walletHome(highlightAsset: String? = null): Uri {
        return if (highlightAsset != null) {
            "$BASE_URI/${Routes.WalletHome.ROUTE}?${Routes.WalletHome.PARAM_HIGHLIGHT_ASSET}=$highlightAsset".toUri()
        } else {
            "$BASE_URI/${Routes.WalletHome.ROUTE}".toUri()
        }
    }
    
    fun assetDetail(assetId: String, chainId: String? = null): Uri {
        return if (chainId != null) {
            "$BASE_URI/${Routes.AssetDetail.ROUTE}/$assetId?${Routes.AssetDetail.PARAM_CHAIN_ID}=$chainId".toUri()
        } else {
            "$BASE_URI/${Routes.AssetDetail.ROUTE}/$assetId".toUri()
        }
    }
    
    fun send(assetId: String, chainId: String? = null, toAddress: String? = null, amount: String? = null): Uri {
        val params = mutableListOf<String>()
        chainId?.let { params.add("${Routes.Send.PARAM_CHAIN_ID}=$it") }
        toAddress?.let { params.add("${Routes.Send.PARAM_TO_ADDRESS}=${Uri.encode(it)}") }
        amount?.let { params.add("${Routes.Send.PARAM_AMOUNT}=$it") }
        
        return if (params.isNotEmpty()) {
            "$BASE_URI/${Routes.Send.ROUTE}/$assetId?${params.joinToString("&")}".toUri()
        } else {
            "$BASE_URI/${Routes.Send.ROUTE}/$assetId".toUri()
        }
    }
    
    fun receive(assetId: String, chainId: String? = null): Uri {
        return if (chainId != null) {
            "$BASE_URI/${Routes.Receive.ROUTE}/$assetId?${Routes.Receive.PARAM_CHAIN_ID}=$chainId".toUri()
        } else {
            "$BASE_URI/${Routes.Receive.ROUTE}/$assetId".toUri()
        }
    }
    
    // ==================== 增长 ====================
    fun inviteCenter(highlightTab: String? = null): Uri {
        return if (highlightTab != null) {
            "$BASE_URI/${Routes.InviteCenter.ROUTE}?${Routes.InviteCenter.PARAM_HIGHLIGHT_TAB}=$highlightTab".toUri()
        } else {
            "$BASE_URI/${Routes.InviteCenter.ROUTE}".toUri()
        }
    }
    
    fun commissionLedger(period: String? = null): Uri {
        return if (period != null) {
            "$BASE_URI/${Routes.CommissionLedger.ROUTE}?${Routes.CommissionLedger.PARAM_PERIOD}=$period".toUri()
        } else {
            "$BASE_URI/${Routes.CommissionLedger.ROUTE}".toUri()
        }
    }
    
    fun withdraw(currency: String, maxAmount: String? = null): Uri {
        return if (maxAmount != null) {
            "$BASE_URI/${Routes.Withdraw.ROUTE}?${Routes.Withdraw.PARAM_CURRENCY}=$currency&${Routes.Withdraw.PARAM_MAX_AMOUNT}=$maxAmount".toUri()
        } else {
            "$BASE_URI/${Routes.Withdraw.ROUTE}?${Routes.Withdraw.PARAM_CURRENCY}=$currency".toUri()
        }
    }
    
    // ==================== 我的 ====================
    fun profile(): Uri = "$BASE_URI/${Routes.Profile.ROUTE}".toUri()
    
    fun legalDocuments(category: String? = null): Uri {
        return if (category != null) {
            "$BASE_URI/${Routes.LegalDocuments.ROUTE}?${Routes.LegalDocuments.PARAM_CATEGORY}=$category".toUri()
        } else {
            "$BASE_URI/${Routes.LegalDocuments.ROUTE}".toUri()
        }
    }
    
    fun legalDocumentDetail(docId: String, docType: String? = null): Uri {
        return if (docType != null) {
            "$BASE_URI/${Routes.LegalDocumentDetail.ROUTE}/$docId?${Routes.LegalDocumentDetail.PARAM_DOC_TYPE}=$docType".toUri()
        } else {
            "$BASE_URI/${Routes.LegalDocumentDetail.ROUTE}/$docId".toUri()
        }
    }
}
