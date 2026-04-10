package com.v2ray.ang.composeui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.repository.RealCryptoVpnRepository
import com.v2ray.ang.composeui.p0.repository.P0Repository
import com.v2ray.ang.composeui.p0.repository.RealP0Repository

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = CryptoVpnRouteSpec.splash.pattern,
    p0Repository: P0Repository,
    repository: CryptoVpnRepository,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        installCryptoVpnAllRoutes(
            navController = navController,
            p0Repository = p0Repository,
            repository = repository,
        )
    }
}
