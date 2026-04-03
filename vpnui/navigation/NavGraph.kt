package com.cryptovpn.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink

/**
 * CryptoVPN 导航图配置
 * 
 * 定义所有页面路由和导航行为
 */
@Composable
fun CryptoVPNNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.Splash.FULL_ROUTE,
    onNavigationManagerReady: ((NavigationManager) -> Unit)? = null
) {
    // 初始化导航管理器
    val navigationManager = remember(navController) {
        NavigationManager(navController).also {
            NavigationManagerProvider.initialize(navController)
            onNavigationManagerReady?.invoke(it)
        }
    }
    
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // ==================== 启动与版本 ====================
        
        // Splash 启动页
        composable(
            route = Routes.Splash.FULL_ROUTE,
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.Splash.DEEP_LINK }
            )
        ) {
            // SplashScreen(navController = navController)
        }
        
        // Force Update 强制更新页
        composable(
            route = "${Routes.ForceUpdate.ROUTE}?" +
                    "${Routes.ForceUpdate.PARAM_VERSION}={${Routes.ForceUpdate.PARAM_VERSION}}&" +
                    "${Routes.ForceUpdate.PARAM_DOWNLOAD_URL}={${Routes.ForceUpdate.PARAM_DOWNLOAD_URL}}",
            arguments = listOf(
                navArgument(Routes.ForceUpdate.PARAM_VERSION) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.ForceUpdate.PARAM_DOWNLOAD_URL) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.ForceUpdate.DEEP_LINK }
            )
        ) { backStackEntry ->
            val version = backStackEntry.arguments?.getString(Routes.ForceUpdate.PARAM_VERSION) ?: ""
            val downloadUrl = backStackEntry.arguments?.getString(Routes.ForceUpdate.PARAM_DOWNLOAD_URL) ?: ""
            // ForceUpdateScreen(version = version, downloadUrl = downloadUrl)
        }
        
        // Optional Update 可选更新弹窗
        composable(
            route = "${Routes.OptionalUpdate.ROUTE}?" +
                    "${Routes.OptionalUpdate.PARAM_VERSION}={${Routes.OptionalUpdate.PARAM_VERSION}}&" +
                    "${Routes.OptionalUpdate.PARAM_DOWNLOAD_URL}={${Routes.OptionalUpdate.PARAM_DOWNLOAD_URL}}",
            arguments = listOf(
                navArgument(Routes.OptionalUpdate.PARAM_VERSION) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.OptionalUpdate.PARAM_DOWNLOAD_URL) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.OptionalUpdate.DEEP_LINK }
            )
        ) { backStackEntry ->
            val version = backStackEntry.arguments?.getString(Routes.OptionalUpdate.PARAM_VERSION) ?: ""
            val downloadUrl = backStackEntry.arguments?.getString(Routes.OptionalUpdate.PARAM_DOWNLOAD_URL) ?: ""
            // OptionalUpdateDialog(version = version, downloadUrl = downloadUrl)
        }
        
        // ==================== 认证 ====================
        
        // Email Login 邮箱登录页
        composable(
            route = "${Routes.EmailLogin.ROUTE}?${Routes.EmailLogin.PARAM_REDIRECT}={${Routes.EmailLogin.PARAM_REDIRECT}}",
            arguments = listOf(
                navArgument(Routes.EmailLogin.PARAM_REDIRECT) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.EmailLogin.DEEP_LINK }
            )
        ) { backStackEntry ->
            val redirect = backStackEntry.arguments?.getString(Routes.EmailLogin.PARAM_REDIRECT)
            // EmailLoginScreen(redirect = redirect)
        }
        
        // Email Register 邮箱注册页
        composable(
            route = "${Routes.EmailRegister.ROUTE}?${Routes.EmailRegister.PARAM_INVITE_CODE}={${Routes.EmailRegister.PARAM_INVITE_CODE}}",
            arguments = listOf(
                navArgument(Routes.EmailRegister.PARAM_INVITE_CODE) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.EmailRegister.DEEP_LINK }
            )
        ) { backStackEntry ->
            val inviteCode = backStackEntry.arguments?.getString(Routes.EmailRegister.PARAM_INVITE_CODE)
            // EmailRegisterScreen(inviteCode = inviteCode)
        }
        
        // Reset Password 重置密码页
        composable(
            route = "${Routes.ResetPassword.ROUTE}?" +
                    "${Routes.ResetPassword.PARAM_EMAIL}={${Routes.ResetPassword.PARAM_EMAIL}}&" +
                    "${Routes.ResetPassword.PARAM_TOKEN}={${Routes.ResetPassword.PARAM_TOKEN}}",
            arguments = listOf(
                navArgument(Routes.ResetPassword.PARAM_EMAIL) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Routes.ResetPassword.PARAM_TOKEN) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.ResetPassword.DEEP_LINK }
            )
        ) { backStackEntry ->
            val email = backStackEntry.arguments?.getString(Routes.ResetPassword.PARAM_EMAIL)
            val token = backStackEntry.arguments?.getString(Routes.ResetPassword.PARAM_TOKEN)
            // ResetPasswordScreen(email = email, token = token)
        }
        
        // ==================== VPN ====================
        
        // VPN Home VPN首页
        composable(
            route = "${Routes.VpnHome.ROUTE}?${Routes.VpnHome.PARAM_AUTO_CONNECT}={${Routes.VpnHome.PARAM_AUTO_CONNECT}}",
            arguments = listOf(
                navArgument(Routes.VpnHome.PARAM_AUTO_CONNECT) {
                    type = NavType.BoolType
                    defaultValue = false
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.VpnHome.DEEP_LINK }
            )
        ) { backStackEntry ->
            val autoConnect = backStackEntry.arguments?.getBoolean(Routes.VpnHome.PARAM_AUTO_CONNECT) ?: false
            // VpnHomeScreen(autoConnect = autoConnect)
        }
        
        // Plans 套餐页
        composable(
            route = "${Routes.Plans.ROUTE}?${Routes.Plans.PARAM_SELECTED_PLAN}={${Routes.Plans.PARAM_SELECTED_PLAN}}",
            arguments = listOf(
                navArgument(Routes.Plans.PARAM_SELECTED_PLAN) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.Plans.DEEP_LINK }
            )
        ) { backStackEntry ->
            val selectedPlan = backStackEntry.arguments?.getString(Routes.Plans.PARAM_SELECTED_PLAN)
            // PlansScreen(selectedPlan = selectedPlan)
        }
        
        // Region Selection 区域选择页
        composable(
            route = "${Routes.RegionSelection.ROUTE}?${Routes.RegionSelection.PARAM_CURRENT_REGION}={${Routes.RegionSelection.PARAM_CURRENT_REGION}}",
            arguments = listOf(
                navArgument(Routes.RegionSelection.PARAM_CURRENT_REGION) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.RegionSelection.DEEP_LINK }
            )
        ) { backStackEntry ->
            val currentRegion = backStackEntry.arguments?.getString(Routes.RegionSelection.PARAM_CURRENT_REGION)
            // RegionSelectionScreen(currentRegion = currentRegion)
        }
        
        // Order Checkout 订单收银台
        composable(
            route = "${Routes.OrderCheckout.ROUTE}/{${Routes.OrderCheckout.PARAM_PLAN_ID}}?" +
                    "${Routes.OrderCheckout.PARAM_DURATION}={${Routes.OrderCheckout.PARAM_DURATION}}",
            arguments = listOf(
                navArgument(Routes.OrderCheckout.PARAM_PLAN_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.OrderCheckout.PARAM_DURATION) {
                    type = NavType.IntType
                    defaultValue = 1
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.OrderCheckout.DEEP_LINK }
            )
        ) { backStackEntry ->
            val planId = backStackEntry.arguments?.getString(Routes.OrderCheckout.PARAM_PLAN_ID) ?: ""
            val duration = backStackEntry.arguments?.getInt(Routes.OrderCheckout.PARAM_DURATION) ?: 1
            // OrderCheckoutScreen(planId = planId, duration = duration)
        }
        
        // Wallet Payment Confirm 钱包支付确认页
        composable(
            route = "${Routes.WalletPaymentConfirm.ROUTE}/{${Routes.WalletPaymentConfirm.PARAM_ORDER_ID}}?" +
                    "${Routes.WalletPaymentConfirm.PARAM_AMOUNT}={${Routes.WalletPaymentConfirm.PARAM_AMOUNT}}&" +
                    "${Routes.WalletPaymentConfirm.PARAM_CURRENCY}={${Routes.WalletPaymentConfirm.PARAM_CURRENCY}}",
            arguments = listOf(
                navArgument(Routes.WalletPaymentConfirm.PARAM_ORDER_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.WalletPaymentConfirm.PARAM_AMOUNT) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Routes.WalletPaymentConfirm.PARAM_CURRENCY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.WalletPaymentConfirm.DEEP_LINK }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Routes.WalletPaymentConfirm.PARAM_ORDER_ID) ?: ""
            val amount = backStackEntry.arguments?.getString(Routes.WalletPaymentConfirm.PARAM_AMOUNT)
            val currency = backStackEntry.arguments?.getString(Routes.WalletPaymentConfirm.PARAM_CURRENCY)
            // WalletPaymentConfirmScreen(orderId = orderId, amount = amount, currency = currency)
        }
        
        // Order Result 订单结果页
        composable(
            route = "${Routes.OrderResult.ROUTE}/{${Routes.OrderResult.PARAM_ORDER_ID}}?" +
                    "${Routes.OrderResult.PARAM_STATUS}={${Routes.OrderResult.PARAM_STATUS}}",
            arguments = listOf(
                navArgument(Routes.OrderResult.PARAM_ORDER_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.OrderResult.PARAM_STATUS) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "pending"
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.OrderResult.DEEP_LINK }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Routes.OrderResult.PARAM_ORDER_ID) ?: ""
            val status = backStackEntry.arguments?.getString(Routes.OrderResult.PARAM_STATUS) ?: "pending"
            // OrderResultScreen(orderId = orderId, status = status)
        }
        
        // Order List 订单列表页
        composable(
            route = "${Routes.OrderList.ROUTE}?${Routes.OrderList.PARAM_STATUS_FILTER}={${Routes.OrderList.PARAM_STATUS_FILTER}}",
            arguments = listOf(
                navArgument(Routes.OrderList.PARAM_STATUS_FILTER) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.OrderList.DEEP_LINK }
            )
        ) { backStackEntry ->
            val statusFilter = backStackEntry.arguments?.getString(Routes.OrderList.PARAM_STATUS_FILTER)
            // OrderListScreen(statusFilter = statusFilter)
        }
        
        // Order Detail 订单详情页
        composable(
            route = "${Routes.OrderDetail.ROUTE}/{${Routes.OrderDetail.PARAM_ORDER_ID}}",
            arguments = listOf(
                navArgument(Routes.OrderDetail.PARAM_ORDER_ID) {
                    type = NavType.StringType
                    nullable = false
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.OrderDetail.DEEP_LINK }
            )
        ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString(Routes.OrderDetail.PARAM_ORDER_ID) ?: ""
            // OrderDetailScreen(orderId = orderId)
        }
        
        // ==================== 钱包 ====================
        
        // Wallet Onboarding 钱包引导页
        composable(
            route = "${Routes.WalletOnboarding.ROUTE}?${Routes.WalletOnboarding.PARAM_STEP}={${Routes.WalletOnboarding.PARAM_STEP}}",
            arguments = listOf(
                navArgument(Routes.WalletOnboarding.PARAM_STEP) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.WalletOnboarding.DEEP_LINK }
            )
        ) { backStackEntry ->
            val step = backStackEntry.arguments?.getString(Routes.WalletOnboarding.PARAM_STEP)
            // WalletOnboardingScreen(step = step)
        }
        
        // Wallet Home 钱包首页
        composable(
            route = "${Routes.WalletHome.ROUTE}?${Routes.WalletHome.PARAM_HIGHLIGHT_ASSET}={${Routes.WalletHome.PARAM_HIGHLIGHT_ASSET}}",
            arguments = listOf(
                navArgument(Routes.WalletHome.PARAM_HIGHLIGHT_ASSET) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.WalletHome.DEEP_LINK }
            )
        ) { backStackEntry ->
            val highlightAsset = backStackEntry.arguments?.getString(Routes.WalletHome.PARAM_HIGHLIGHT_ASSET)
            // WalletHomeScreen(highlightAsset = highlightAsset)
        }
        
        // Asset Detail 资产详情页
        composable(
            route = "${Routes.AssetDetail.ROUTE}/{${Routes.AssetDetail.PARAM_ASSET_ID}}?" +
                    "${Routes.AssetDetail.PARAM_CHAIN_ID}={${Routes.AssetDetail.PARAM_CHAIN_ID}}",
            arguments = listOf(
                navArgument(Routes.AssetDetail.PARAM_ASSET_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.AssetDetail.PARAM_CHAIN_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.AssetDetail.DEEP_LINK }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString(Routes.AssetDetail.PARAM_ASSET_ID) ?: ""
            val chainId = backStackEntry.arguments?.getString(Routes.AssetDetail.PARAM_CHAIN_ID)
            // AssetDetailScreen(assetId = assetId, chainId = chainId)
        }
        
        // Receive 收款页
        composable(
            route = "${Routes.Receive.ROUTE}/{${Routes.Receive.PARAM_ASSET_ID}}?" +
                    "${Routes.Receive.PARAM_CHAIN_ID}={${Routes.Receive.PARAM_CHAIN_ID}}",
            arguments = listOf(
                navArgument(Routes.Receive.PARAM_ASSET_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.Receive.PARAM_CHAIN_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.Receive.DEEP_LINK }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString(Routes.Receive.PARAM_ASSET_ID) ?: ""
            val chainId = backStackEntry.arguments?.getString(Routes.Receive.PARAM_CHAIN_ID)
            // ReceiveScreen(assetId = assetId, chainId = chainId)
        }
        
        // Send 发送页
        composable(
            route = "${Routes.Send.ROUTE}/{${Routes.Send.PARAM_ASSET_ID}}?" +
                    "${Routes.Send.PARAM_CHAIN_ID}={${Routes.Send.PARAM_CHAIN_ID}}&" +
                    "${Routes.Send.PARAM_TO_ADDRESS}={${Routes.Send.PARAM_TO_ADDRESS}}&" +
                    "${Routes.Send.PARAM_AMOUNT}={${Routes.Send.PARAM_AMOUNT}}",
            arguments = listOf(
                navArgument(Routes.Send.PARAM_ASSET_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.Send.PARAM_CHAIN_ID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Routes.Send.PARAM_TO_ADDRESS) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                },
                navArgument(Routes.Send.PARAM_AMOUNT) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.Send.DEEP_LINK }
            )
        ) { backStackEntry ->
            val assetId = backStackEntry.arguments?.getString(Routes.Send.PARAM_ASSET_ID) ?: ""
            val chainId = backStackEntry.arguments?.getString(Routes.Send.PARAM_CHAIN_ID)
            val toAddress = backStackEntry.arguments?.getString(Routes.Send.PARAM_TO_ADDRESS)
            val amount = backStackEntry.arguments?.getString(Routes.Send.PARAM_AMOUNT)
            // SendScreen(assetId = assetId, chainId = chainId, toAddress = toAddress, amount = amount)
        }
        
        // Send Result 发送结果页
        composable(
            route = "${Routes.SendResult.ROUTE}/{${Routes.SendResult.PARAM_TX_HASH}}?" +
                    "${Routes.SendResult.PARAM_STATUS}={${Routes.SendResult.PARAM_STATUS}}",
            arguments = listOf(
                navArgument(Routes.SendResult.PARAM_TX_HASH) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.SendResult.PARAM_STATUS) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = "pending"
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.SendResult.DEEP_LINK }
            )
        ) { backStackEntry ->
            val txHash = backStackEntry.arguments?.getString(Routes.SendResult.PARAM_TX_HASH) ?: ""
            val status = backStackEntry.arguments?.getString(Routes.SendResult.PARAM_STATUS) ?: "pending"
            // SendResultScreen(txHash = txHash, status = status)
        }
        
        // Wallet Payment 钱包支付确认页
        composable(
            route = "${Routes.WalletPayment.ROUTE}/{${Routes.WalletPayment.PARAM_REQUEST_ID}}?" +
                    "${Routes.WalletPayment.PARAM_PAYLOAD}={${Routes.WalletPayment.PARAM_PAYLOAD}}",
            arguments = listOf(
                navArgument(Routes.WalletPayment.PARAM_REQUEST_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.WalletPayment.PARAM_PAYLOAD) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.WalletPayment.DEEP_LINK }
            )
        ) { backStackEntry ->
            val requestId = backStackEntry.arguments?.getString(Routes.WalletPayment.PARAM_REQUEST_ID) ?: ""
            val payload = backStackEntry.arguments?.getString(Routes.WalletPayment.PARAM_PAYLOAD)
            // WalletPaymentScreen(requestId = requestId, payload = payload)
        }
        
        // ==================== 增长 ====================
        
        // Invite Center 邀请中心页
        composable(
            route = "${Routes.InviteCenter.ROUTE}?${Routes.InviteCenter.PARAM_HIGHLIGHT_TAB}={${Routes.InviteCenter.PARAM_HIGHLIGHT_TAB}}",
            arguments = listOf(
                navArgument(Routes.InviteCenter.PARAM_HIGHLIGHT_TAB) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.InviteCenter.DEEP_LINK }
            )
        ) { backStackEntry ->
            val highlightTab = backStackEntry.arguments?.getString(Routes.InviteCenter.PARAM_HIGHLIGHT_TAB)
            // InviteCenterScreen(highlightTab = highlightTab)
        }
        
        // Commission Ledger 佣金账本页
        composable(
            route = "${Routes.CommissionLedger.ROUTE}?${Routes.CommissionLedger.PARAM_PERIOD}={${Routes.CommissionLedger.PARAM_PERIOD}}",
            arguments = listOf(
                navArgument(Routes.CommissionLedger.PARAM_PERIOD) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.CommissionLedger.DEEP_LINK }
            )
        ) { backStackEntry ->
            val period = backStackEntry.arguments?.getString(Routes.CommissionLedger.PARAM_PERIOD)
            // CommissionLedgerScreen(period = period)
        }
        
        // Withdraw 提现申请页
        composable(
            route = "${Routes.Withdraw.ROUTE}?" +
                    "${Routes.Withdraw.PARAM_CURRENCY}={${Routes.Withdraw.PARAM_CURRENCY}}&" +
                    "${Routes.Withdraw.PARAM_MAX_AMOUNT}={${Routes.Withdraw.PARAM_MAX_AMOUNT}}",
            arguments = listOf(
                navArgument(Routes.Withdraw.PARAM_CURRENCY) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.Withdraw.PARAM_MAX_AMOUNT) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.Withdraw.DEEP_LINK }
            )
        ) { backStackEntry ->
            val currency = backStackEntry.arguments?.getString(Routes.Withdraw.PARAM_CURRENCY) ?: ""
            val maxAmount = backStackEntry.arguments?.getString(Routes.Withdraw.PARAM_MAX_AMOUNT)
            // WithdrawScreen(currency = currency, maxAmount = maxAmount)
        }
        
        // ==================== 我的与法务 ====================
        
        // Profile 我的页
        composable(
            route = Routes.Profile.FULL_ROUTE,
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.Profile.DEEP_LINK }
            )
        ) {
            // ProfileScreen()
        }
        
        // Legal Documents 法务文档列表页
        composable(
            route = "${Routes.LegalDocuments.ROUTE}?${Routes.LegalDocuments.PARAM_CATEGORY}={${Routes.LegalDocuments.PARAM_CATEGORY}}",
            arguments = listOf(
                navArgument(Routes.LegalDocuments.PARAM_CATEGORY) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.LegalDocuments.DEEP_LINK }
            )
        ) { backStackEntry ->
            val category = backStackEntry.arguments?.getString(Routes.LegalDocuments.PARAM_CATEGORY)
            // LegalDocumentsScreen(category = category)
        }
        
        // Legal Document Detail 法务文档详情页
        composable(
            route = "${Routes.LegalDocumentDetail.ROUTE}/{${Routes.LegalDocumentDetail.PARAM_DOC_ID}}?" +
                    "${Routes.LegalDocumentDetail.PARAM_DOC_TYPE}={${Routes.LegalDocumentDetail.PARAM_DOC_TYPE}}",
            arguments = listOf(
                navArgument(Routes.LegalDocumentDetail.PARAM_DOC_ID) {
                    type = NavType.StringType
                    nullable = false
                },
                navArgument(Routes.LegalDocumentDetail.PARAM_DOC_TYPE) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            ),
            deepLinks = listOf(
                navDeepLink { uriPattern = Routes.LegalDocumentDetail.DEEP_LINK }
            )
        ) { backStackEntry ->
            val docId = backStackEntry.arguments?.getString(Routes.LegalDocumentDetail.PARAM_DOC_ID) ?: ""
            val docType = backStackEntry.arguments?.getString(Routes.LegalDocumentDetail.PARAM_DOC_TYPE)
            // LegalDocumentDetailScreen(docId = docId, docType = docType)
        }
    }
}

/**
 * 导航图构建器
 * 用于测试和模块化导航
 */
class NavGraphBuilder {
    var startDestination: String = Routes.Splash.FULL_ROUTE
    val routes = mutableListOf<RouteConfig>()
    
    data class RouteConfig(
        val route: String,
        val hasArgs: Boolean = false,
        val args: List<ArgConfig> = emptyList(),
        val deepLinks: List<String> = emptyList()
    )
    
    data class ArgConfig(
        val name: String,
        val type: String,
        val nullable: Boolean = false,
        val defaultValue: Any? = null
    )
    
    fun route(route: String, config: RouteConfigBuilder.() -> Unit = {}) {
        val builder = RouteConfigBuilder(route).apply(config)
        routes.add(builder.build())
    }
    
    class RouteConfigBuilder(private val route: String) {
        private var hasArgs = false
        private val args = mutableListOf<ArgConfig>()
        private val deepLinks = mutableListOf<String>()
        
        fun arg(name: String, type: String, nullable: Boolean = false, defaultValue: Any? = null) {
            hasArgs = true
            args.add(ArgConfig(name, type, nullable, defaultValue))
        }
        
        fun deepLink(uri: String) {
            deepLinks.add(uri)
        }
        
        fun build(): RouteConfig {
            return RouteConfig(route, hasArgs, args, deepLinks)
        }
    }
}
