package com.v2ray.ang.composeui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.cryptoVpnViewModelFactory
import com.v2ray.ang.composeui.p2extended.model.AddCustomTokenRouteArgs
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.BridgeRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ChainManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.CreateWalletRouteArgs
import com.v2ray.ang.composeui.p2extended.model.DappBrowserRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderRouteArgs
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicRouteArgs
import com.v2ray.ang.composeui.p2extended.model.NodeSpeedTestRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SignMessageConfirmRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailRouteArgs
import com.v2ray.ang.composeui.p2extended.model.SwapRouteArgs
import com.v2ray.ang.composeui.p2extended.model.TokenManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionRouteArgs
import com.v2ray.ang.composeui.p2extended.model.WalletManagerRouteArgs
import com.v2ray.ang.composeui.p2extended.viewmodel.AddCustomTokenViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.AutoConnectRulesViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.BackupMnemonicViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.BridgeViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.ChainManagerViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.ConfirmMnemonicViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.CreateWalletViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.DappBrowserViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.ExpiryReminderViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportMnemonicViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportWatchWalletViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportWalletMethodViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.NodeSpeedTestViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.SecurityCenterViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.SignMessageConfirmViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.SubscriptionDetailViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.SwapViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.TokenManagerViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.WalletManagerViewModel
import com.v2ray.ang.composeui.p2extended.viewmodel.WalletConnectSessionViewModel
import com.v2ray.ang.composeui.pages.p2extended.AddCustomTokenRoute
import com.v2ray.ang.composeui.pages.p2extended.AutoConnectRulesRoute
import com.v2ray.ang.composeui.pages.p2extended.BackupMnemonicRoute
import com.v2ray.ang.composeui.pages.p2extended.BridgeRoute
import com.v2ray.ang.composeui.pages.p2extended.ChainManagerRoute
import com.v2ray.ang.composeui.pages.p2extended.ConfirmMnemonicRoute
import com.v2ray.ang.composeui.pages.p2extended.CreateWalletRoute
import com.v2ray.ang.composeui.pages.p2extended.DappBrowserRoute
import com.v2ray.ang.composeui.pages.p2extended.ExpiryReminderRoute
import com.v2ray.ang.composeui.pages.p2extended.ImportMnemonicRoute
import com.v2ray.ang.composeui.pages.p2extended.ImportWatchWalletRoute
import com.v2ray.ang.composeui.pages.p2extended.ImportWalletMethodRoute
import com.v2ray.ang.composeui.pages.p2extended.NodeSpeedTestRoute
import com.v2ray.ang.composeui.pages.p2extended.SecurityCenterRoute
import com.v2ray.ang.composeui.pages.p2extended.SignMessageConfirmRoute
import com.v2ray.ang.composeui.pages.p2extended.SubscriptionDetailRoute
import com.v2ray.ang.composeui.pages.p2extended.SwapRoute
import com.v2ray.ang.composeui.pages.p2extended.TokenManagerRoute
import com.v2ray.ang.composeui.pages.p2extended.WalletManagerRoute
import com.v2ray.ang.composeui.pages.p2extended.WalletConnectSessionRoute

fun NavGraphBuilder.installCryptoVpnP2ExtendedRoutes(
    navController: NavHostController,
    repository: CryptoVpnRepository,
) {
    composable(
        route = CryptoVpnRouteSpec.subscriptionDetail.pattern,
        arguments = listOf(
            navArgument("subscriptionId") {
                type = NavType.StringType
                defaultValue = "current_subscription"
            },
        ),
    ) { backStackEntry ->
        val args = SubscriptionDetailRouteArgs(
            subscriptionId = backStackEntry.arguments?.getString("subscriptionId")
                ?: "current_subscription",
        )
        val vm: SubscriptionDetailViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SubscriptionDetailViewModel(repository, args) },
        )
        SubscriptionDetailRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.profile.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.expiryReminder.pattern,
        arguments = listOf(
            navArgument("daysLeft") {
                type = NavType.StringType
                defaultValue = "0"
            },
        ),
    ) { backStackEntry ->
        val args = ExpiryReminderRouteArgs(
            daysLeft = backStackEntry.arguments?.getString("daysLeft") ?: "0",
        )
        val vm: ExpiryReminderViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ExpiryReminderViewModel(repository, args) },
        )
        ExpiryReminderRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onSecondaryAction = {
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.subscriptionDetailRoute("current_subscription"),
                )
            },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.nodeSpeedTest.pattern,
        arguments = listOf(
            navArgument("nodeGroupId") {
                type = NavType.StringType
                defaultValue = "default_group"
            },
        ),
    ) { backStackEntry ->
        val args = NodeSpeedTestRouteArgs(
            nodeGroupId = backStackEntry.arguments?.getString("nodeGroupId") ?: "default_group",
        )
        val vm: NodeSpeedTestViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { NodeSpeedTestViewModel(repository, args) },
        )
        NodeSpeedTestRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.regionSelectionRoute()) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.autoConnectRules.pattern) {
        val vm: AutoConnectRulesViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { AutoConnectRulesViewModel(repository) },
        )
        AutoConnectRulesRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.securityCenter.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.createWallet.pattern,
        arguments = listOf(
            navArgument("mode") {
                type = NavType.StringType
                defaultValue = "create"
            },
        ),
    ) { backStackEntry ->
        val args = CreateWalletRouteArgs(
            mode = backStackEntry.arguments?.getString("mode") ?: "create",
        )
        val vm: CreateWalletViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { CreateWalletViewModel(repository, args) },
        )
        CreateWalletRoute(
            viewModel = vm,
            onPrimaryAction = { walletId ->
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.backupMnemonicRoute(walletId ?: "primary_wallet"),
                )
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importWalletMethod.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.importWalletMethod.pattern) {
        val vm: ImportWalletMethodViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ImportWalletMethodViewModel(repository) },
        )
        ImportWalletMethodRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importMnemonicRoute("onboarding")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importWatchWalletRoute()) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.importWatchWallet.pattern) {
        val vm: ImportWatchWalletViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ImportWatchWalletViewModel(repository) },
        )
        ImportWatchWalletRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletManagerRoute(it ?: "primary_wallet")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importWalletMethod.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.importMnemonic.pattern,
        arguments = listOf(
            navArgument("source") {
                type = NavType.StringType
                defaultValue = "onboarding"
            },
        ),
    ) { backStackEntry ->
        val args = ImportMnemonicRouteArgs(
            source = backStackEntry.arguments?.getString("source") ?: "onboarding",
        )
        val vm: ImportMnemonicViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ImportMnemonicViewModel(repository, args) },
        )
        ImportMnemonicRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importWalletMethod.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.backupMnemonic.pattern,
        arguments = listOf(
            navArgument("walletId") {
                type = NavType.StringType
                defaultValue = "primary_wallet"
            },
        ),
    ) { backStackEntry ->
        val args = BackupMnemonicRouteArgs(
            walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet",
        )
        val vm: BackupMnemonicViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { BackupMnemonicViewModel(repository, args) },
        )
        BackupMnemonicRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.confirmMnemonicRoute(args.walletId)) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletOnboarding.pattern) },
            onBottomNav = { route -> navController.navigateSingleTop(route) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.confirmMnemonic.pattern,
        arguments = listOf(
            navArgument("walletId") {
                type = NavType.StringType
                defaultValue = "primary_wallet"
            },
        ),
    ) { backStackEntry ->
        val args = ConfirmMnemonicRouteArgs(
            walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet",
        )
        val vm: ConfirmMnemonicViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ConfirmMnemonicViewModel(repository, args) },
        )
        ConfirmMnemonicRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.backupMnemonicRoute(args.walletId)) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.securityCenter.pattern) {
        val vm: SecurityCenterViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SecurityCenterViewModel(repository) },
        )
        SecurityCenterRoute(
            viewModel = vm,
            onSecondaryAction = { navController.navigateReset(CryptoVpnRouteSpec.emailLogin.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.walletManager.pattern,
        arguments = listOf(
            navArgument("walletId") {
                type = NavType.StringType
                defaultValue = "primary_wallet"
            },
        ),
    ) { backStackEntry ->
        val args = WalletManagerRouteArgs(
            walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet",
        )
        val vm: WalletManagerViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletManagerViewModel(repository, args) },
        )
        WalletManagerRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.createWalletRoute("create")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importWatchWalletRoute()) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.tokenManager.pattern,
        arguments = listOf(
            navArgument("walletId") {
                type = NavType.StringType
                defaultValue = "primary_wallet"
            },
            navArgument("chainId") {
                type = NavType.StringType
                defaultValue = "solana"
            },
        ),
    ) { backStackEntry ->
        val args = TokenManagerRouteArgs(
            walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet",
            chainId = backStackEntry.arguments?.getString("chainId") ?: "solana",
        )
        val vm: TokenManagerViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { TokenManagerViewModel(repository, args) },
        )
        TokenManagerRoute(
            viewModel = vm,
            onPrimaryAction = {
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.addCustomTokenRoute(args.walletId, args.chainId),
                )
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.chainManager.pattern,
        arguments = listOf(
            navArgument("walletId") {
                type = NavType.StringType
                defaultValue = "primary_wallet"
            },
        ),
    ) { backStackEntry ->
        val args = ChainManagerRouteArgs(
            walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet",
            chainId = "solana",
        )
        val vm: ChainManagerViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ChainManagerViewModel(repository, args) },
        )
        ChainManagerRoute(
            viewModel = vm,
            onPrimaryAction = {
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.addCustomTokenRoute(args.walletId, args.chainId),
                )
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.addCustomToken.pattern,
        arguments = listOf(
            navArgument("walletId") {
                type = NavType.StringType
                defaultValue = "primary_wallet"
            },
            navArgument("chainId") {
                type = NavType.StringType
                defaultValue = "tron"
            },
        ),
    ) { backStackEntry ->
        val args = AddCustomTokenRouteArgs(
            walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet",
            chainId = backStackEntry.arguments?.getString("chainId") ?: "tron",
        )
        val vm: AddCustomTokenViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { AddCustomTokenViewModel(repository, args) },
        )
        AddCustomTokenRoute(
            viewModel = vm,
            onPrimaryAction = {
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.tokenManagerRoute(args.walletId, args.chainId),
                )
            },
            onSecondaryAction = {
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.tokenManagerRoute(args.walletId, args.chainId),
                )
            },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.swap.pattern,
        arguments = listOf(
            navArgument("fromAsset") {
                type = NavType.StringType
                defaultValue = "USDT"
            },
            navArgument("toAsset") {
                type = NavType.StringType
                defaultValue = "SOL"
            },
        ),
    ) { backStackEntry ->
        val args = SwapRouteArgs(
            fromAsset = backStackEntry.arguments?.getString("fromAsset") ?: "USDT",
            toAsset = backStackEntry.arguments?.getString("toAsset") ?: "SOL",
        )
        val vm: SwapViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SwapViewModel(repository, args) },
        )
        SwapRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.bridgeRoute("tron", "solana")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.bridge.pattern,
        arguments = listOf(
            navArgument("fromChainId") {
                type = NavType.StringType
                defaultValue = "tron"
            },
            navArgument("toChainId") {
                type = NavType.StringType
                defaultValue = "solana"
            },
        ),
    ) { backStackEntry ->
        val args = BridgeRouteArgs(
            fromChainId = backStackEntry.arguments?.getString("fromChainId") ?: "tron",
            toChainId = backStackEntry.arguments?.getString("toChainId") ?: "solana",
        )
        val vm: BridgeViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { BridgeViewModel(repository, args) },
        )
        BridgeRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.swapRoute("USDT", "SOL")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.dappBrowser.pattern,
        arguments = listOf(
            navArgument("entry") {
                type = NavType.StringType
                defaultValue = "jup.ag"
            },
        ),
    ) { backStackEntry ->
        val args = DappBrowserRouteArgs(
            entry = backStackEntry.arguments?.getString("entry") ?: "jup.ag",
        )
        val vm: DappBrowserViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { DappBrowserViewModel(repository, args) },
        )
        DappBrowserRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletConnectSessionRoute("session_default")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.walletConnectSession.pattern,
        arguments = listOf(
            navArgument("sessionId") {
                type = NavType.StringType
                defaultValue = "session_default"
            },
        ),
    ) { backStackEntry ->
        val args = WalletConnectSessionRouteArgs(
            sessionId = backStackEntry.arguments?.getString("sessionId") ?: "session_default",
        )
        val vm: WalletConnectSessionViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletConnectSessionViewModel(repository, args) },
        )
        WalletConnectSessionRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.signMessageConfirmRoute("request_default")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.dappBrowserRoute("jup.ag")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.signMessageConfirm.pattern,
        arguments = listOf(
            navArgument("requestId") {
                type = NavType.StringType
                defaultValue = "request_default"
            },
        ),
    ) { backStackEntry ->
        val args = SignMessageConfirmRouteArgs(
            requestId = backStackEntry.arguments?.getString("requestId") ?: "request_default",
        )
        val vm: SignMessageConfirmViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SignMessageConfirmViewModel(repository, args) },
        )
        SignMessageConfirmRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletConnectSessionRoute("session_default")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }
}
