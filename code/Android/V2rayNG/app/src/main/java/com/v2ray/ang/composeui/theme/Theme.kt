package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.theme.tokens.ColorTokens
import com.v2ray.ang.composeui.theme.tokens.AppColorTokens
import com.v2ray.ang.composeui.theme.tokens.AppElevationTokens
import com.v2ray.ang.composeui.theme.tokens.AppGradientTokens
import com.v2ray.ang.composeui.theme.tokens.AppShapeTokens
import com.v2ray.ang.composeui.theme.tokens.AppSpacingTokens
import com.v2ray.ang.composeui.theme.tokens.AppTypographyTokens
import com.v2ray.ang.composeui.theme.tokens.ElevationTokens
import com.v2ray.ang.composeui.theme.tokens.GradientTokens
import com.v2ray.ang.composeui.theme.tokens.ShapeTokens
import com.v2ray.ang.composeui.theme.tokens.SpacingTokens
import com.v2ray.ang.composeui.theme.tokens.TypographyTokens
import com.v2ray.ang.composeui.theme.tokens.toMaterialShapes
import com.v2ray.ang.composeui.theme.tokens.toMaterialTypography

private val LocalAppColors = staticCompositionLocalOf { ColorTokens.light }
private val LocalAppTypography = staticCompositionLocalOf { TypographyTokens.medium() }
private val LocalAppShapes = staticCompositionLocalOf { ShapeTokens.default }
private val LocalAppSpacing = staticCompositionLocalOf { SpacingTokens.default }
private val LocalAppElevation = staticCompositionLocalOf { ElevationTokens.default }
private val LocalAppGradients = staticCompositionLocalOf { GradientTokens.from(ColorTokens.light) }

object AppTheme {
    val colors: AppColorTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColors.current

    val typography: AppTypographyTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppTypography.current

    val shapes: AppShapeTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppShapes.current

    val spacing: AppSpacingTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppSpacing.current

    val elevation: AppElevationTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppElevation.current

    val gradients: AppGradientTokens
        @Composable
        @ReadOnlyComposable
        get() = LocalAppGradients.current
}

private fun AppColorTokens.toLightColorScheme() = lightColorScheme(
    primary = brandPrimary,
    onPrimary = textOnPrimary,
    primaryContainer = infoBg,
    onPrimaryContainer = textPrimary,
    secondary = brandSecondary,
    onSecondary = textOnPrimary,
    secondaryContainer = bgSubtle,
    onSecondaryContainer = textPrimary,
    tertiary = accentPurple,
    onTertiary = textOnPrimary,
    tertiaryContainer = surfaceElevated,
    onTertiaryContainer = textPrimary,
    background = bgApp,
    onBackground = textPrimary,
    surface = surfaceCard,
    onSurface = textPrimary,
    surfaceVariant = bgSubtle,
    onSurfaceVariant = textSecondary,
    outline = dividerSubtle,
    outlineVariant = borderSubtle,
    error = error,
    onError = textOnPrimary,
    errorContainer = errorBg,
    onErrorContainer = error,
    surfaceTint = brandPrimary,
    scrim = Color(0x66000000),
)

private fun AppColorTokens.toDarkColorScheme() = darkColorScheme(
    primary = brandPrimary,
    onPrimary = textOnPrimary,
    secondary = brandSecondary,
    tertiary = accentPurple,
    background = bgApp,
    onBackground = textPrimary,
    surface = surfaceCard,
    onSurface = textPrimary,
    surfaceVariant = bgSubtle,
    onSurfaceVariant = textSecondary,
    outline = dividerSubtle,
    outlineVariant = borderSubtle,
    error = error,
    onError = textOnPrimary,
)

@Composable
fun CryptoVpnTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val width = LocalConfiguration.current.screenWidthDp
    val colors = if (darkTheme) ColorTokens.dark else ColorTokens.light
    val typography = when {
        width < 360 -> TypographyTokens.compact()
        width < 412 -> TypographyTokens.medium()
        else -> TypographyTokens.expanded()
    }
    val spacing = when {
        width < 360 -> SpacingTokens.compact()
        width < 412 -> SpacingTokens.medium()
        else -> SpacingTokens.expanded()
    }
    val shapes = ShapeTokens.default
    val elevation = ElevationTokens.default
    val gradients = GradientTokens.from(colors)

    CompositionLocalProvider(
        LocalAppColors provides colors,
        LocalAppTypography provides typography,
        LocalAppShapes provides shapes,
        LocalAppSpacing provides spacing,
        LocalAppElevation provides elevation,
        LocalAppGradients provides gradients,
    ) {
        MaterialTheme(
            colorScheme = if (darkTheme) colors.toDarkColorScheme() else colors.toLightColorScheme(),
            typography = typography.toMaterialTypography(),
            shapes = shapes.toMaterialShapes(),
            content = content,
        )
    }
}
