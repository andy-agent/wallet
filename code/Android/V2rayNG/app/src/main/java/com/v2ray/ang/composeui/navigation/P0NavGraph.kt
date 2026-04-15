package com.v2ray.ang.composeui.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.cryptoVpnViewModelFactory
import com.v2ray.ang.composeui.p0.model.resolveContinueRoute
import com.v2ray.ang.composeui.p0.repository.P0Repository
import com.v2ray.ang.composeui.p0.viewmodel.*
import com.v2ray.ang.composeui.pages.p0.*


fun NavGraphBuilder.installCryptoVpnP0Routes(
    navController: NavHostController,
    p0Repository: P0Repository,
    repository: CryptoVpnRepository,
) {
    composable(CryptoVpnRouteSpec.splash.pattern) {
        val vm: SplashViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { SplashViewModel(p0Repository) },
        )
        SplashRoute(
            viewModel = vm,
            onFinished = { route ->
                navController.navigateSingleTop(
                    route.ifBlank { CryptoVpnRouteSpec.emailLogin.pattern },
                )
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
                navController.navigateSingleTop(vm.nextRoute ?: CryptoVpnRouteSpec.walletOnboarding.pattern)
            },
            onForgotPassword = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.resetPassword.pattern)
            },
            onRegister = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.emailRegister.pattern)
            },
            onWalletOnboarding = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.emailRegister.pattern)
            },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.walletOnboarding.pattern) {
        val vm: WalletOnboardingViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { WalletOnboardingViewModel(p0Repository) },
        )
        WalletOnboardingRoute(
            viewModel = vm,
            onCreateWallet = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.createWalletRoute("create"))
            },
            onImportWallet = {
                navController.navigateSingleTop(CryptoVpnRouteSpec.importWalletMethod.pattern)
            },
            onContinue = {
                navController.navigateSingleTop(vm.uiState.value.resolveContinueRoute())
            },
            onBottomNav = { navController.navigateSingleTop(it) },
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
            onReceive = { assetId, chainId ->
                when {
                    !vm.uiState.value.walletExists ->
                        navController.navigateSingleTop(CryptoVpnRouteSpec.walletOnboarding.pattern)

                    vm.uiState.value.walletNextAction.equals("BACKUP_MNEMONIC", ignoreCase = true) &&
                        !vm.uiState.value.walletId.isNullOrBlank() ->
                        navController.navigateSingleTop(
                            CryptoVpnRouteSpec.backupMnemonicRoute(vm.uiState.value.walletId!!),
                        )

                    vm.uiState.value.walletNextAction.equals("CONFIRM_MNEMONIC", ignoreCase = true) &&
                        !vm.uiState.value.walletId.isNullOrBlank() ->
                        navController.navigateSingleTop(
                            CryptoVpnRouteSpec.confirmMnemonicRoute(vm.uiState.value.walletId!!),
                        )

                    else ->
                        navController.navigateSingleTop(CryptoVpnRouteSpec.receiveRoute(assetId, chainId))
                }
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
            factory = cryptoVpnViewModelFactory { EmailRegisterViewModel(repository) },
        )
        EmailRegisterRoute(
            viewModel = vm,
            onPrimaryAction = {
                navController.navigateSingleTop(vm.nextRoute ?: CryptoVpnRouteSpec.walletOnboarding.pattern)
            },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.emailLogin.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }

    composable(CryptoVpnRouteSpec.resetPassword.pattern) {
        val vm: ResetPasswordViewModel = viewModel(
            factory = cryptoVpnViewModelFactory { ResetPasswordViewModel(repository) },
        )
        ResetPasswordRoute(
            viewModel = vm,
            onPrimaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.emailLogin.pattern) },
            onSecondaryAction = { navController.navigateSingleTop(CryptoVpnRouteSpec.emailLogin.pattern) },
            onBottomNav = { navController.navigateSingleTop(it) },
        )
    }
}
