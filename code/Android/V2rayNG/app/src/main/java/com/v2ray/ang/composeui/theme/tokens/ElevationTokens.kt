package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppElevationTokens(
    val none: Dp,
    val card: Dp,
    val floating: Dp,
    val hero: Dp,
)

object ElevationTokens {
    val default = AppElevationTokens(
        none = 0.dp,
        card = 6.dp,
        floating = 10.dp,
        hero = 14.dp,
    )
}
