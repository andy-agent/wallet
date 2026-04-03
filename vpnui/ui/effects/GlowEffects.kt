package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * 发光效果合集 - 霓虹光晕、脉冲发光、边缘发光
 * 
 * 包含：
 * 1. GlowEffect - 基础霓虹光晕效果
 * 2. PulseGlowEffect - 脉冲发光效果
 * 3. EdgeGlowEffect - 边缘发光效果
 * 4. Modifier扩展 - 便捷使用方式
 */

/**
 * 基础霓虹光晕效果
 * 
 * @param glowColor 发光颜色
 * @param glowRadius 发光半径
 * @param intensity 发光强度 0-1
 * @param shape 形状（圆形或圆角矩形）
 */
@Composable
fun GlowEffect(
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF1D4ED8),
    glowRadius: Dp = 20.dp,
    intensity: Float = 0.6f,
    shape: GlowShape = GlowShape.Circle,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .drawBehind {
                drawGlow(
                    color = glowColor,
                    radius = glowRadius.toPx(),
                    intensity = intensity,
                    shape = shape
                )
            }
    ) {
        content()
    }
}

/**
 * 脉冲发光效果 - 呼吸式发光
 * 
 * @param glowColor 发光颜色
 * @param minRadius 最小发光半径
 * @param maxRadius 最大发光半径
 * @param pulseDuration 脉冲周期（毫秒）
 */
@Composable
fun PulseGlowEffect(
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF22C55E),
    minRadius: Dp = 15.dp,
    maxRadius: Dp = 35.dp,
    pulseDuration: Int = 1500,
    shape: GlowShape = GlowShape.Circle,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulseGlow")
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDuration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    val currentRadius = minRadius + (maxRadius - minRadius) * pulse
    val currentIntensity = 0.4f + 0.4f * pulse
    
    Box(
        modifier = modifier
            .drawBehind {
                drawGlow(
                    color = glowColor,
                    radius = currentRadius.toPx(),
                    intensity = currentIntensity,
                    shape = shape
                )
            }
    ) {
        content()
    }
}

/**
 * 边缘发光效果 - 用于重要卡片
 * 
 * @param glowColor 发光颜色
 * @param borderWidth 边框宽度
 * @param glowWidth 发光宽度
 * @param cornerRadius 圆角半径
 */
@Composable
fun EdgeGlowEffect(
    modifier: Modifier = Modifier,
    glowColor: Color = Color(0xFF1D4ED8),
    borderWidth: Dp = 2.dp,
    glowWidth: Dp = 8.dp,
    cornerRadius: Dp = 12.dp,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "edgeGlow")
    
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Box(
        modifier = modifier
            .drawBehind {
                drawEdgeGlow(
                    color = glowColor,
                    borderWidth = borderWidth.toPx(),
                    glowWidth = glowWidth.toPx(),
                    cornerRadius = cornerRadius.toPx(),
                    shimmerAngle = shimmer
                )
            }
    ) {
        content()
    }
}

/**
 * 发光形状枚举
 */
enum class GlowShape {
    Circle,
    RoundedRect
}

/**
 * 绘制发光效果
 */
private fun DrawScope.drawGlow(
    color: Color,
    radius: Float,
    intensity: Float,
    shape: GlowShape
) {
    when (shape) {
        GlowShape.Circle -> {
            // 多层光晕
            for (i in 3 downTo 1) {
                val layerRadius = radius * i / 3
                val layerAlpha = intensity / i
                
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = layerAlpha),
                            color.copy(alpha = layerAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = layerRadius
                    ),
                    radius = layerRadius,
                    center = center
                )
            }
        }
        GlowShape.RoundedRect -> {
            // 圆角矩形光晕
            for (i in 3 downTo 1) {
                val layerRadius = radius * i / 3
                val layerAlpha = intensity / i
                
                drawRoundRect(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            color.copy(alpha = layerAlpha),
                            color.copy(alpha = layerAlpha * 0.3f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = layerRadius
                    ),
                    topLeft = Offset(
                        center.x - size.width / 2 - layerRadius * 0.3f,
                        center.y - size.height / 2 - layerRadius * 0.3f
                    ),
                    size = androidx.compose.ui.geometry.Size(
                        size.width + layerRadius * 0.6f,
                        size.height + layerRadius * 0.6f
                    ),
                    cornerRadius = androidx.compose.ui.geometry.CornerRadius(layerRadius * 0.3f)
                )
            }
        }
    }
}

/**
 * 绘制边缘发光
 */
private fun DrawScope.drawEdgeGlow(
    color: Color,
    borderWidth: Float,
    glowWidth: Float,
    cornerRadius: Float,
    shimmerAngle: Float
) {
    val halfWidth = size.width / 2
    val halfHeight = size.height / 2
    
    // 计算闪烁位置
    val shimmerProgress = (shimmerAngle / 360f)
    val shimmerX = -halfWidth + size.width * shimmerProgress
    
    // 外发光
    drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(
                color.copy(alpha = 0.1f),
                color.copy(alpha = 0.3f),
                color.copy(alpha = 0.1f)
            ),
            start = Offset(0f, 0f),
            end = Offset(size.width, size.height)
        ),
        topLeft = Offset(-glowWidth, -glowWidth),
        size = androidx.compose.ui.geometry.Size(
            size.width + glowWidth * 2,
            size.height + glowWidth * 2
        ),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius + glowWidth)
    )
    
    // 边框
    drawRoundRect(
        color = color.copy(alpha = 0.6f),
        topLeft = Offset(0f, 0f),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = borderWidth)
    )
    
    // 闪烁效果
    drawRoundRect(
        brush = Brush.radialGradient(
            colors = listOf(
                Color.White.copy(alpha = 0.5f),
                Color.Transparent
            ),
            center = Offset(shimmerX + halfWidth, halfHeight),
            radius = glowWidth * 2
        ),
        topLeft = Offset(0f, 0f),
        size = size,
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(cornerRadius)
    )
}

/**
 * Modifier扩展 - 霓虹光晕
 */
fun Modifier.neonGlow(
    color: Color = Color(0xFF1D4ED8),
    radius: Dp = 20.dp,
    intensity: Float = 0.6f
): Modifier = composed {
    drawBehind {
        drawGlow(color, radius.toPx(), intensity, GlowShape.Circle)
    }
}

/**
 * Modifier扩展 - 脉冲发光
 */
fun Modifier.pulseGlow(
    color: Color = Color(0xFF22C55E),
    minRadius: Dp = 15.dp,
    maxRadius: Dp = 35.dp,
    duration: Int = 1500
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "modifierPulse")
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "modifierPulseAnim"
    )
    
    val currentRadius = minRadius + (maxRadius - minRadius) * pulse
    
    drawBehind {
        drawGlow(color, currentRadius.toPx(), 0.4f + 0.4f * pulse, GlowShape.Circle)
    }
}

/**
 * Modifier扩展 - 边缘发光
 */
fun Modifier.edgeGlow(
    color: Color = Color(0xFF1D4ED8),
    borderWidth: Dp = 2.dp,
    glowWidth: Dp = 8.dp,
    cornerRadius: Dp = 12.dp
): Modifier = composed {
    val infiniteTransition = rememberInfiniteTransition(label = "edgeGlowModifier")
    
    val shimmer by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    drawBehind {
        drawEdgeGlow(
            color = color,
            borderWidth = borderWidth.toPx(),
            glowWidth = glowWidth.toPx(),
            cornerRadius = cornerRadius.toPx(),
            shimmerAngle = shimmer
        )
    }
}

// ==================== 预览 ====================

@Preview(device = "id:pixel_5")
@Composable
private fun GlowEffectPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        GlowEffect(
            glowColor = Color(0xFF1D4ED8),
            glowRadius = 30.dp,
            intensity = 0.7f
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                modifier = Modifier.size(120.dp, 50.dp)
            ) {
                Text("Connect", color = Color.White)
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun PulseGlowEffectPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        PulseGlowEffect(
            glowColor = Color(0xFF22C55E),
            minRadius = 20.dp,
            maxRadius = 50.dp,
            pulseDuration = 1500
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF22C55E),
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = androidx.compose.material.icons.Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(40.dp)
                    )
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun EdgeGlowEffectPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        EdgeGlowEffect(
            glowColor = Color(0xFF06B6D4),
            borderWidth = 2.dp,
            glowWidth = 12.dp,
            cornerRadius = 16.dp
        ) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color(0xFF1F2937),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(120.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Premium Server",
                        color = Color.White,
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = "Ultra-fast connection",
                        color = Color.Gray
                    )
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun ModifierGlowPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // 霓虹光晕
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .neonGlow(Color(0xFF1D4ED8), 25.dp, 0.7f)
                    .background(Color(0xFF1D4ED8), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("VPN", color = Color.White)
            }
            
            // 脉冲光晕
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .pulseGlow(Color(0xFF22C55E), 15.dp, 40.dp, 1500)
                    .background(Color(0xFF22C55E), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Check,
                    contentDescription = null,
                    tint = Color.White
                )
            }
            
            // 边缘光晕
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .edgeGlow(Color(0xFFF59E0B), 2.dp, 8.dp, 12.dp)
                    .background(Color(0xFF1F2937), RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = androidx.compose.material.icons.Icons.Default.Settings,
                    contentDescription = null,
                    tint = Color(0xFFF59E0B)
                )
            }
        }
    }
}

/**
 * 使用示例：
 * 
 * ```kotlin
 * // 基础发光效果
 * GlowEffect(
 *     glowColor = Color(0xFF1D4ED8),
 *     glowRadius = 30.dp
 * ) {
 *     ConnectButton()
 * }
 * 
 * // 脉冲发光（已连接状态）
 * PulseGlowEffect(
 *     glowColor = Color(0xFF22C55E),
 *     minRadius = 20.dp,
 *     maxRadius = 50.dp
 * ) {
 *     ConnectedIndicator()
 * }
 * 
 * // 边缘发光（重要卡片）
 * EdgeGlowEffect(
 *     glowColor = Color(0xFF06B6D4),
 *     cornerRadius = 16.dp
 * ) {
 *     PremiumCard()
 * }
 * 
 * // Modifier方式使用
 * ConnectButton(
 *     modifier = Modifier.neonGlow(Color(0xFF1D4ED8))
 * )
 * ```
 */