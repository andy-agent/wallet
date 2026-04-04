package com.v2ray.ang.composeui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

/**
 * CryptoVPN 设计系统 - 形状定义
 * 
 * 圆角系统规范：
 * - radius-sm: 4dp
 * - radius-md: 8dp
 * - radius-lg: 12dp
 * - radius-xl: 16dp
 * - radius-2xl: 24dp
 * - radius-full: 999dp
 */

// 基础圆角值
val RadiusSmall = 4.dp
val RadiusMedium = 8.dp
val RadiusLarge = 12.dp
val RadiusXLarge = 16.dp
val Radius2XLarge = 24.dp
val RadiusFull = 999.dp

// Material 3 Shapes
val AppShapes = Shapes(
    extraSmall = RoundedCornerShape(RadiusSmall),
    small = RoundedCornerShape(RadiusMedium),
    medium = RoundedCornerShape(RadiusLarge),
    large = RoundedCornerShape(RadiusXLarge),
    extraLarge = RoundedCornerShape(Radius2XLarge)
)

// 常用形状
object AppShape {
    // 卡片形状
    val Card = RoundedCornerShape(RadiusLarge)
    val CardLarge = RoundedCornerShape(RadiusXLarge)
    
    // 按钮形状
    val Button = RoundedCornerShape(RadiusMedium)
    val ButtonLarge = RoundedCornerShape(RadiusLarge)
    val ButtonFull = RoundedCornerShape(RadiusFull)
    
    // 输入框形状
    val Input = RoundedCornerShape(RadiusMedium)
    val InputLarge = RoundedCornerShape(RadiusLarge)
    
    // 弹窗形状
    val Dialog = RoundedCornerShape(RadiusXLarge)
    val BottomSheet = RoundedCornerShape(topStart = RadiusXLarge, topEnd = RadiusXLarge)
    
    // 标签形状
    val Tag = RoundedCornerShape(RadiusSmall)
    val TagPill = RoundedCornerShape(RadiusFull)
    
    // 列表项形状
    val ListItem = RoundedCornerShape(RadiusMedium)
    
    // 头像形状
    val Avatar = RoundedCornerShape(RadiusFull)
    val AvatarSquare = RoundedCornerShape(RadiusMedium)
}
