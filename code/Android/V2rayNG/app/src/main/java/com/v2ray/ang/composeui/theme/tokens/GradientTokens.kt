package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Brush

@Immutable
data class AppGradientTokens(
    val primaryGradient: Brush,
    val heroGlowGradient: Brush,
    val cardGlowGradient: Brush,
)

object GradientTokens {
    fun from(colors: AppColorTokens): AppGradientTokens = AppGradientTokens(
        primaryGradient = Brush.horizontalGradient(
            colors = listOf(
                colors.brandPrimary,
                colors.brandSecondary,
            ),
        ),
        heroGlowGradient = Brush.linearGradient(
            colors = listOf(
                colors.accentPurple,
                colors.brandPrimary,
                colors.brandSecondary,
            ),
        ),
        cardGlowGradient = Brush.linearGradient(
            colors = listOf(
                colors.surfaceGlowStrong,
                colors.surfaceGlowWeak,
                colors.bgSubtle,
            ),
        ),
    )
}
