package com.cryptovpn.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val CryptoVpnLightColors = lightColorScheme(
    primary = ElectricBlue,
    onPrimary = LayerWhite,
    secondary = ElectricCyan,
    tertiary = AuroraPurple,
    background = AppWhite,
    surface = LayerWhite,
    surfaceVariant = SurfaceCloud,
    onBackground = TextStrong,
    onSurface = TextStrong,
    error = DangerRed,
)

@Composable
fun CryptoVpnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = CryptoVpnLightColors,
        typography = CryptoVpnTypography,
        shapes = CryptoVpnShapes,
        content = content,
    )
}
