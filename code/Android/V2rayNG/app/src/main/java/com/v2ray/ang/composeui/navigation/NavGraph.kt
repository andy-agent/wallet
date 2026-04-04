package com.v2ray.ang.composeui.navigation

import androidx.compose.runtime.Composable
import com.v2ray.ang.composeui.bridge.auth.ComposeAuthBridge
import com.v2ray.ang.composeui.pages.splash.ComposeUpdateBridge

@Composable
fun CryptoVPNNavGraph(
    authBridge: ComposeAuthBridge,
    updateBridge: ComposeUpdateBridge,
    onOpenUrl: (String) -> Unit,
    onExitApp: () -> Unit,
    onAuthSuccess: () -> Unit,
    startDestination: String = Routes.SPLASH,
    onNavigationManagerReady: ((NavigationManager) -> Unit)? = null,
) {
    onNavigationManagerReady?.invoke(NavigationManager(startDestination))
    AppNavGraph(
        authBridge = authBridge,
        updateBridge = updateBridge,
        onOpenUrl = onOpenUrl,
        onOpenLegacyDestination = {},
        onExitApp = onExitApp,
        onAuthSuccess = onAuthSuccess,
        startDestination = startDestination,
    )
}
