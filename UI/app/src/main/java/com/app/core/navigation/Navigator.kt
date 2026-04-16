package com.app.core.navigation

import androidx.navigation.NavHostController

interface Navigator {
    fun navigate(route: String)
    fun back()
}

class NavControllerNavigator(
    private val navController: NavHostController,
) : Navigator {
    override fun navigate(route: String) {
        navController.navigate(route)
    }

    override fun back() {
        navController.popBackStack()
    }
}
