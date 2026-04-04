package com.v2ray.ang.composeui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackStackManager(private val navigationManager: NavigationManager = NavigationManager()) {
    private val _canGoBack = MutableStateFlow(navigationManager.canGoBack())
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun handleBackPress(): Boolean = goBack()

    fun goBack(): Boolean {
        val handled = navigationManager.goBack()
        _canGoBack.value = navigationManager.canGoBack()
        return handled
    }

    fun popBackTo(route: String, inclusive: Boolean = false): Boolean {
        val handled = navigationManager.popBackTo(route, inclusive)
        _canGoBack.value = navigationManager.canGoBack()
        return handled
    }

    fun clearStack() {
        navigationManager.navigateAndClearStack(
            navigationManager.currentRoute.value ?: Routes.SPLASH,
        )
        _canGoBack.value = false
    }
}
