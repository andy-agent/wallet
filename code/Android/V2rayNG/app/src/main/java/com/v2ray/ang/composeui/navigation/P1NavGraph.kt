package com.v2ray.ang.composeui.navigation

import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.repository.MockCryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.cryptoVpnViewModelFactory
import com.v2ray.ang.composeui.p1.model.*
import com.v2ray.ang.composeui.p1.viewmodel.*
import com.v2ray.ang.composeui.pages.p1.*

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
                defaultValue = ""
    }
        ),
    ) { backStackEntry ->
        val args = OrderCheckoutRouteArgs(
            planId = backStackEntry.arguments?.getString("planId") ?: ""
        )
        val vm: OrderCheckoutViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderCheckoutViewModel(repository, args) },
        )
        val uiState by vm.uiState.collectAsState()
        OrderCheckoutRoute(
            viewModel = vm,
            onPrimaryAction = {
                val orderNo = uiState.order?.orderNo
                if (!orderNo.isNullOrBlank()) {
                    navController.navigateSingleTop(CryptoVpnRouteSpec.walletPaymentConfirmRoute(orderNo))
                }
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.walletPaymentConfirm.pattern,
        arguments = listOf(
    navArgument("orderId") {
        type = NavType.StringType
                defaultValue = ""
    }
        ),
    ) { backStackEntry ->
        val args = WalletPaymentConfirmRouteArgs(
            orderId = backStackEntry.arguments?.getString("orderId") ?: ""
        )
        val vm: WalletPaymentConfirmViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletPaymentConfirmViewModel(repository, args) },
        )
        val uiState by vm.uiState.collectAsState()
        WalletPaymentConfirmRoute(
            viewModel = vm,
            onPrimaryAction = {
                val orderNo = uiState.order?.orderNo ?: args.orderId
                if (orderNo.isNotBlank()) {
                    navController.navigateSingleTop(CryptoVpnRouteSpec.orderResultRoute(orderNo))
                }
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.orderCheckoutRoute(uiState.order?.planCode ?: "")) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderResult.pattern,
        arguments = listOf(
    navArgument("orderId") {
        type = NavType.StringType
                defaultValue = ""
    }
        ),
    ) { backStackEntry ->
        val args = OrderResultRouteArgs(
            orderId = backStackEntry.arguments?.getString("orderId") ?: ""
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
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderDetail.pattern,
        arguments = listOf(
    navArgument("orderId") {
        type = NavType.StringType
                defaultValue = ""
    }
        ),
    ) { backStackEntry ->
        val args = OrderDetailRouteArgs(
            orderId = backStackEntry.arguments?.getString("orderId") ?: ""
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

}
