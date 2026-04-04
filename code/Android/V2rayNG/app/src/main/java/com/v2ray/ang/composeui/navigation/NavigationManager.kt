package com.v2ray.ang.composeui.navigation

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class NavigationManager(startRoute: String = Routes.SPLASH) {
    private val _backStack = MutableStateFlow(listOf(Routes.normalize(startRoute)))
    val backStack: StateFlow<List<String>> = _backStack.asStateFlow()

    private val _currentRoute = MutableStateFlow(_backStack.value.last())
    val currentRoute: StateFlow<String?> = _currentRoute.asStateFlow()

    fun navigateTo(route: String) {
        val normalized = Routes.normalize(route)
        if (_backStack.value.lastOrNull() == normalized) {
            return
        }
        _backStack.value = _backStack.value + normalized
        _currentRoute.value = normalized
    }

    fun navigateAndClearStack(route: String) {
        val normalized = Routes.normalize(route)
        _backStack.value = listOf(normalized)
        _currentRoute.value = normalized
    }

    fun goBack(): Boolean {
        if (_backStack.value.size <= 1) {
            return false
        }
        val nextStack = _backStack.value.dropLast(1)
        _backStack.value = nextStack
        _currentRoute.value = nextStack.last()
        return true
    }

    fun canGoBack(): Boolean = _backStack.value.size > 1

    fun popBackTo(route: String, inclusive: Boolean = false): Boolean {
        val normalized = Routes.normalize(route)
        val targetIndex = _backStack.value.indexOfLast { it == normalized }
        if (targetIndex < 0) {
            return false
        }

        val keepCount = if (inclusive) targetIndex else targetIndex + 1
        if (keepCount <= 0) {
            return false
        }

        val nextStack = _backStack.value.take(keepCount)
        _backStack.value = nextStack
        _currentRoute.value = nextStack.last()
        return true
    }

    fun navigateToSplash() = navigateAndClearStack(Routes.SPLASH)

    fun navigateToEmailLogin() = navigateTo(Routes.EMAIL_LOGIN)

    fun navigateToVpnHome() = navigateAndClearStack(Routes.VPN_HOME)

    fun navigateToPlans() = navigateTo(Routes.PLANS)

    fun navigateToProfile() = navigateTo(Routes.PROFILE)

    fun navigateToWalletHome() = navigateTo(Routes.WALLET_HOME)

    fun navigateToOrderCheckout(planId: String) = navigateTo(Routes.orderCheckout(planId))

    fun navigateToOrderDetail(orderId: String) = navigateTo(Routes.orderDetail(orderId))

    fun navigateToAssetDetail(assetId: String) = navigateTo(Routes.assetDetail(assetId))

    fun navigateToLegalDocument(documentId: String) = navigateTo(Routes.legalDocumentDetail(documentId))
}
