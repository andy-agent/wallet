package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = Primary,
    onPrimary = BackgroundDeepest,
    primaryContainer = AccentSurfaceStrong,
    onPrimaryContainer = PrimaryHover,
    secondary = Info,
    onSecondary = BackgroundDeepest,
    secondaryContainer = AccentSurfaceInfo,
    onSecondaryContainer = Info,
    tertiary = Warning,
    onTertiary = BackgroundDeepest,
    tertiaryContainer = AccentSurfaceWarm,
    onTertiaryContainer = Warning,
    error = Error,
    onError = BackgroundDeepest,
    errorContainer = Error.copy(alpha = 0.16f),
    onErrorContainer = Error.copy(alpha = 0.92f),
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = BackgroundSecondary,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundTertiary,
    onSurfaceVariant = TextSecondary,
    outline = BorderDefault,
    outlineVariant = DividerColor,
    scrim = Color.Black.copy(alpha = 0.72f),
)

private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = BackgroundDeepest,
    primaryContainer = AccentSurfaceStrong,
    onPrimaryContainer = BackgroundDeepest,
    secondary = Info,
    onSecondary = BackgroundDeepest,
    secondaryContainer = AccentSurfaceInfo,
    onSecondaryContainer = BackgroundDeepest,
    tertiary = Warning,
    onTertiary = BackgroundDeepest,
    tertiaryContainer = AccentSurfaceWarm,
    onTertiaryContainer = BackgroundDeepest,
    error = Error,
    onError = Color.White,
    errorContainer = Error.copy(alpha = 0.14f),
    onErrorContainer = Error.copy(alpha = 0.92f),
    background = BackgroundPrimary,
    onBackground = TextPrimary,
    surface = BackgroundSecondary,
    onSurface = TextPrimary,
    surfaceVariant = BackgroundTertiary,
    onSurfaceVariant = TextSecondary,
    outline = BorderDefault,
    outlineVariant = DividerColor,
    scrim = BackgroundDeepest.copy(alpha = 0.18f),
)

@Composable
fun CryptoVPNTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CryptoVPNTypography,
        shapes = AppShapes,
        content = content,
    )
}
