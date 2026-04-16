package com.v2ray.ang.composeui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.v2ray.ang.composeui.theme.tokens.TypographyTokens

object AppTypographyFactory {

    fun small(): Typography = TypographyTokens.materialCompact()

    fun normal(): Typography = TypographyTokens.materialMedium()

    fun large(): Typography = TypographyTokens.materialExpanded()
}

@Composable
fun rememberAdaptiveTypography(): Typography {
    val width = LocalConfiguration.current.screenWidthDp
    return when {
        width < 360 -> AppTypographyFactory.small()
        width < 412 -> AppTypographyFactory.normal()
        else -> AppTypographyFactory.large()
    }
}
