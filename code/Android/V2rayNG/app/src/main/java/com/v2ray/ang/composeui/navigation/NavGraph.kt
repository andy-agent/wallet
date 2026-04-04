package com.v2ray.ang.composeui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController

@Composable
fun CryptoVPNNavGraph(
    navController: NavHostController = rememberNavController(),
    startDestination: String = Routes.SPLASH,
    onNavigationManagerReady: ((NavigationManager) -> Unit)? = null,
) {
    val navigationManager = NavigationManager(navController)
    onNavigationManagerReady?.invoke(navigationManager)
    AppNavGraph(
        navController = navController,
        startDestination = startDestination,
    )
}
