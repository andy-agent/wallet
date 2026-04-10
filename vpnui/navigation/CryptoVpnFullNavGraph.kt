package com.cryptovpn.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.repository.MockCryptoVpnRepository
import com.cryptovpn.ui.p0.repository.MockP0Repository
import com.cryptovpn.ui.p0.repository.P0Repository

fun NavGraphBuilder.installCryptoVpnAllRoutes(
    navController: NavHostController,
    p0Repository: P0Repository = MockP0Repository(),
    repository: CryptoVpnRepository = MockCryptoVpnRepository(),
) {
    installCryptoVpnP0Routes(
        navController = navController,
        p0Repository = p0Repository,
        repository = repository,
    )
    installCryptoVpnP1Routes(
        navController = navController,
        repository = repository,
    )
    installCryptoVpnP2CoreRoutes(
        navController = navController,
        repository = repository,
    )
    installCryptoVpnP2ExtendedRoutes(
        navController = navController,
        repository = repository,
    )
}
