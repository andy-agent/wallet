package com.v2ray.ang.composeui.pages.p0

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p0.model.loginPreviewState
import com.v2ray.ang.composeui.p0.model.splashPreviewState
import com.v2ray.ang.composeui.p0.model.vpnHomePreviewState
import com.v2ray.ang.composeui.p0.model.walletHomePreviewState
import com.v2ray.ang.composeui.p0.model.walletOnboardingPreviewState
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SplashRoutePreview() {
    CryptoVpnTheme {
        SplashScreen(uiState = splashPreviewState())
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun EmailLoginRoutePreview() {
    CryptoVpnTheme {
        EmailLoginScreen(
            uiState = loginPreviewState(),
            onEmailChange = {},
            onPasswordChange = {},
            onPrimary = {},
            onDismissDialog = {},
            onRegister = {},
            onWalletImport = {},
            onForgotPassword = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletOnboardingRoutePreview() {
    CryptoVpnTheme {
        WalletOnboardingScreen(
            uiState = walletOnboardingPreviewState(),
            onSelectMode = {},
            onContinue = {},
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun VpnHomeRoutePreview() {
    CryptoVpnTheme {
        VpnHomeScreen(
            currentRoute = "vpn_home",
            uiState = vpnHomePreviewState(),
            onToggleConnection = {},
            onSelectRegion = {},
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
        WalletHomeScreen(
            currentRoute = "wallet_home",
            uiState = walletHomePreviewState(),
            onBottomNav = {},
            onRefresh = {},
            onWalletContextSelected = { _, _ -> },
            onCopyAddress = {},
            onCreateWallet = {},
            onOpenProfile = {},
            onOpenSecurityCenter = {},
            onOpenInviteCenter = {},
            onClearLocalWallet = {},
            onReceive = {},
            onSend = {},
        )
    }
}
