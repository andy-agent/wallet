package com.app.core.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.app.core.ui.AdaptiveScaffold
import com.app.feature.auth.ui.ForceUpdateScreen
import com.app.feature.auth.ui.LoginScreen
import com.app.feature.auth.ui.RegisterScreen
import com.app.feature.auth.ui.ResetPasswordScreen
import com.app.feature.auth.ui.SplashScreen
import com.app.feature.auth.ui.VersionUpdateScreen
import com.app.feature.market.ui.MarketOverviewScreen
import com.app.feature.market.ui.MarketTickerDetailScreen
import com.app.feature.settings.ui.EffectLabScreen
import com.app.feature.settings.ui.LegalDetailScreen
import com.app.feature.settings.ui.LegalDocumentsScreen
import com.app.feature.settings.ui.ProfileScreen
import com.app.feature.vpn.ui.CommissionLedgerScreen
import com.app.feature.vpn.ui.InviteCenterScreen
import com.app.feature.vpn.ui.NodeDetailScreen
import com.app.feature.vpn.ui.NodeListScreen
import com.app.feature.vpn.ui.OrderDetailScreen
import com.app.feature.vpn.ui.OrderPaymentScreen
import com.app.feature.vpn.ui.OrderResultScreen
import com.app.feature.vpn.ui.OrderScreen
import com.app.feature.vpn.ui.OrdersCenterScreen
import com.app.feature.vpn.ui.PlanListScreen
import com.app.feature.vpn.ui.SubscriptionScreen
import com.app.feature.vpn.ui.VpnHomeScreen
import com.app.feature.vpn.ui.WithdrawCommissionScreen
import com.app.feature.wallet.ui.AddCustomTokenScreen
import com.app.feature.wallet.ui.AssetListScreen
import com.app.feature.wallet.ui.BackupMnemonicScreen
import com.app.feature.wallet.ui.BridgeScreen
import com.app.feature.wallet.ui.ChainManagerScreen
import com.app.feature.wallet.ui.DappBrowserScreen
import com.app.feature.wallet.ui.ImportWalletScreen
import com.app.feature.wallet.ui.InputMnemonicScreen
import com.app.feature.wallet.ui.ReceiveScreen
import com.app.feature.wallet.ui.SecurityCenterScreen
import com.app.feature.wallet.ui.SendResultScreen
import com.app.feature.wallet.ui.SendScreen
import com.app.feature.wallet.ui.SignRequestScreen
import com.app.feature.wallet.ui.SwapScreen
import com.app.feature.wallet.ui.TokenDetailScreen
import com.app.feature.wallet.ui.VerifyMnemonicScreen
import com.app.feature.wallet.ui.WalletConnectScreen
import com.app.feature.wallet.ui.WalletGuideScreen
import com.app.feature.wallet.ui.WalletHomeScreen
import com.app.feature.wallet.ui.WalletPaymentConfirmScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route.orEmpty()
    val showTopLevel = AppRoutes.topLevelRoutes.contains(currentRoute)

    AdaptiveScaffold(
        showNavigation = showTopLevel,
        currentRoute = currentRoute.ifBlank { AppRoutes.WalletHome },
        navItems = defaultBottomNavItems(),
        onNavigate = { route ->
            navController.navigate(route) {
                popUpTo(AppRoutes.WalletHome) { saveState = true }
                launchSingleTop = true
                restoreState = true
            }
        },
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = AppRoutes.Splash,
            modifier = Modifier.padding(padding),
        ) {
            composable(AppRoutes.Splash) {
                SplashScreen(
                    onGoLogin = { navController.navigate(AppRoutes.Login) { popUpTo(AppRoutes.Splash) { inclusive = true } } },
                    onGoHome = { navController.navigate(AppRoutes.WalletHome) { popUpTo(AppRoutes.Splash) { inclusive = true } } },
                    onForceUpdate = { navController.navigate(AppRoutes.ForceUpdate) },
                    onVersionUpdate = { navController.navigate(AppRoutes.VersionUpdate) },
                )
            }
            composable(AppRoutes.Login) {
                LoginScreen(
                    onLoginSuccess = { navController.navigate(AppRoutes.WalletHome) { popUpTo(AppRoutes.Login) { inclusive = true } } },
                    onRegister = { navController.navigate(AppRoutes.Register) },
                    onResetPassword = { navController.navigate(AppRoutes.ResetPassword) },
                    onOpenEffectLab = { navController.navigate(AppRoutes.EffectLab) },
                )
            }
            composable(AppRoutes.Register) {
                RegisterScreen(onBack = { navController.popBackStack() }, onRegistered = { navController.navigate(AppRoutes.WalletGuide) { popUpTo(AppRoutes.Register) { inclusive = true } } })
            }
            composable(AppRoutes.ResetPassword) { ResetPasswordScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.ForceUpdate) { ForceUpdateScreen() }
            composable(AppRoutes.VersionUpdate) {
                VersionUpdateScreen(
                    onSkip = { navController.navigate(AppRoutes.Login) { popUpTo(AppRoutes.VersionUpdate) { inclusive = true } } },
                    onUpdate = { navController.navigate(AppRoutes.Login) { popUpTo(AppRoutes.VersionUpdate) { inclusive = true } } },
                )
            }
            composable(AppRoutes.WalletGuide) {
                WalletGuideScreen(
                    onCreateWallet = { navController.navigate(AppRoutes.BackupMnemonic) },
                    onImportWallet = { navController.navigate(AppRoutes.ImportWallet) },
                )
            }
            composable(AppRoutes.ImportWallet) {
                ImportWalletScreen(onBack = { navController.popBackStack() }, onInputMnemonic = { navController.navigate(AppRoutes.InputMnemonic) })
            }
            composable(AppRoutes.InputMnemonic) {
                InputMnemonicScreen(onBack = { navController.popBackStack() }, onContinue = { navController.navigate(AppRoutes.VerifyMnemonic) })
            }
            composable(AppRoutes.BackupMnemonic) {
                BackupMnemonicScreen(onBack = { navController.popBackStack() }, onContinue = { navController.navigate(AppRoutes.VerifyMnemonic) })
            }
            composable(AppRoutes.VerifyMnemonic) {
                VerifyMnemonicScreen(onBack = { navController.popBackStack() }, onDone = { navController.navigate(AppRoutes.WalletHome) { popUpTo(AppRoutes.WalletGuide) { inclusive = true } } })
            }
            composable(AppRoutes.WalletHome) {
                WalletHomeScreen(
                    onOpenAssets = { navController.navigate(AppRoutes.AssetList) },
                    onOpenToken = { navController.navigate(AppRoutes.tokenDetail(it)) },
                    onOpenPlans = { navController.navigate(AppRoutes.PlanList) },
                    onOpenMarket = { navController.navigate(AppRoutes.MarketOverview) },
                )
            }
            composable(AppRoutes.AssetList) {
                AssetListScreen(onBack = { navController.popBackStack() }, onOpenToken = { navController.navigate(AppRoutes.tokenDetail(it)) })
            }
            composable(AppRoutes.TokenDetailPattern, arguments = listOf(navArgument(RouteArguments.SYMBOL) { type = NavType.StringType })) { entry ->
                val symbol = entry.arguments?.getString(RouteArguments.SYMBOL).orEmpty()
                TokenDetailScreen(symbol = symbol, onBack = { navController.popBackStack() }, onSend = { navController.navigate(AppRoutes.send(symbol)) }, onReceive = { navController.navigate(AppRoutes.receive(symbol)) })
            }
            composable(AppRoutes.SendPattern, arguments = listOf(navArgument(RouteArguments.SYMBOL) { type = NavType.StringType })) { entry ->
                val symbol = entry.arguments?.getString(RouteArguments.SYMBOL).orEmpty()
                SendScreen(symbol = symbol, onBack = { navController.popBackStack() }, onSent = { navController.navigate(AppRoutes.sendResult(it)) })
            }
            composable(AppRoutes.ReceivePattern, arguments = listOf(navArgument(RouteArguments.SYMBOL) { type = NavType.StringType })) { entry ->
                ReceiveScreen(symbol = entry.arguments?.getString(RouteArguments.SYMBOL).orEmpty(), onBack = { navController.popBackStack() })
            }
            composable(AppRoutes.SendResultPattern, arguments = listOf(navArgument(RouteArguments.TX_ID) { type = NavType.StringType })) { entry ->
                SendResultScreen(txId = entry.arguments?.getString(RouteArguments.TX_ID).orEmpty(), onBack = { navController.popBackStack() }, onDone = { navController.navigate(AppRoutes.AssetList) })
            }
            composable(AppRoutes.SecurityCenter) { SecurityCenterScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.ChainManager) { ChainManagerScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.AddCustomToken) { AddCustomTokenScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.Swap) { SwapScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.Bridge) { BridgeScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.DappBrowser) { DappBrowserScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.WalletConnect) { WalletConnectScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.SignRequestPattern, arguments = listOf(navArgument(RouteArguments.REQUEST_ID) { type = NavType.StringType })) { entry ->
                SignRequestScreen(requestId = entry.arguments?.getString(RouteArguments.REQUEST_ID).orEmpty(), onBack = { navController.popBackStack() }, onApprove = { navController.popBackStack() })
            }
            composable(AppRoutes.VpnHome) {
                VpnHomeScreen(
                    onOpenNodes = { navController.navigate(AppRoutes.NodeList) },
                    onOpenPlans = { navController.navigate(AppRoutes.PlanList) },
                    onOpenSubscription = { navController.navigate(AppRoutes.Subscription) },
                    onOpenOrders = { navController.navigate(AppRoutes.OrdersCenter) },
                )
            }
            composable(AppRoutes.NodeList) { NodeListScreen(onBack = { navController.popBackStack() }, onOpenNode = { navController.navigate(AppRoutes.nodeDetail(it)) }) }
            composable(AppRoutes.NodeDetailPattern, arguments = listOf(navArgument(RouteArguments.NODE_ID) { type = NavType.StringType })) { entry ->
                NodeDetailScreen(nodeId = entry.arguments?.getString(RouteArguments.NODE_ID).orEmpty(), onBack = { navController.popBackStack() })
            }
            composable(AppRoutes.Subscription) { SubscriptionScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.PlanList) { PlanListScreen(onBack = { navController.popBackStack() }, onSelectPlan = { navController.navigate(AppRoutes.order(it)) }) }
            composable(AppRoutes.OrderPattern, arguments = listOf(navArgument(RouteArguments.PLAN_ID) { type = NavType.StringType })) { entry ->
                OrderScreen(planId = entry.arguments?.getString(RouteArguments.PLAN_ID).orEmpty(), onBack = { navController.popBackStack() }, onNext = { navController.navigate(AppRoutes.orderPayment(it)) })
            }
            composable(AppRoutes.OrderPaymentPattern, arguments = listOf(navArgument(RouteArguments.ORDER_ID) { type = NavType.StringType })) { entry ->
                val orderId = entry.arguments?.getString(RouteArguments.ORDER_ID).orEmpty()
                OrderPaymentScreen(orderId = orderId, onBack = { navController.popBackStack() }, onConfirmWallet = { navController.navigate(AppRoutes.walletPaymentConfirm(orderId)) })
            }
            composable(AppRoutes.WalletPaymentConfirmPattern, arguments = listOf(navArgument(RouteArguments.ORDER_ID) { type = NavType.StringType })) { entry ->
                val orderId = entry.arguments?.getString(RouteArguments.ORDER_ID).orEmpty()
                WalletPaymentConfirmScreen(orderId = orderId, onBack = { navController.popBackStack() }, onConfirm = { navController.navigate(AppRoutes.orderResult(orderId)) })
            }
            composable(AppRoutes.OrderResultPattern, arguments = listOf(navArgument(RouteArguments.ORDER_ID) { type = NavType.StringType })) { entry ->
                OrderResultScreen(orderId = entry.arguments?.getString(RouteArguments.ORDER_ID).orEmpty(), onDone = { navController.navigate(AppRoutes.VpnHome) { popUpTo(AppRoutes.OrderResultPattern) { inclusive = true } } })
            }
            composable(AppRoutes.OrdersCenter) { OrdersCenterScreen(onBack = { navController.popBackStack() }, onOpenOrder = { navController.navigate(AppRoutes.orderDetail(it)) }) }
            composable(AppRoutes.OrderDetailPattern, arguments = listOf(navArgument(RouteArguments.ORDER_ID) { type = NavType.StringType })) { entry ->
                OrderDetailScreen(orderId = entry.arguments?.getString(RouteArguments.ORDER_ID).orEmpty(), onBack = { navController.popBackStack() })
            }
            composable(AppRoutes.InviteCenter) { InviteCenterScreen(onBack = { navController.popBackStack() }, onOpenLedger = { navController.navigate(AppRoutes.CommissionLedger) }, onOpenWithdraw = { navController.navigate(AppRoutes.WithdrawCommission) }) }
            composable(AppRoutes.CommissionLedger) { CommissionLedgerScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.WithdrawCommission) { WithdrawCommissionScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.MarketOverview) { MarketOverviewScreen(onOpenTicker = { navController.navigate(AppRoutes.marketTickerDetail(it)) }) }
            composable(AppRoutes.MarketTickerDetailPattern, arguments = listOf(navArgument(RouteArguments.SYMBOL) { type = NavType.StringType })) { entry ->
                MarketTickerDetailScreen(symbol = entry.arguments?.getString(RouteArguments.SYMBOL).orEmpty(), onBack = { navController.popBackStack() })
            }
            composable(AppRoutes.Profile) {
                ProfileScreen(
                    onOpenLegalDocs = { navController.navigate(AppRoutes.LegalDocuments) },
                    onLogout = { navController.navigate(AppRoutes.Login) { popUpTo(AppRoutes.Profile) { inclusive = true } } },
                    onOpenEffectLab = { navController.navigate(AppRoutes.EffectLab) },
                )
            }
            composable(AppRoutes.EffectLab) { EffectLabScreen(onBack = { navController.popBackStack() }) }
            composable(AppRoutes.LegalDocuments) { LegalDocumentsScreen(onBack = { navController.popBackStack() }, onOpenDoc = { navController.navigate(AppRoutes.legalDetail(it)) }) }
            composable(AppRoutes.LegalDetailPattern, arguments = listOf(navArgument(RouteArguments.DOC_ID) { type = NavType.StringType })) { entry ->
                LegalDetailScreen(docId = entry.arguments?.getString(RouteArguments.DOC_ID).orEmpty(), onBack = { navController.popBackStack() })
            }
        }
    }
}
