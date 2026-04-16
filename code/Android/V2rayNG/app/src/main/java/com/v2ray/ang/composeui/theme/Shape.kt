package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import com.v2ray.ang.composeui.theme.tokens.ShapeTokens

val CryptoVpnShapes = Shapes(
    extraSmall = RoundedCornerShape(ShapeTokens.RadiusXs),
    small = RoundedCornerShape(ShapeTokens.RadiusS),
    medium = RoundedCornerShape(ShapeTokens.RadiusM),
    large = RoundedCornerShape(ShapeTokens.RadiusL),
    extraLarge = RoundedCornerShape(ShapeTokens.RadiusXl),
)

val CryptoVpnPillShape = ShapeTokens.Pill
