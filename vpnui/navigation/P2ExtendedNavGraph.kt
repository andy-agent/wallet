package com.cryptovpn.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.repository.MockCryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.cryptoVpnViewModelFactory
import com.cryptovpn.ui.p2extended.model.*
import com.cryptovpn.ui.p2extended.viewmodel.*
import com.cryptovpn.ui.pages.p2extended.*

fun NavGraphBuilder.installCryptoVpnP2ExtendedRoutes(
    navController: NavHostController,
    repository: CryptoVpnRepository = MockCryptoVpnRepository(),
) {
    composable(
        route = CryptoVpnRouteSpec.subscriptionDetail.pattern,
        arguments = listOf(
    navArgument("subscriptionId") {
        type = NavType.StringType
        defaultValue = "pro_mesh_30d"
    }
        ),
    ) { backStackEntry ->
        val args = SubscriptionDetailRouteArgs(
    subscriptionId = backStackEntry.arguments?.getString("subscriptionId") ?: "pro_mesh_30d"
        )
        val vm: SubscriptionDetailViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SubscriptionDetailViewModel(repository, args) },
        )
        SubscriptionDetailRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.expiryReminderRoute("5")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.expiryReminder.pattern,
        arguments = listOf(
    navArgument("daysLeft") {
        type = NavType.StringType
        defaultValue = "5"
    }
        ),
    ) { backStackEntry ->
        val args = ExpiryReminderRouteArgs(
    daysLeft = backStackEntry.arguments?.getString("daysLeft") ?: "5"
        )
        val vm: ExpiryReminderViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ExpiryReminderViewModel(repository, args) },
        )
        ExpiryReminderRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.subscriptionDetailRoute("pro_mesh_30d")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.nodeSpeedTest.pattern,
        arguments = listOf(
    navArgument("nodeGroupId") {
        type = NavType.StringType
        defaultValue = "premium_apac"
    }
        ),
    ) { backStackEntry ->
        val args = NodeSpeedTestRouteArgs(
    nodeGroupId = backStackEntry.arguments?.getString("nodeGroupId") ?: "premium_apac"
        )
        val vm: NodeSpeedTestViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { NodeSpeedTestViewModel(repository, args) },
        )
        NodeSpeedTestRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.regionSelection.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
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
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.profile.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.createWallet.pattern,
        arguments = listOf(
    navArgument("mode") {
        type = NavType.StringType
        defaultValue = "create"
    }
        ),
    ) { backStackEntry ->
        val args = CreateWalletRouteArgs(
    mode = backStackEntry.arguments?.getString("mode") ?: "create"
        )
        val vm: CreateWalletViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { CreateWalletViewModel(repository, args) },
        )
        CreateWalletRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.backupMnemonicRoute("primary_wallet")) },
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
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importPrivateKeyRoute("ethereum")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.importMnemonic.pattern,
        arguments = listOf(
    navArgument("source") {
        type = NavType.StringType
        defaultValue = "onboarding"
    }
        ),
    ) { backStackEntry ->
        val args = ImportMnemonicRouteArgs(
    source = backStackEntry.arguments?.getString("source") ?: "onboarding"
        )
        val vm: ImportMnemonicViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ImportMnemonicViewModel(repository, args) },
        )
        ImportMnemonicRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.securityCenter.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.importWalletMethod.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.importPrivateKey.pattern,
        arguments = listOf(
    navArgument("chainId") {
        type = NavType.StringType
        defaultValue = "ethereum"
    }
        ),
    ) { backStackEntry ->
        val args = ImportPrivateKeyRouteArgs(
    chainId = backStackEntry.arguments?.getString("chainId") ?: "ethereum"
        )
        val vm: ImportPrivateKeyViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ImportPrivateKeyViewModel(repository, args) },
        )
        ImportPrivateKeyRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.securityCenter.pattern) },
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
    }
        ),
    ) { backStackEntry ->
        val args = BackupMnemonicRouteArgs(
    walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet"
        )
        val vm: BackupMnemonicViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { BackupMnemonicViewModel(repository, args) },
        )
        BackupMnemonicRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.confirmMnemonicRoute("primary_wallet")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.createWalletRoute("create")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.confirmMnemonic.pattern,
        arguments = listOf(
    navArgument("walletId") {
        type = NavType.StringType
        defaultValue = "primary_wallet"
    }
        ),
    ) { backStackEntry ->
        val args = ConfirmMnemonicRouteArgs(
    walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet"
        )
        val vm: ConfirmMnemonicViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ConfirmMnemonicViewModel(repository, args) },
        )
        ConfirmMnemonicRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.securityCenter.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.backupMnemonicRoute("primary_wallet")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.securityCenter.pattern) {
        val vm: SecurityCenterViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SecurityCenterViewModel(repository) },
        )
        SecurityCenterRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.riskAuthorizations.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.profile.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.chainManager.pattern,
        arguments = listOf(
    navArgument("walletId") {
        type = NavType.StringType
        defaultValue = "primary_wallet"
    }
        ),
    ) { backStackEntry ->
        val args = ChainManagerRouteArgs(
    walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet"
        )
        val vm: ChainManagerViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ChainManagerViewModel(repository, args) },
        )
        ChainManagerRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.addCustomTokenRoute("solana")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.addCustomToken.pattern,
        arguments = listOf(
    navArgument("chainId") {
        type = NavType.StringType
        defaultValue = "base"
    }
        ),
    ) { backStackEntry ->
        val args = AddCustomTokenRouteArgs(
    chainId = backStackEntry.arguments?.getString("chainId") ?: "base"
        )
        val vm: AddCustomTokenViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { AddCustomTokenViewModel(repository, args) },
        )
        AddCustomTokenRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.chainManagerRoute("primary_wallet")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.walletManager.pattern,
        arguments = listOf(
    navArgument("walletId") {
        type = NavType.StringType
        defaultValue = "primary_wallet"
    }
        ),
    ) { backStackEntry ->
        val args = WalletManagerRouteArgs(
    walletId = backStackEntry.arguments?.getString("walletId") ?: "primary_wallet"
        )
        val vm: WalletManagerViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletManagerViewModel(repository, args) },
        )
        WalletManagerRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.addressBookRoute("send")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.profile.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.addressBook.pattern,
        arguments = listOf(
    navArgument("mode") {
        type = NavType.StringType
        defaultValue = "send"
    }
        ),
    ) { backStackEntry ->
        val args = AddressBookRouteArgs(
    mode = backStackEntry.arguments?.getString("mode") ?: "send"
        )
        val vm: AddressBookViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { AddressBookViewModel(repository, args) },
        )
        AddressBookRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.sendRoute("USDT","tron")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletManagerRoute("primary_wallet")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.gasSettings.pattern,
        arguments = listOf(
    navArgument("chainId") {
        type = NavType.StringType
        defaultValue = "ethereum"
    }
        ),
    ) { backStackEntry ->
        val args = GasSettingsRouteArgs(
    chainId = backStackEntry.arguments?.getString("chainId") ?: "ethereum"
        )
        val vm: GasSettingsViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { GasSettingsViewModel(repository, args) },
        )
        GasSettingsRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.sendRoute("ETH","ethereum")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.sendRoute("ETH","ethereum")) },
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
    }
        ),
    ) { backStackEntry ->
        val args = SwapRouteArgs(
    fromAsset = backStackEntry.arguments?.getString("fromAsset") ?: "USDT",
    toAsset = backStackEntry.arguments?.getString("toAsset") ?: "SOL"
        )
        val vm: SwapViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SwapViewModel(repository, args) },
        )
        SwapRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
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
    }
        ),
    ) { backStackEntry ->
        val args = BridgeRouteArgs(
    fromChainId = backStackEntry.arguments?.getString("fromChainId") ?: "tron",
    toChainId = backStackEntry.arguments?.getString("toChainId") ?: "solana"
        )
        val vm: BridgeViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { BridgeViewModel(repository, args) },
        )
        BridgeRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.dappBrowser.pattern,
        arguments = listOf(
    navArgument("entry") {
        type = NavType.StringType
        defaultValue = "jup.ag"
    }
        ),
    ) { backStackEntry ->
        val args = DappBrowserRouteArgs(
    entry = backStackEntry.arguments?.getString("entry") ?: "jup.ag"
        )
        val vm: DappBrowserViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { DappBrowserViewModel(repository, args) },
        )
        DappBrowserRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletConnectSessionRoute("session_jupiter")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.walletConnectSession.pattern,
        arguments = listOf(
    navArgument("sessionId") {
        type = NavType.StringType
        defaultValue = "session_jupiter"
    }
        ),
    ) { backStackEntry ->
        val args = WalletConnectSessionRouteArgs(
    sessionId = backStackEntry.arguments?.getString("sessionId") ?: "session_jupiter"
        )
        val vm: WalletConnectSessionViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletConnectSessionViewModel(repository, args) },
        )
        WalletConnectSessionRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.signMessageConfirmRoute("req_001")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.signMessageConfirm.pattern,
        arguments = listOf(
    navArgument("requestId") {
        type = NavType.StringType
        defaultValue = "req_001"
    }
        ),
    ) { backStackEntry ->
        val args = SignMessageConfirmRouteArgs(
    requestId = backStackEntry.arguments?.getString("requestId") ?: "req_001"
        )
        val vm: SignMessageConfirmViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SignMessageConfirmViewModel(repository, args) },
        )
        SignMessageConfirmRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.riskAuthorizations.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.riskAuthorizations.pattern) {
        val vm: RiskAuthorizationsViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { RiskAuthorizationsViewModel(repository) },
        )
        RiskAuthorizationsRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.securityCenter.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.nftGallery.pattern) {
        val vm: NftGalleryViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { NftGalleryViewModel(repository) },
        )
        NftGalleryRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.dappBrowserRoute("magiceden.io")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.stakingEarn.pattern) {
        val vm: StakingEarnViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { StakingEarnViewModel(repository) },
        )
        StakingEarnRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.assetDetailRoute("USDT","tron")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

}
