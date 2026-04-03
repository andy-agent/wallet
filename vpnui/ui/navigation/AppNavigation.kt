package com.cryptovpn.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cryptovpn.ui.pages.auth.*
import com.cryptovpn.ui.pages.growth.*
import com.cryptovpn.ui.pages.legal.*
import com.cryptovpn.ui.pages.profile.ProfilePage
import com.cryptovpn.ui.pages.splash.*
import com.cryptovpn.ui.pages.vpn.*
import com.cryptovpn.ui.pages.wallet.*

/**
 * 导航路由定义
 */
object AppRoutes {
    // 启动与版本
    const val SPLASH = "splash"
    const val FORCE_UPDATE = "force_update"
    
    // 认证
    const val LOGIN = "login"
    const val REGISTER = "register"
    const val RESET_PASSWORD = "reset_password"
    
    // VPN
    const val VPN_HOME = "vpn_home"
    const val PLANS = "plans"
    const val REGION_SELECTION = "region_selection"
    const val ORDER_CHECKOUT = "order_checkout/{planId}"
    const val ORDER_RESULT = "order_result/{result}"
    const val ORDER_LIST = "order_list"
    const val ORDER_DETAIL = "order_detail/{orderId}"
    
    // 钱包
    const val WALLET_ONBOARDING = "wallet_onboarding"
    const val WALLET_HOME = "wallet_home"
    const val ASSET_DETAIL = "asset_detail/{symbol}"
    const val RECEIVE = "receive/{symbol}"
    const val SEND = "send/{symbol}"
    const val SEND_RESULT = "send_result/{result}"
    
    // 增长
    const val INVITE_CENTER = "invite_center"
    const val COMMISSION_LEDGER = "commission_ledger"
    const val WITHDRAW = "withdraw"
    
    // 我的与法务
    const val PROFILE = "profile"
    const val LEGAL_DOCUMENTS = "legal_documents"
    const val LEGAL_DOCUMENT_DETAIL = "legal_document_detail/{documentId}"
}

/**
 * 构建带参数的导航路由
 */
fun buildRoute(route: String, vararg params: Pair<String, String>): String {
    var result = route
    params.forEach { (key, value) ->
        result = result.replace("{$key}", value)
    }
    return result
}

/**
 * 应用导航图
 * 定义所有页面之间的导航关系
 */
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String = AppRoutes.SPLASH
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ==================== 启动与版本 ====================
        composable(AppRoutes.SPLASH) {
            SplashScreen(
                onNavigateToHome = { navController.navigate(AppRoutes.VPN_HOME) },
                onNavigateToLogin = { navController.navigate(AppRoutes.LOGIN) },
                onShowForceUpdate = { navController.navigate(AppRoutes.FORCE_UPDATE) },
                onShowOptionalUpdate = { /* 显示可选更新弹窗 */ }
            )
        }
        
        composable(AppRoutes.FORCE_UPDATE) {
            ForceUpdatePage(
                onUpdateClick = { /* 打开应用商店 */ },
                onExitClick = { /* 退出应用 */ }
            )
        }
        
        // ==================== 认证 ====================
        composable(AppRoutes.LOGIN) {
            EmailLoginPage(
                onLoginSuccess = { navController.navigate(AppRoutes.VPN_HOME) },
                onNavigateToRegister = { navController.navigate(AppRoutes.REGISTER) },
                onNavigateToResetPassword = { navController.navigate(AppRoutes.RESET_PASSWORD) }
            )
        }
        
        composable(AppRoutes.REGISTER) {
            EmailRegisterPage(
                onRegisterSuccess = { navController.navigate(AppRoutes.LOGIN) },
                onNavigateToLogin = { navController.navigate(AppRoutes.LOGIN) },
                onNavigateToTerms = { navController.navigate(AppRoutes.LEGAL_DOCUMENTS) },
                onNavigateToPrivacy = { navController.navigate(AppRoutes.LEGAL_DOCUMENTS) }
            )
        }
        
        composable(AppRoutes.RESET_PASSWORD) {
            ResetPasswordPage(
                onResetSuccess = { navController.navigate(AppRoutes.LOGIN) },
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        // ==================== VPN ====================
        composable(AppRoutes.VPN_HOME) {
            VPNHomePage(
                onNavigateToRegions = { navController.navigate(AppRoutes.REGION_SELECTION) },
                onNavigateToPlans = { navController.navigate(AppRoutes.PLANS) },
                onNavigateToProfile = { navController.navigate(AppRoutes.PROFILE) }
            )
        }
        
        composable(AppRoutes.PLANS) {
            PlansPage(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCheckout = { planId ->
                    navController.navigate(buildRoute(AppRoutes.ORDER_CHECKOUT, "planId" to planId))
                }
            )
        }
        
        composable(AppRoutes.REGION_SELECTION) {
            RegionSelectionPage(
                onNavigateBack = { navController.popBackStack() },
                onRegionSelected = { region ->
                    navController.popBackStack()
                }
            )
        }
        
        composable(
            route = AppRoutes.ORDER_CHECKOUT,
            arguments = listOf(navArgument("planId") { type = NavType.StringType })
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString("planId") ?: ""
            OrderCheckoutPage(
                planId = planId,
                onNavigateBack = { navController.popBackStack() },
                onPayWithWallet = { 
                    navController.navigate(buildRoute(AppRoutes.ORDER_RESULT, "result" to "success"))
                },
                onPayWithCrypto = { 
                    navController.navigate(buildRoute(AppRoutes.ORDER_RESULT, "result" to "success"))
                }
            )
        }
        
        composable(
            route = AppRoutes.ORDER_RESULT,
            arguments = listOf(navArgument("result") { type = NavType.StringType })
        ) { backStackEntry ->
            val result = backStackEntry.arguments?.getString("result") ?: "success"
            val resultType = when (result) {
                "success" -> OrderResultType.SUCCESS
                "failed" -> OrderResultType.FAILED
                else -> OrderResultType.SUCCESS
            }
            OrderResultPage(
                resultType = resultType,
                onNavigateToHome = { 
                    navController.navigate(AppRoutes.VPN_HOME) {
                        popUpTo(AppRoutes.VPN_HOME) { inclusive = true }
                    }
                },
                onNavigateToOrders = { navController.navigate(AppRoutes.ORDER_LIST) }
            )
        }
        
        composable(AppRoutes.ORDER_LIST) {
            OrderListPage(
                onNavigateBack = { navController.popBackStack() },
                onOrderClick = { orderId ->
                    navController.navigate(buildRoute(AppRoutes.ORDER_DETAIL, "orderId" to orderId))
                }
            )
        }
        
        composable(
            route = AppRoutes.ORDER_DETAIL,
            arguments = listOf(navArgument("orderId") { type = NavType.StringType })
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailPage(
                orderId = orderId,
                onNavigateBack = { navController.popBackStack() },
                onPayOrder = { /* 支付订单 */ },
                onContactSupport = { /* 联系客服 */ }
            )
        }
        
        // ==================== 钱包 ====================
        composable(AppRoutes.WALLET_ONBOARDING) {
            WalletOnboardingPage(
                onNavigateToCreate = { navController.navigate(AppRoutes.WALLET_HOME) },
                onNavigateToImport = { navController.navigate(AppRoutes.WALLET_HOME) },
                onSkip = { navController.navigate(AppRoutes.VPN_HOME) }
            )
        }
        
        composable(AppRoutes.WALLET_HOME) {
            WalletHomePage(
                onNavigateToReceive = { navController.navigate(buildRoute(AppRoutes.RECEIVE, "symbol" to "ETH")) },
                onNavigateToSend = { navController.navigate(buildRoute(AppRoutes.SEND, "symbol" to "ETH")) },
                onNavigateToAssetDetail = { symbol ->
                    navController.navigate(buildRoute(AppRoutes.ASSET_DETAIL, "symbol" to symbol))
                },
                onNavigateToProfile = { navController.navigate(AppRoutes.PROFILE) }
            )
        }
        
        composable(
            route = AppRoutes.ASSET_DETAIL,
            arguments = listOf(navArgument("symbol") { type = NavType.StringType })
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: "ETH"
            AssetDetailPage(
                symbol = symbol,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToSend = { navController.navigate(buildRoute(AppRoutes.SEND, "symbol" to symbol)) },
                onNavigateToReceive = { navController.navigate(buildRoute(AppRoutes.RECEIVE, "symbol" to symbol)) },
                onTransactionClick = { /* 查看交易详情 */ }
            )
        }
        
        composable(
            route = AppRoutes.RECEIVE,
            arguments = listOf(navArgument("symbol") { type = NavType.StringType })
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: "ETH"
            ReceivePage(
                symbol = symbol,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        
        composable(
            route = AppRoutes.SEND,
            arguments = listOf(navArgument("symbol") { type = NavType.StringType })
        ) { backStackEntry ->
            val symbol = backStackEntry.arguments?.getString("symbol") ?: "ETH"
            SendPage(
                symbol = symbol,
                onNavigateBack = { navController.popBackStack() },
                onNavigateToConfirm = { recipient, amount, asset ->
                    navController.navigate(buildRoute(AppRoutes.SEND_RESULT, "result" to "success"))
                },
                onScanQR = { /* 扫描二维码 */ }
            )
        }
        
        composable(
            route = AppRoutes.SEND_RESULT,
            arguments = listOf(navArgument("result") { type = NavType.StringType })
        ) { backStackEntry ->
            val result = backStackEntry.arguments?.getString("result") ?: "success"
            val resultType = when (result) {
                "success" -> SendResultType.SUCCESS
                "failed" -> SendResultType.FAILED
                else -> SendResultType.SUCCESS
            }
            SendResultPage(
                resultType = resultType,
                onNavigateToHome = { navController.navigate(AppRoutes.VPN_HOME) },
                onNavigateToWallet = { navController.navigate(AppRoutes.WALLET_HOME) },
                onRetry = { navController.popBackStack() },
                onViewExplorer = { /* 查看区块链浏览器 */ }
            )
        }
        
        // ==================== 增长 ====================
        composable(AppRoutes.INVITE_CENTER) {
            InviteCenterPage(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToCommission = { navController.navigate(AppRoutes.COMMISSION_LEDGER) },
                onNavigateToWithdraw = { navController.navigate(AppRoutes.WITHDRAW) }
            )
        }
        
        composable(AppRoutes.COMMISSION_LEDGER) {
            CommissionLedgerPage(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToWithdraw = { navController.navigate(AppRoutes.WITHDRAW) }
            )
        }
        
        composable(AppRoutes.WITHDRAW) {
            WithdrawPage(
                onNavigateBack = { navController.popBackStack() },
                onWithdrawSuccess = { txHash ->
                    navController.popBackStack()
                }
            )
        }
        
        // ==================== 我的与法务 ====================
        composable(AppRoutes.PROFILE) {
            ProfilePage(
                onNavigateToOrders = { navController.navigate(AppRoutes.ORDER_LIST) },
                onNavigateToWallet = { navController.navigate(AppRoutes.WALLET_HOME) },
                onNavigateToInvite = { navController.navigate(AppRoutes.INVITE_CENTER) },
                onNavigateToCommission = { navController.navigate(AppRoutes.COMMISSION_LEDGER) },
                onNavigateToSettings = { /* 设置页面 */ },
                onNavigateToLegal = { navController.navigate(AppRoutes.LEGAL_DOCUMENTS) },
                onNavigateToSupport = { /* 帮助与反馈 */ },
                onNavigateToAbout = { /* 关于我们 */ },
                onLogout = { navController.navigate(AppRoutes.LOGIN) }
            )
        }
        
        composable(AppRoutes.LEGAL_DOCUMENTS) {
            LegalDocumentsListPage(
                onNavigateBack = { navController.popBackStack() },
                onDocumentClick = { documentId ->
                    navController.navigate(buildRoute(AppRoutes.LEGAL_DOCUMENT_DETAIL, "documentId" to documentId))
                }
            )
        }
        
        composable(
            route = AppRoutes.LEGAL_DOCUMENT_DETAIL,
            arguments = listOf(navArgument("documentId") { type = NavType.StringType })
        ) { backStackEntry ->
            val documentId = backStackEntry.arguments?.getString("documentId") ?: ""
            LegalDocumentDetailPage(
                documentId = documentId,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

/**
 * 页面跳转关系说明：
 * 
 * 1. 启动流程：
 *    Splash -> (ForceUpdate) -> Login/Register -> VPN_HOME
 * 
 * 2. VPN使用流程：
 *    VPN_HOME -> RegionSelection (返回)
 *    VPN_HOME -> Plans -> OrderCheckout -> OrderResult -> VPN_HOME/OrderList
 * 
 * 3. 钱包流程：
 *    WalletOnboarding -> WalletHome -> AssetDetail/Send/Receive
 *    WalletHome -> Send -> SendResult -> WalletHome
 * 
 * 4. 增长流程：
 *    Profile -> InviteCenter -> CommissionLedger -> Withdraw
 * 
 * 5. 法务流程：
 *    Profile -> LegalDocuments -> LegalDocumentDetail
 * 
 * 6. 认证流程：
 *    Login <-> Register
 *    Login -> ResetPassword -> Login
 */
