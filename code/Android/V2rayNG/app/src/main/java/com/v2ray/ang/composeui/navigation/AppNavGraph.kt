package com.v2ray.ang.composeui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable

@Composable
fun AppNavGraph(
    navController: NavHostController,
    startDestination: String = Routes.SPLASH,
) {
    NavHost(
        navController = navController,
        startDestination = startDestination,
    ) {
        composable(Routes.SPLASH) {
            PlaceholderRoute(label = "Splash")
        }
        composable(Routes.EMAIL_LOGIN) {
            PlaceholderRoute(label = "Email Login")
        }
        composable(Routes.VPN_HOME) {
            PlaceholderRoute(label = "VPN Home")
        }
    }
}

@Composable
private fun PlaceholderRoute(label: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {
        Text(text = label)
    }
}
