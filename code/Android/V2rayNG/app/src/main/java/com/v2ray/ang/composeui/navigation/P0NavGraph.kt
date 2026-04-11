package com.v2ray.ang.composeui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.repository.MockCryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.cryptoVpnViewModelFactory
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.repository.P0Repository
import com.v2ray.ang.composeui.p0.viewmodel.*
import com.v2ray.ang.composeui.pages.p0.*


fun NavGraphBuilder.installCryptoVpnP0Routes(
    navController: NavHostController,
    p0Repository: P0Repository = MockP0Repository(),
    repository: CryptoVpnRepository = MockCryptoVpnRepository(),
) {
    composable(CryptoVpnRouteSpec.splash.pattern) {
        val vm: SplashViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SplashViewModel(p0Repository) },
        )
        SplashRoute(
            viewModel = vm,
            onFinished = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.emailLogin.pattern)
            },
        )
    }

    composable(CryptoVpnRouteSpec.emailLogin.pattern) {
        val vm: LoginViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { LoginViewModel(p0Repository) },
        )
        EmailLoginRoute(
            viewModel = vm,
            onLoginSuccess = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern)
            },
            onForgotPassword = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.resetPassword.pattern)
            },
            onRegister = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.emailRegister.pattern)
            },
            onWalletOnboarding = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.walletOnboarding.pattern)
            },
        )
    }

    composable(CryptoVpnRouteSpec.walletOnboarding.pattern) {
        val vm: WalletOnboardingViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletOnboardingViewModel(p0Repository) },
        )
        WalletOnboardingRoute(
            viewModel = vm,
            onContinue = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern)
            },
        )
    }

    composable(CryptoVpnRouteSpec.vpnHome.pattern) {
        val vm: VpnHomeViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { VpnHomeViewModel(p0Repository) },
        )
        VpnHomeRoute(
            currentRoute = CryptoVpnRouteSpec.vpnHome.name,
            viewModel = vm,
            onBottomNav = { navController.navigateSingleTop(it) },
            onWalletHome = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.walletHome.pattern)
            },
            onPlans = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.plans.pattern)
            },
        )
    }

    composable(CryptoVpnRouteSpec.walletHome.pattern) {
        val vm: WalletHomeViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletHomeViewModel(p0Repository) },
        )
        WalletHomeRoute(
            currentRoute = CryptoVpnRouteSpec.walletHome.name,
            viewModel = vm,
            onBottomNav = { navController.navigateSingleTop(it) },
            onReceive = {
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.receiveRoute("USDT", "tron"),
                )
            },
            onSend = {
                navController.navigateSingleTop(
                    CryptoVpnRouteSpec.sendRoute("USDT", "tron"),
                )
            },
        )
    }
    composable(CryptoVpnRouteSpec.forceUpdate.pattern) {
        val vm: ForceUpdateViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ForceUpdateViewModel(repository) },
        )
        ForceUpdateRoute(
            viewModel = vm,
            onPrimaryAction = {},
            onSecondaryAction = null,
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.optionalUpdate.pattern) {
        val vm: OptionalUpdateViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { OptionalUpdateViewModel(repository) },
        )
        OptionalUpdateRoute(
            viewModel = vm,
            onPrimaryAction = {},
            onSecondaryAction = null,
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.emailRegister.pattern) {
        val vm: EmailRegisterViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { EmailRegisterViewModel(repository, p0Repository) },
        )
        EmailRegisterRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.vpnHome.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.emailLogin.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.resetPassword.pattern) {
        val vm: ResetPasswordViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ResetPasswordViewModel(repository, p0Repository) },
        )
        ResetPasswordRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.emailLogin.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.emailLogin.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }
}
