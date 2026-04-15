package com.v2ray.ang.composeui.pages.p0

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.viewmodel.LoginViewModel
import com.v2ray.ang.composeui.p0.viewmodel.SplashViewModel
import com.v2ray.ang.composeui.p0.viewmodel.VpnHomeViewModel
import com.v2ray.ang.composeui.p0.viewmodel.WalletHomeViewModel
import com.v2ray.ang.composeui.p0.viewmodel.WalletOnboardingViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SplashRoutePreview() {
    CryptoVpnTheme {
        val repository = remember { MockP0Repository() }
        val viewModel = remember { SplashViewModel(repository) }
        SplashRoute(
            viewModel = viewModel,
            onFinished = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun EmailLoginRoutePreview() {
    CryptoVpnTheme {
        val repository = remember { MockP0Repository() }
        val viewModel = remember { LoginViewModel(repository) }
        EmailLoginRoute(
            viewModel = viewModel,
            onLoginSuccess = {},
            onForgotPassword = {},
            onRegister = {},
            onWalletOnboarding = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletOnboardingRoutePreview() {
    CryptoVpnTheme {
        val repository = remember { MockP0Repository() }
        val viewModel = remember { WalletOnboardingViewModel(repository) }
        WalletOnboardingRoute(
            viewModel = viewModel,
            onContinue = { },
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun VpnHomeRoutePreview() {
    CryptoVpnTheme {
        val repository = remember { MockP0Repository() }
        val viewModel = remember { VpnHomeViewModel(repository) }
        VpnHomeRoute(
            currentRoute = "vpn_home",
            viewModel = viewModel,
            onBottomNav = {},
            onWalletHome = {},
            onPlans = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletHomeRoutePreview() {
    CryptoVpnTheme {
        val repository = remember { MockP0Repository() }
        val viewModel = remember { WalletHomeViewModel(repository) }
        WalletHomeRoute(
            currentRoute = "wallet_home",
            viewModel = viewModel,
            onBottomNav = {},
            onReceive = { _, _ -> },
            onSend = {},
        )
    }
}
