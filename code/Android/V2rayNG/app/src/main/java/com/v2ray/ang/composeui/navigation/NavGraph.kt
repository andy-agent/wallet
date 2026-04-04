package com.v2ray.ang.composeui.navigation

import androidx.compose.runtime.Composable

@Composable
fun CryptoVPNNavGraph(
    navController: Any? = null,
    startDestination: String = Routes.SPLASH,
    onNavigationManagerReady: ((NavigationManager) -> Unit)? = null,
) {
    onNavigationManagerReady?.invoke(NavigationManager(navController))
    AppNavGraph(
        navController = navController,
        startDestination = startDestination,
    )
}
