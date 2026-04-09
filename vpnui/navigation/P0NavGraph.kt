package com.cryptovpn.navigation

import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.cryptovpn.ui.p0.repository.MockP0Repository
import com.cryptovpn.ui.p0.repository.P0Repository
import com.cryptovpn.ui.p0.viewmodel.LoginViewModel
import com.cryptovpn.ui.p0.viewmodel.SplashViewModel
import com.cryptovpn.ui.p0.viewmodel.VpnHomeViewModel
import com.cryptovpn.ui.p0.viewmodel.WalletHomeViewModel
import com.cryptovpn.ui.p0.viewmodel.WalletOnboardingViewModel
import com.cryptovpn.ui.p0.viewmodel.p0ViewModelFactory
import com.cryptovpn.ui.pages.p0.EmailLoginRoute
import com.cryptovpn.ui.pages.p0.SplashRoute
import com.cryptovpn.ui.pages.p0.VpnHomeRoute
import com.cryptovpn.ui.pages.p0.WalletHomeRoute
import com.cryptovpn.ui.pages.p0.WalletOnboardingRoute
import com.cryptovpn.ui.theme.CryptoVpnTheme

fun NavHostController.navigateSingleTop(route: String) {
    navigate(route) { launchSingleTop = true }
}

fun NavGraphBuilder.installCryptoVpnP0Routes(
    navController: NavHostController,
    repository: P0Repository = MockP0Repository(),
) {
    composable(Routes.SPLASH) {
        val vm: SplashViewModel = viewModel(
            factory = p0ViewModelFactory { SplashViewModel(repository) },
        )
        CryptoVpnTheme {
            SplashRoute(
                viewModel = vm,
                onFinished = { navController.navigateSingleTop(Routes.EMAIL_LOGIN) },
            )
        }
    }

    composable(Routes.EMAIL_LOGIN) {
        val vm: LoginViewModel = viewModel(
            factory = p0ViewModelFactory { LoginViewModel(repository) },
        )
        CryptoVpnTheme {
            EmailLoginRoute(
                viewModel = vm,
                onLoginSuccess = { navController.navigateSingleTop(Routes.VPN_HOME) },
                onForgotPassword = { navController.navigateSingleTop(Routes.RESET_PASSWORD) },
                onRegister = { navController.navigateSingleTop(Routes.EMAIL_REGISTER) },
                onWalletOnboarding = { navController.navigateSingleTop(Routes.WALLET_ONBOARDING) },
            )
        }
    }

    composable(Routes.WALLET_ONBOARDING) {
        val vm: WalletOnboardingViewModel = viewModel(
            factory = p0ViewModelFactory { WalletOnboardingViewModel(repository) },
        )
        CryptoVpnTheme {
            WalletOnboardingRoute(
                viewModel = vm,
                onContinue = { navController.navigateSingleTop(Routes.WALLET_HOME) },
            )
        }
    }

    composable(Routes.VPN_HOME) {
        val vm: VpnHomeViewModel = viewModel(
            factory = p0ViewModelFactory { VpnHomeViewModel(repository) },
        )
        CryptoVpnTheme {
            VpnHomeRoute(
                currentRoute = Routes.VPN_HOME,
                viewModel = vm,
                onBottomNav = { navController.navigateSingleTop(it) },
                onWalletHome = { navController.navigateSingleTop(Routes.WALLET_HOME) },
                onPlans = { navController.navigateSingleTop(Routes.PLANS) },
            )
        }
    }

    composable(Routes.WALLET_HOME) {
        val vm: WalletHomeViewModel = viewModel(
            factory = p0ViewModelFactory { WalletHomeViewModel(repository) },
        )
        CryptoVpnTheme {
            WalletHomeRoute(
                currentRoute = Routes.WALLET_HOME,
                viewModel = vm,
                onBottomNav = { navController.navigateSingleTop(it) },
                onReceive = { navController.navigateSingleTop(Routes.RECEIVE) },
                onSend = { navController.navigateSingleTop(Routes.SEND) },
            )
        }
    }
}
