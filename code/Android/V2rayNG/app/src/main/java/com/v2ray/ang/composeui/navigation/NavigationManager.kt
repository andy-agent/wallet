package com.v2ray.ang.composeui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationManager(private val navController: Any? = null) {
    private val _currentRoute = MutableStateFlow<String?>(null)
    val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()

    fun navigateTo(route: String) {
        _currentRoute.value = route
    }

    fun navigateAndClearStack(route: String) {
        _currentRoute.value = route
    }

    fun goBack(): Boolean = false

    fun navigateToSplash() = navigateAndClearStack(Routes.SPLASH)

    fun navigateToEmailLogin() = navigateTo(Routes.EMAIL_LOGIN)

    fun navigateToVpnHome() = navigateAndClearStack(Routes.VPN_HOME)

    fun navigateToPlans() = navigateTo(Routes.PLANS)

    fun navigateToProfile() = navigateTo(Routes.PROFILE)

    fun navigateToWalletHome() = navigateTo(Routes.WALLET_HOME)
}
