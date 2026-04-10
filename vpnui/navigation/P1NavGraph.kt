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
import com.cryptovpn.ui.p1.model.*
import com.cryptovpn.ui.p1.viewmodel.*
import com.cryptovpn.ui.pages.p1.*

fun NavGraphBuilder.installCryptoVpnP1Routes(
    navController: NavHostController,
    repository: CryptoVpnRepository = MockCryptoVpnRepository(),
) {
    composable(CryptoVpnRouteSpec.plans.pattern) {
        val vm: PlansViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { PlansViewModel(repository) },
        )
        PlansRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.orderCheckoutRoute("annual_pro")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.regionSelection.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.regionSelection.pattern) {
        val vm: RegionSelectionViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { RegionSelectionViewModel(repository) },
        )
        RegionSelectionRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderCheckout.pattern,
        arguments = listOf(
    navArgument("planId") {
        type = NavType.StringType
        defaultValue = "annual_pro"
    }
        ),
    ) { backStackEntry ->
        val args = OrderCheckoutRouteArgs(
    planId = backStackEntry.arguments?.getString("planId") ?: "annual_pro"
        )
        val vm: OrderCheckoutViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderCheckoutViewModel(repository, args) },
        )
        OrderCheckoutRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletPaymentConfirmRoute("ORD-2025-0001")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletPayment.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.walletPaymentConfirm.pattern,
        arguments = listOf(
    navArgument("orderId") {
        type = NavType.StringType
        defaultValue = "ORD-2025-0001"
    }
        ),
    ) { backStackEntry ->
        val args = WalletPaymentConfirmRouteArgs(
    orderId = backStackEntry.arguments?.getString("orderId") ?: "ORD-2025-0001"
        )
        val vm: WalletPaymentConfirmViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletPaymentConfirmViewModel(repository, args) },
        )
        WalletPaymentConfirmRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.orderResultRoute("ORD-2025-0001")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.orderCheckoutRoute("annual_pro")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderResult.pattern,
        arguments = listOf(
    navArgument("orderId") {
        type = NavType.StringType
        defaultValue = "ORD-2025-0001"
    }
        ),
    ) { backStackEntry ->
        val args = OrderResultRouteArgs(
    orderId = backStackEntry.arguments?.getString("orderId") ?: "ORD-2025-0001"
        )
        val vm: OrderResultViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderResultViewModel(repository, args) },
        )
        OrderResultRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.orderList.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.orderList.pattern) {
        val vm: OrderListViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderListViewModel(repository) },
        )
        OrderListRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.orderDetailRoute("ORD-2025-0001")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderDetail.pattern,
        arguments = listOf(
    navArgument("orderId") {
        type = NavType.StringType
        defaultValue = "ORD-2025-0001"
    }
        ),
    ) { backStackEntry ->
        val args = OrderDetailRouteArgs(
    orderId = backStackEntry.arguments?.getString("orderId") ?: "ORD-2025-0001"
        )
        val vm: OrderDetailViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderDetailViewModel(repository, args) },
        )
        OrderDetailRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.orderList.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.walletPayment.pattern) {
        val vm: WalletPaymentViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletPaymentViewModel(repository) },
        )
        WalletPaymentRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.walletPaymentConfirmRoute("ORD-WALLET-0001")) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

}
