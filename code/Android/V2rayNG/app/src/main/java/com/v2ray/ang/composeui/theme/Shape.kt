package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val RadiusSmall = 10.dp
val RadiusMedium = 14.dp
val RadiusLarge = 18.dp
val RadiusXLarge = 24.dp
val Radius2XLarge = 32.dp
val RadiusFull = 999.dp

val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusSmall),
    small = RoundedCornerShape(RadiusMedium),
    medium = RoundedCornerShape(RadiusLarge),
    large = RoundedCornerShape(RadiusXLarge),
    extraLarge = RoundedCornerShape(Radius2XLarge),
)

object AppShape {
    val Card = RoundedCornerShape(RadiusLarge)
    val CardLarge = RoundedCornerShape(Radius2XLarge)

    val Button = RoundedCornerShape(RadiusLarge)
    val ButtonLarge = RoundedCornerShape(RadiusXLarge)
    val ButtonFull = RoundedCornerShape(RadiusFull)

    val Input = RoundedCornerShape(RadiusLarge)
    val InputLarge = RoundedCornerShape(RadiusXLarge)

    val Dialog = RoundedCornerShape(RadiusXLarge)
    val BottomSheet = RoundedCornerShape(topStart = RadiusXLarge, topEnd = RadiusXLarge)

    val Tag = RoundedCornerShape(RadiusMedium)
    val TagPill = RoundedCornerShape(RadiusFull)

    val ListItem = RoundedCornerShape(RadiusLarge)

    val Avatar = RoundedCornerShape(RadiusFull)
    val AvatarSquare = RoundedCornerShape(RadiusLarge)
}
