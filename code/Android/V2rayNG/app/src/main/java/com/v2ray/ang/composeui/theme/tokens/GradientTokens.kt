package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.ui.graphics.Brush

object GradientTokens {
    val PrimaryGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(
                ColorTokens.BrandPrimary,
                ColorTokens.BrandSecondary,
            ),
        )

    val HeroGlowGradient: Brush
        get() = Brush.linearGradient(
            colors = listOf(
                ColorTokens.AccentPurple,
                ColorTokens.BrandPrimary,
                ColorTokens.BrandSecondary,
            ),
        )

    val SoftBackgroundGradient: Brush
        get() = Brush.verticalGradient(
            colors = listOf(
                ColorTokens.BackgroundBase,
                ColorTokens.BackgroundSoftTint,
            ),
        )

    val InfoTintGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(
                ColorTokens.StatusInfoBg,
                ColorTokens.CardBase,
            ),
        )

    val SuccessTintGradient: Brush
        get() = Brush.horizontalGradient(
            colors = listOf(
                ColorTokens.StatusSuccessBg,
                ColorTokens.CardBase,
            ),
        )
}
