package com.v2ray.ang.composeui.navigation

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationManager(private val navController: NavController) {
    private val _currentRoute = MutableStateFlow<String?>(navController.currentDestination?.route)
    val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()

    init {
        navController.addOnDestinationChangedListener { _, destination, _ ->
            _currentRoute.value = destination.route
        }
    }

    fun navigateTo(route: String) {
        navController.navigate(route)
    }

    fun navigateAndClearStack(route: String) {
        navController.navigate(route) {
            popUpTo(0) { inclusive = true }
        }
    }

    fun goBack(): Boolean = navController.popBackStack()

    fun navigateToSplash() = navigateAndClearStack(Routes.SPLASH)

    fun navigateToEmailLogin() = navigateTo(Routes.EMAIL_LOGIN)

    fun navigateToVpnHome() = navigateAndClearStack(Routes.VPN_HOME)

    fun navigateToPlans() = navigateTo(Routes.PLANS)

    fun navigateToProfile() = navigateTo(Routes.PROFILE)

    fun navigateToWalletHome() = navigateTo(Routes.WALLET_HOME)
}
