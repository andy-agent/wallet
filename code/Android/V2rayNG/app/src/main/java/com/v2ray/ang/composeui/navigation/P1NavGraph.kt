package com.v2ray.ang.composeui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.cryptoVpnViewModelFactory
import com.v2ray.ang.composeui.p1.model.*
import com.v2ray.ang.composeui.p1.viewmodel.*
import com.v2ray.ang.composeui.pages.p1.*

fun NavGraphBuilder.installCryptoVpnP1Routes(
    navController: NavHostController,
    repository: CryptoVpnRepository,
) {
    composable(CryptoVpnRouteSpec.plans.pattern) {
        val vm: PlansViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { PlansViewModel(repository) },
        )
        PlansRoute(
            viewModel = vm,
            onPrimaryAction = { planCode ->
                navController.navigateSingleTop(CryptoVpnRouteSpec.orderCheckoutRoute(planCode))
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.regionSelectionRoute()) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.regionSelection.pattern,
        arguments = listOf(
            navArgument("planId") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) { backStackEntry ->
        val planId = backStackEntry.arguments?.getString("planId")
        val vm: RegionSelectionViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { RegionSelectionViewModel(repository) },
        )
        RegionSelectionRoute(
            viewModel = vm,
            onPrimaryAction = {
                if (!planId.isNullOrBlank()) {
                    navController.navigateSingleTop(CryptoVpnRouteSpec.orderCheckoutRoute(planId))
                } else {
                    navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern)
                }
            },
            onSecondaryAction = {
                if (!planId.isNullOrBlank()) {
                    navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern)
                } else {
                    navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern)
                }
            },
            emptyActionLabel = if (!planId.isNullOrBlank()) {
                "暂无节点，继续支付"
            } else {
                "返回首页继续连接"
            },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderCheckout.pattern,
        arguments = listOf(
            navArgument("planId") {
                type = NavType.StringType
            },
            navArgument("assetCode") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
            navArgument("networkCode") {
                type = NavType.StringType
                nullable = true
                defaultValue = null
            },
        ),
    ) { backStackEntry ->
        val args = OrderCheckoutRouteArgs(
            planId = backStackEntry.arguments?.getString("planId").orEmpty(),
            assetCode = backStackEntry.arguments?.getString("assetCode").orEmpty(),
            networkCode = backStackEntry.arguments?.getString("networkCode").orEmpty(),
        )
        val vm: OrderCheckoutViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderCheckoutViewModel(repository, args) },
        )
        OrderCheckoutRoute(
            viewModel = vm,
            onPrimaryAction = { orderNo ->
                navController.navigateSingleTop(CryptoVpnRouteSpec.walletPaymentConfirmRoute(orderNo))
            },
            onSecondaryAction = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern)
            },
            onPaymentOptionRoute = { route ->
                navController.navigate(route)
            },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.walletPaymentConfirm.pattern,
        arguments = listOf(
            navArgument("orderId") {
                type = NavType.StringType
            },
        ),
    ) { backStackEntry ->
        val args = WalletPaymentConfirmRouteArgs(
            orderId = backStackEntry.arguments?.getString("orderId").orEmpty(),
        )
        val vm: WalletPaymentConfirmViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletPaymentConfirmViewModel(repository, args) },
        )
        WalletPaymentConfirmRoute(
            viewModel = vm,
            onPrimaryAction = { orderNo ->
                navController.navigateSingleTop(CryptoVpnRouteSpec.orderResultRoute(orderNo))
            },
            onSecondaryAction = { planCode ->
                navController.navigateSingleTop(CryptoVpnRouteSpec.orderCheckoutRoute(planCode))
            },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderResult.pattern,
        arguments = listOf(
            navArgument("orderId") {
                type = NavType.StringType
            },
        ),
    ) { backStackEntry ->
        val args = OrderResultRouteArgs(
            orderId = backStackEntry.arguments?.getString("orderId").orEmpty(),
        )
        val vm: OrderResultViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderResultViewModel(repository, args) },
        )
        OrderResultRoute(
            viewModel = vm,
            onPrimaryAction = {},
            onSecondaryAction = { orderNo ->
                navController.navigateSingleTop(CryptoVpnRouteSpec.orderDetailRoute(orderNo))
            },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.orderList.pattern) {
        val vm: OrderListViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OrderListViewModel(repository) },
        )
        OrderListRoute(
            viewModel = vm,
            onPrimaryAction = { orderNo ->
                navController.navigateSingleTop(CryptoVpnRouteSpec.orderDetailRoute(orderNo))
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(
        route = CryptoVpnRouteSpec.orderDetail.pattern,
        arguments = listOf(
            navArgument("orderId") {
                type = NavType.StringType
            },
        ),
    ) { backStackEntry ->
        val args = OrderDetailRouteArgs(
            orderId = backStackEntry.arguments?.getString("orderId").orEmpty(),
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
            onPrimaryAction = {
                val orderId = repository.getCurrentOrderIdHint() ?: "ORD-2025-0001"
                navController.navigateSingleTop(CryptoVpnRouteSpec.walletPaymentConfirmRoute(orderId))
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

}
