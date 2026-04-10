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
import com.cryptovpn.ui.p2.model.*
import com.cryptovpn.ui.p2.viewmodel.*
import com.cryptovpn.ui.pages.p2.*

fun NavGraphBuilder.installCryptoVpnP2CoreRoutes(
    navController: NavHostController,
    repository: CryptoVpnRepository = MockCryptoVpnRepository(),
) {
    composable(
        route = CryptoVpnRouteSpec.assetDetail.pattern,
        arguments = listOf(
    navArgument("assetId") {
        type = NavType.StringType
        defaultValue = "USDT"
    },
    navArgument("chainId") {
        type = NavType.StringType
        defaultValue = "tron"
    }
        ),
    ) { backStackEntry ->
        val args = AssetDetailRouteArgs(
    assetId = backStackEntry.arguments?.getString("assetId") ?: "USDT",
    chainId = backStackEntry.arguments?.getString("chainId") ?: "tron"
        )
        val vm: AssetDetailViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { AssetDetailViewModel(repository, args) },
        )
        AssetDetailRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.sendRoute("USDT","tron")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.receiveRoute("USDT","tron")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.receive.pattern,
        arguments = listOf(
    navArgument("assetId") {
        type = NavType.StringType
        defaultValue = "USDT"
    },
    navArgument("chainId") {
        type = NavType.StringType
        defaultValue = "tron"
    }
        ),
    ) { backStackEntry ->
        val args = ReceiveRouteArgs(
    assetId = backStackEntry.arguments?.getString("assetId") ?: "USDT",
    chainId = backStackEntry.arguments?.getString("chainId") ?: "tron"
        )
        val vm: ReceiveViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ReceiveViewModel(repository, args) },
        )
        ReceiveRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.send.pattern,
        arguments = listOf(
    navArgument("assetId") {
        type = NavType.StringType
        defaultValue = "USDT"
    },
    navArgument("chainId") {
        type = NavType.StringType
        defaultValue = "tron"
    }
        ),
    ) { backStackEntry ->
        val args = SendRouteArgs(
    assetId = backStackEntry.arguments?.getString("assetId") ?: "USDT",
    chainId = backStackEntry.arguments?.getString("chainId") ?: "tron"
        )
        val vm: SendViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SendViewModel(repository, args) },
        )
        SendRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.sendResultRoute("TX-9F32")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.gasSettingsRoute("ethereum")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.sendResult.pattern,
        arguments = listOf(
    navArgument("txId") {
        type = NavType.StringType
        defaultValue = "TX-9F32"
    }
        ),
    ) { backStackEntry ->
        val args = SendResultRouteArgs(
    txId = backStackEntry.arguments?.getString("txId") ?: "TX-9F32"
        )
        val vm: SendResultViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SendResultViewModel(repository, args) },
        )
        SendResultRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.assetDetailRoute("USDT","tron")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.inviteCenter.pattern) {
        val vm: InviteCenterViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { InviteCenterViewModel(repository) },
        )
        InviteCenterRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.commissionLedger.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.commissionLedger.pattern) {
        val vm: CommissionLedgerViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { CommissionLedgerViewModel(repository) },
        )
        CommissionLedgerRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.withdraw.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.inviteCenter.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.withdraw.pattern) {
        val vm: WithdrawViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WithdrawViewModel(repository) },
        )
        WithdrawRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.commissionLedger.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.commissionLedger.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.profile.pattern) {
        val vm: ProfileViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ProfileViewModel(repository) },
        )
        ProfileRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.securityCenter.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.legalDocuments.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.legalDocuments.pattern) {
        val vm: LegalDocumentsViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { LegalDocumentsViewModel(repository) },
        )
        LegalDocumentsRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.legalDocumentDetailRoute("terms_of_service")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.profile.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.legalDocumentDetail.pattern,
        arguments = listOf(
    navArgument("documentId") {
        type = NavType.StringType
        defaultValue = "terms_of_service"
    }
        ),
    ) { backStackEntry ->
        val args = LegalDocumentDetailRouteArgs(
    documentId = backStackEntry.arguments?.getString("documentId") ?: "terms_of_service"
        )
        val vm: LegalDocumentDetailViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { LegalDocumentDetailViewModel(repository, args) },
        )
        LegalDocumentDetailRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.legalDocuments.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.profile.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

}
