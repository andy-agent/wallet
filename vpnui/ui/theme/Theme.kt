package com.cryptovpn.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Dark Color Scheme (Default for CryptoVPN)
private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = Primary.copy(alpha = 0.2f),
    onPrimaryContainer = Primary,
    secondary = Success,
    onSecondary = Color.White,
    secondaryContainer = Success.copy(alpha = 0.2f),
    onSecondaryContainer = Success,
    tertiary = Warning,
    onTertiary = Color.White,
    tertiaryContainer = Warning.copy(alpha = 0.2f),
    onTertiaryContainer = Warning,
    error = Error,
    onError = Color.White,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error,
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = BackgroundSecondary,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundTertiary,
    onSurfaceVariant = TextSecondary,
    outline = BorderDefault,
    outlineVariant = DividerColor,
    scrim = Color.Black.copy(alpha = 0.7f)
)

@Composable
fun CryptoVPNTheme(
    darkTheme: Boolean = true, // Always use dark theme for CryptoVPN
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CryptoVPNTypography,
        content = content
    )
}