package com.cryptovpn.navigation

import android.net.Uri
import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 导航管理器
 * 
 * 封装所有导航操作，提供类型安全的导航方法
 * 管理导航状态和深层链接处理
 */
class NavigationManager(private val navController: NavController) {
    
    // ==================== 导航状态 ====================
    private val _currentRoute = MutableStateFlow<String?>(null)
    val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()
    
    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()
    
    private val _navigationHistory = MutableStateFlow<List<String>>(emptyList())
    val navigationHistory: StateFlow<List<String>> = _navigationHistory.asStateFlow()
    
    init {
        // 监听导航变化
        navController.addOnDestinationChangedListener { _, destination, _ ->
            _currentRoute.value = destination.route
            _canGoBack.value = navController.previousBackStackEntry != null
            _navigationHistory.value = _navigationHistory.value + (destination.route ?: "")
        }
    }
    
    // ==================== 基础导航方法 ====================
    
    /**
     * 导航到指定路由
     */
    fun navigateTo(
        route: String,
        popUpToRoute: String? = null,
        inclusive: Boolean = false,
        launchSingleTop: Boolean = false,
        restoreState: Boolean = true
    ) {
        val options = navOptions {
            if (popUpToRoute != null) {
                popUpTo(popUpToRoute) {
                    this.inclusive = inclusive
                }
            }
            this.launchSingleTop = launchSingleTop
            this.restoreState = restoreState
        }
        navController.navigate(route, options)
    }
    
    /**
     * 返回上一页
     */
    fun goBack(): Boolean {
        return navController.popBackStack()
    }
    
    /**
     * 返回到指定路由
     */
    fun popBackTo(route: String, inclusive: Boolean = false): Boolean {
        return navController.popBackStack(route, inclusive)
    }
    
    /**
     * 返回到起始目的地
     */
    fun popToStart() {
        val startDestination = navController.graph.findStartDestination().route
        startDestination?.let {
            navController.popBackStack(it, false)
        }
    }
    
    /**
     * 清空导航栈并导航到新页面
     */
    fun navigateAndClearStack(route: String) {
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
        }
    }
    
    // ==================== 启动与版本导航 ====================
    
    fun navigateToSplash() {
        navigateTo(Routes.Splash.FULL_ROUTE)
    }
    
    fun navigateToForceUpdate(version: String, downloadUrl: String) {
        val route = "${Routes.ForceUpdate.ROUTE}?" +
                "${Routes.ForceUpdate.PARAM_VERSION}=$version&" +
                "${Routes.ForceUpdate.PARAM_DOWNLOAD_URL}=${Uri.encode(downloadUrl)}"
        navigateAndClearStack(route)
    }
    
    fun navigateToOptionalUpdate(version: String, downloadUrl: String) {
        val route = "${Routes.OptionalUpdate.ROUTE}?" +
                "${Routes.OptionalUpdate.PARAM_VERSION}=$version&" +
                "${Routes.OptionalUpdate.PARAM_DOWNLOAD_URL}=${Uri.encode(downloadUrl)}"
        navigateTo(route)
    }
    
    // ==================== 认证导航 ====================
    
    fun navigateToEmailLogin(redirect: String? = null) {
        val route = if (redirect != null) {
            "${Routes.EmailLogin.ROUTE}?${Routes.EmailLogin.PARAM_REDIRECT}=${Uri.encode(redirect)}"
        } else {
            Routes.EmailLogin.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToEmailRegister(inviteCode: String? = null) {
        val route = if (inviteCode != null) {
            "${Routes.EmailRegister.ROUTE}?${Routes.EmailRegister.PARAM_INVITE_CODE}=$inviteCode"
        } else {
            Routes.EmailRegister.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToResetPassword(email: String? = null, token: String? = null) {
        val route = buildString {
            append(Routes.ResetPassword.ROUTE)
            val params = mutableListOf<String>()
            email?.let { params.add("${Routes.ResetPassword.PARAM_EMAIL}=$it") }
            token?.let { params.add("${Routes.ResetPassword.PARAM_TOKEN}=$it") }
            if (params.isNotEmpty()) {
                append("?${params.joinToString("&")}")
            }
        }
        navigateTo(route)
    }
    
    // ==================== VPN导航 ====================
    
    fun navigateToVpnHome(autoConnect: Boolean = false) {
        val route = if (autoConnect) {
            "${Routes.VpnHome.ROUTE}?${Routes.VpnHome.PARAM_AUTO_CONNECT}=true"
        } else {
            Routes.VpnHome.FULL_ROUTE
        }
        navigateAndClearStack(route)
    }
    
    fun navigateToPlans(selectedPlan: String? = null) {
        val route = if (selectedPlan != null) {
            "${Routes.Plans.ROUTE}?${Routes.Plans.PARAM_SELECTED_PLAN}=$selectedPlan"
        } else {
            Routes.Plans.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToRegionSelection(currentRegion: String? = null) {
        val route = if (currentRegion != null) {
            "${Routes.RegionSelection.ROUTE}?${Routes.RegionSelection.PARAM_CURRENT_REGION}=$currentRegion"
        } else {
            Routes.RegionSelection.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToOrderCheckout(planId: String, duration: Int = 1) {
        val route = "${Routes.OrderCheckout.ROUTE}/$planId?${Routes.OrderCheckout.PARAM_DURATION}=$duration"
        navigateTo(route)
    }
    
    fun navigateToWalletPaymentConfirm(orderId: String, amount: String, currency: String) {
        val route = "${Routes.WalletPaymentConfirm.ROUTE}/$orderId?" +
                "${Routes.WalletPaymentConfirm.PARAM_AMOUNT}=$amount&" +
                "${Routes.WalletPaymentConfirm.PARAM_CURRENCY}=$currency"
        navigateTo(route)
    }
    
    fun navigateToOrderResult(orderId: String, status: String) {
        val route = "${Routes.OrderResult.ROUTE}/$orderId?${Routes.OrderResult.PARAM_STATUS}=$status"
        navigateTo(route)
    }
    
    fun navigateToOrderList(statusFilter: String? = null) {
        val route = if (statusFilter != null) {
            "${Routes.OrderList.ROUTE}?${Routes.OrderList.PARAM_STATUS_FILTER}=$statusFilter"
        } else {
            Routes.OrderList.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToOrderDetail(orderId: String) {
        val route = "${Routes.OrderDetail.ROUTE}/$orderId"
        navigateTo(route)
    }
    
    // ==================== 钱包导航 ====================
    
    fun navigateToWalletOnboarding(step: String? = null) {
        val route = if (step != null) {
            "${Routes.WalletOnboarding.ROUTE}?${Routes.WalletOnboarding.PARAM_STEP}=$step"
        } else {
            Routes.WalletOnboarding.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToWalletHome(highlightAsset: String? = null) {
        val route = if (highlightAsset != null) {
            "${Routes.WalletHome.ROUTE}?${Routes.WalletHome.PARAM_HIGHLIGHT_ASSET}=$highlightAsset"
        } else {
            Routes.WalletHome.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToAssetDetail(assetId: String, chainId: String? = null) {
        val route = if (chainId != null) {
            "${Routes.AssetDetail.ROUTE}/$assetId?${Routes.AssetDetail.PARAM_CHAIN_ID}=$chainId"
        } else {
            "${Routes.AssetDetail.ROUTE}/$assetId"
        }
        navigateTo(route)
    }
    
    fun navigateToReceive(assetId: String, chainId: String? = null) {
        val route = if (chainId != null) {
            "${Routes.Receive.ROUTE}/$assetId?${Routes.Receive.PARAM_CHAIN_ID}=$chainId"
        } else {
            "${Routes.Receive.ROUTE}/$assetId"
        }
        navigateTo(route)
    }
    
    fun navigateToSend(assetId: String, chainId: String? = null, toAddress: String? = null, amount: String? = null) {
        val route = buildString {
            append("${Routes.Send.ROUTE}/$assetId")
            val params = mutableListOf<String>()
            chainId?.let { params.add("${Routes.Send.PARAM_CHAIN_ID}=$it") }
            toAddress?.let { params.add("${Routes.Send.PARAM_TO_ADDRESS}=${Uri.encode(it)}") }
            amount?.let { params.add("${Routes.Send.PARAM_AMOUNT}=$it") }
            if (params.isNotEmpty()) {
                append("?${params.joinToString("&")}")
            }
        }
        navigateTo(route)
    }
    
    fun navigateToSendResult(txHash: String, status: String) {
        val route = "${Routes.SendResult.ROUTE}/$txHash?${Routes.SendResult.PARAM_STATUS}=$status"
        navigateTo(route)
    }
    
    fun navigateToWalletPayment(requestId: String, payload: String? = null) {
        val route = if (payload != null) {
            "${Routes.WalletPayment.ROUTE}/$requestId?${Routes.WalletPayment.PARAM_PAYLOAD}=${Uri.encode(payload)}"
        } else {
            "${Routes.WalletPayment.ROUTE}/$requestId"
        }
        navigateTo(route)
    }
    
    // ==================== 增长导航 ====================
    
    fun navigateToInviteCenter(highlightTab: String? = null) {
        val route = if (highlightTab != null) {
            "${Routes.InviteCenter.ROUTE}?${Routes.InviteCenter.PARAM_HIGHLIGHT_TAB}=$highlightTab"
        } else {
            Routes.InviteCenter.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToCommissionLedger(period: String? = null) {
        val route = if (period != null) {
            "${Routes.CommissionLedger.ROUTE}?${Routes.CommissionLedger.PARAM_PERIOD}=$period"
        } else {
            Routes.CommissionLedger.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToWithdraw(currency: String, maxAmount: String? = null) {
        val route = buildString {
            append(Routes.Withdraw.ROUTE)
            val params = mutableListOf<String>()
            params.add("${Routes.Withdraw.PARAM_CURRENCY}=$currency")
            maxAmount?.let { params.add("${Routes.Withdraw.PARAM_MAX_AMOUNT}=$it") }
            if (params.isNotEmpty()) {
                append("?${params.joinToString("&")}")
            }
        }
        navigateTo(route)
    }
    
    // ==================== 我的与法务导航 ====================
    
    fun navigateToProfile() {
        navigateTo(Routes.Profile.FULL_ROUTE)
    }
    
    fun navigateToLegalDocuments(category: String? = null) {
        val route = if (category != null) {
            "${Routes.LegalDocuments.ROUTE}?${Routes.LegalDocuments.PARAM_CATEGORY}=$category"
        } else {
            Routes.LegalDocuments.FULL_ROUTE
        }
        navigateTo(route)
    }
    
    fun navigateToLegalDocumentDetail(docId: String, docType: String? = null) {
        val route = if (docType != null) {
            "${Routes.LegalDocumentDetail.ROUTE}/$docId?${Routes.LegalDocumentDetail.PARAM_DOC_TYPE}=$docType"
        } else {
            "${Routes.LegalDocumentDetail.ROUTE}/$docId"
        }
        navigateTo(route)
    }
    
    // ==================== 深层链接处理 ====================
    
    /**
     * 处理深层链接URI
     */
    fun handleDeepLink(uri: Uri): Boolean {
        return when (uri.pathSegments.firstOrNull()) {
            Routes.Splash.ROUTE -> {
                navigateToSplash()
                true
            }
            Routes.EmailLogin.ROUTE -> {
                val redirect = uri.getQueryParameter(Routes.EmailLogin.PARAM_REDIRECT)
                navigateToEmailLogin(redirect)
                true
            }
            Routes.EmailRegister.ROUTE -> {
                val inviteCode = uri.getQueryParameter(Routes.EmailRegister.PARAM_INVITE_CODE)
                navigateToEmailRegister(inviteCode)
                true
            }
            Routes.ResetPassword.ROUTE -> {
                val email = uri.getQueryParameter(Routes.ResetPassword.PARAM_EMAIL)
                val token = uri.getQueryParameter(Routes.ResetPassword.PARAM_TOKEN)
                navigateToResetPassword(email, token)
                true
            }
            Routes.VpnHome.ROUTE -> {
                val autoConnect = uri.getQueryParameter(Routes.VpnHome.PARAM_AUTO_CONNECT)?.toBoolean() ?: false
                navigateToVpnHome(autoConnect)
                true
            }
            Routes.Plans.ROUTE -> {
                val selectedPlan = uri.getQueryParameter(Routes.Plans.PARAM_SELECTED_PLAN)
                navigateToPlans(selectedPlan)
                true
            }
            Routes.OrderDetail.ROUTE -> {
                val orderId = uri.pathSegments.getOrNull(1)
                orderId?.let { navigateToOrderDetail(it) }
                orderId != null
            }
            Routes.WalletHome.ROUTE -> {
                navigateToWalletHome()
                true
            }
            Routes.InviteCenter.ROUTE -> {
                navigateToInviteCenter()
                true
            }
            Routes.Profile.ROUTE -> {
                navigateToProfile()
                true
            }
            else -> false
        }
    }
    
    // ==================== 工具方法 ====================
    
    /**
     * 检查当前是否在指定路由
     */
    fun isCurrentRoute(route: String): Boolean {
        return navController.currentDestination?.route?.startsWith(route) == true
    }
    
    /**
     * 获取当前路由参数
     */
    fun <T> getCurrentArgument(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return navController.currentBackStackEntry?.arguments?.get(key) as? T
    }
    
    /**
     * 设置返回结果
     */
    fun setResult(key: String, value: Any) {
        navController.previousBackStackEntry?.savedStateHandle?.set(key, value)
    }
    
    /**
     * 获取返回结果
     */
    fun <T> getResult(key: String): T? {
        @Suppress("UNCHECKED_CAST")
        return navController.currentBackStackEntry?.savedStateHandle?.get<T>(key)
    }
    
    /**
     * 清除导航历史
     */
    fun clearHistory() {
        _navigationHistory.value = emptyList()
    }
}

/**
 * 导航管理器提供者
 * 用于Compose中获取NavigationManager
 */
object NavigationManagerProvider {
    private var instance: NavigationManager? = null
    
    fun initialize(navController: NavController) {
        instance = NavigationManager(navController)
    }
    
    fun get(): NavigationManager {
        return instance ?: throw IllegalStateException("NavigationManager not initialized")
    }
    
    fun getOrNull(): NavigationManager? = instance
}
