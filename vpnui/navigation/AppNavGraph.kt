package com.cryptovpn.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.cryptovpn.ui.common.repository.MockCryptoVpnRepository
import com.cryptovpn.ui.p0.repository.MockP0Repository

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = CryptoVpnRouteSpec.splash.pattern,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        installCryptoVpnAllRoutes(
            navController = navController,
            p0Repository = MockP0Repository(),
            repository = MockCryptoVpnRepository(),
        )
    }
}
