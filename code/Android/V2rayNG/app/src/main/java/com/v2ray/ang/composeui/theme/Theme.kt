package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.theme.tokens.ColorTokens

private val CryptoVpnLightColors = lightColorScheme(
    primary = ColorTokens.BrandPrimary,
    onPrimary = ColorTokens.TextOnPrimary,
    primaryContainer = ColorTokens.StatusInfoBg,
    onPrimaryContainer = ColorTokens.TextPrimary,
    secondary = ColorTokens.BrandSecondary,
    onSecondary = ColorTokens.TextOnPrimary,
    secondaryContainer = ColorTokens.SoftSkyBg,
    onSecondaryContainer = ColorTokens.TextPrimary,
    tertiary = ColorTokens.AccentPurple,
    onTertiary = ColorTokens.TextOnPrimary,
    tertiaryContainer = ColorTokens.SoftLavender,
    onTertiaryContainer = ColorTokens.TextPrimary,
    background = ColorTokens.BackgroundBase,
    onBackground = ColorTokens.TextPrimary,
    surface = ColorTokens.CardBase,
    onSurface = ColorTokens.TextPrimary,
    surfaceVariant = ColorTokens.BackgroundSoftTint,
    onSurfaceVariant = ColorTokens.TextSecondary,
    outline = ColorTokens.BorderLight,
    outlineVariant = ColorTokens.BorderSubtle,
    error = ColorTokens.StatusError,
    onError = ColorTokens.TextOnPrimary,
    errorContainer = ColorTokens.StatusErrorBg,
    onErrorContainer = ColorTokens.StatusError,
    surfaceTint = ColorTokens.BrandPrimary,
    scrim = Color(0x66000000),
)

private val CryptoVpnDarkColors = darkColorScheme(
    primary = ColorTokens.BrandPrimary,
    onPrimary = ColorTokens.TextOnPrimary,
    secondary = ColorTokens.BrandSecondary,
    tertiary = ColorTokens.AccentPurple,
    background = Color(0xFF0D1524),
    onBackground = Color(0xFFEAF1FF),
    surface = Color(0xFF111B2C),
    onSurface = Color(0xFFEAF1FF),
    surfaceVariant = Color(0xFF19253B),
    onSurfaceVariant = Color(0xFFB8C1D1),
    outline = Color(0xFF324158),
    outlineVariant = Color(0xFF26354A),
    error = ColorTokens.StatusError,
    onError = ColorTokens.TextOnPrimary,
)

@Composable
fun CryptoVpnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val typography = rememberAdaptiveTypography()
    MaterialTheme(
        colorScheme = if (darkTheme) CryptoVpnDarkColors else CryptoVpnLightColors,
        typography = typography,
        shapes = CryptoVpnShapes,
        content = content,
    )
}
