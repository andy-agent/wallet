package com.v2ray.ang.composeui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class BackStackManager(private val navController: Any? = null) {
    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()

    fun handleBackPress(): Boolean = false

    fun goBack(): Boolean = false

    fun popBackTo(route: String, inclusive: Boolean = false): Boolean = false

    fun clearStack() {
        _canGoBack.value = false
    }
}
