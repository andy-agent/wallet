package com.v2ray.ang.composeui.navigation

import androidx.navigation.NavController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackStackManager(private val navController: NavController) {
    private val _canGoBack = MutableStateFlow(navController.previousBackStackEntry != null)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    init {
        navController.addOnDestinationChangedListener { controller, _, _ ->
            _canGoBack.value = controller.previousBackStackEntry != null
        }
    }

    fun handleBackPress(): Boolean = navController.popBackStack()

    fun goBack(): Boolean = navController.popBackStack()

    fun popBackTo(route: String, inclusive: Boolean = false): Boolean =
        navController.popBackStack(route, inclusive)

    fun clearStack() {
        while (navController.previousBackStackEntry != null) {
            if (!navController.popBackStack()) {
                return
            }
        }
    }
}
