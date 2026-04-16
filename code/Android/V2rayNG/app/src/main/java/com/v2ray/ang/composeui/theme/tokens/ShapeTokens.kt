package com.v2ray.ang.composeui.theme.tokens

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.runtime.Immutable
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Immutable
data class AppShapeTokens(
    val radiusXs: Dp,
    val radiusSm: Dp,
    val radiusMd: Dp,
    val radiusLg: Dp,
    val radiusXl: Dp,
    val radiusPill: Dp,
)

object ShapeTokens {
    val default = AppShapeTokens(
        radiusXs = 8.dp,
        radiusSm = 12.dp,
        radiusMd = 16.dp,
        radiusLg = 20.dp,
        radiusXl = 24.dp,
        radiusPill = 999.dp,
    )
}

fun AppShapeTokens.toMaterialShapes(): Shapes = Shapes(
    extraSmall = RoundedCornerShape(radiusXs),
    small = RoundedCornerShape(radiusSm),
    medium = RoundedCornerShape(radiusMd),
    large = RoundedCornerShape(radiusLg),
    extraLarge = RoundedCornerShape(radiusXl),
)

fun AppShapeTokens.pill() = RoundedCornerShape(radiusPill)
