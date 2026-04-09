package com.v2ray.ang.composeui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

@Suppress("UNUSED_PARAMETER")
@Composable
fun CryptoVPNTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = ControlPlaneTokens.materialColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CryptoVPNTypography,
        shapes = AppShapes,
        content = content,
    )
}
