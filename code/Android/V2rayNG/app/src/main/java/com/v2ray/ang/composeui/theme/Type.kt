package com.v2ray.ang.composeui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalConfiguration
import com.v2ray.ang.composeui.theme.tokens.TypographyTokens
import com.v2ray.ang.composeui.theme.tokens.toMaterialTypography

object AppTypographyFactory {

    fun small(): Typography = TypographyTokens.compact().toMaterialTypography()

    fun normal(): Typography = TypographyTokens.medium().toMaterialTypography()

    fun large(): Typography = TypographyTokens.expanded().toMaterialTypography()
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
