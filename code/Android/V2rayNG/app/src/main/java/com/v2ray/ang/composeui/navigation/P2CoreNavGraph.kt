package com.v2ray.ang.composeui.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.repository.MockCryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.cryptoVpnViewModelFactory
import com.v2ray.ang.composeui.p2.model.AssetDetailRouteArgs
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailRouteArgs
import com.v2ray.ang.composeui.p2.model.ReceiveRouteArgs
import com.v2ray.ang.composeui.p2.model.SendResultRouteArgs
import com.v2ray.ang.composeui.p2.model.SendRouteArgs
import com.v2ray.ang.composeui.p2.viewmodel.AboutAppViewModel
import com.v2ray.ang.composeui.p2.viewmodel.AssetDetailViewModel
import com.v2ray.ang.composeui.p2.viewmodel.CommissionLedgerViewModel
import com.v2ray.ang.composeui.p2.viewmodel.InviteCenterViewModel
import com.v2ray.ang.composeui.p2.viewmodel.InviteShareViewModel
import com.v2ray.ang.composeui.p2.viewmodel.LegalDocumentDetailViewModel
import com.v2ray.ang.composeui.p2.viewmodel.LegalDocumentsViewModel
import com.v2ray.ang.composeui.p2.viewmodel.ProfileViewModel
import com.v2ray.ang.composeui.p2.viewmodel.ReceiveViewModel
import com.v2ray.ang.composeui.p2.viewmodel.SendResultViewModel
import com.v2ray.ang.composeui.p2.viewmodel.SendViewModel
import com.v2ray.ang.composeui.p2.viewmodel.WithdrawViewModel
import com.v2ray.ang.composeui.pages.p2.AboutAppRoute
import com.v2ray.ang.composeui.pages.p2.AssetDetailRoute
import com.v2ray.ang.composeui.pages.p2.CommissionLedgerRoute
import com.v2ray.ang.composeui.pages.p2.InviteCenterRoute
import com.v2ray.ang.composeui.pages.p2.InviteShareRoute
import com.v2ray.ang.composeui.pages.p2.LegalDocumentDetailRoute
import com.v2ray.ang.composeui.pages.p2.LegalDocumentsRoute
import com.v2ray.ang.composeui.pages.p2.ProfileRoute
import com.v2ray.ang.composeui.pages.p2.ReceiveRoute
import com.v2ray.ang.composeui.pages.p2.SendResultRoute
import com.v2ray.ang.composeui.pages.p2.SendRoute
import com.v2ray.ang.composeui.pages.p2.WithdrawRoute

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
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.assetDetailRoute("USDT","tron")) },
            onBottomNav = { route -> navController.navigateSingleTop(route) },
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
            onBottomNav = { route -> navController.navigateSingleTop(route) },
        )
    }

    composable(CryptoVpnRouteSpec.inviteCenter.pattern) {
        val vm: InviteCenterViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { InviteCenterViewModel(repository) },
        )
        InviteCenterRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.commissionLedger.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.inviteShare.pattern) },
            onBottomNav = { route -> navController.navigateSingleTop(route) },
        )
    }

    composable(CryptoVpnRouteSpec.inviteShare.pattern) {
        val vm: InviteShareViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { InviteShareViewModel(repository) },
        )
        InviteShareRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.inviteCenter.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.commissionLedger.pattern) },
            onBottomNav = { route -> navController.navigateSingleTop(route) },
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
            onBottomNav = { route -> navController.navigateSingleTop(route) },
        )
    }

    composable(CryptoVpnRouteSpec.withdraw.pattern) {
        val context = LocalContext.current
        val vm: WithdrawViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WithdrawViewModel(repository, context) },
        )
        WithdrawRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.commissionLedger.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.commissionLedger.pattern) },
            onBottomNav = { route -> navController.navigateSingleTop(route) },
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
            onBottomNav = { route -> navController.navigateSingleTop(route) },
        )
    }

    composable(CryptoVpnRouteSpec.legalDocuments.pattern) {
        val vm: LegalDocumentsViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { LegalDocumentsViewModel(repository) },
        )
        LegalDocumentsRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.legalDocumentDetailRoute("terms_of_service")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.aboutApp.pattern) },
            onBottomNav = { route -> navController.navigateSingleTop(route) },
        )
    }

    composable(CryptoVpnRouteSpec.aboutApp.pattern) {
        val vm: AboutAppViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { AboutAppViewModel(repository) },
        )
        AboutAppRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.legalDocuments.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.profile.pattern) },
            onBottomNav = { route -> navController.navigateSingleTop(route) },
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
            onBottomNav = { route -> navController.navigateSingleTop(route) },
        )
    }

}
