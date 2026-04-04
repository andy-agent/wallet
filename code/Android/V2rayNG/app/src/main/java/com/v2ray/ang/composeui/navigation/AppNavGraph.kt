package com.v2ray.ang.composeui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun AppNavGraph(
    navController: Any? = null,
    startDestination: String = Routes.SPLASH,
) {
    PlaceholderRoute(label = startDestination)
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
