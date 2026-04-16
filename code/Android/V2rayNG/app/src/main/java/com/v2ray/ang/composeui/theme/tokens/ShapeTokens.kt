package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

object ShapeTokens {
    val RadiusXs: Dp = 8.dp
    val RadiusS: Dp = 12.dp
    val RadiusM: Dp = 16.dp
    val RadiusL: Dp = 20.dp
    val RadiusXl: Dp = 24.dp
    val RadiusPill: Dp = 999.dp

    val Xs = RoundedCornerShape(RadiusXs)
    val S = RoundedCornerShape(RadiusS)
    val M = RoundedCornerShape(RadiusM)
    val L = RoundedCornerShape(RadiusL)
    val Xl = RoundedCornerShape(RadiusXl)
    val Pill = RoundedCornerShape(RadiusPill)
}
