package com.v2ray.ang.composeui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
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

@Composable
fun CryptoVPNTheme(
    darkTheme: Boolean = true,
    content: @Composable () -> Unit,
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = CryptoVPNTypography,
        shapes = AppShapes,
        content = content,
    )
}
