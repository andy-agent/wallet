package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * 动画效果合集 - 呼吸、旋转、波浪、波纹、闪光动画
 * 
 * 包含：
 * 1. BreathingAnimation - 呼吸动画
 * 2. RotatingAnimation - 旋转动画
 * 3. WaveAnimation - 波浪动画
 * 4. RippleAnimation - 波纹动画
 * 5. ShimmerAnimation - 闪光动画
 */

// ==================== 呼吸动画 ====================

/**
 * 呼吸动画效果 - 用于Logo和图标
 * 
 * @param minScale 最小缩放
 * @param maxScale 最大缩放
 * @param duration 动画周期
 * @param content 内容
 */
@Composable
fun BreathingAnimation(
    modifier: Modifier = Modifier,
    minScale: Float = 0.95f,
    maxScale: Float = 1.05f,
    duration: Int = 2000,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "breathing")
    
    val scale by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathingScale"
    )
    
    val currentScale = minScale + (maxScale - minScale) * scale
    
    Box(
        modifier = modifier.scale(currentScale),
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

// ==================== 旋转动画 ====================

/**
 * 旋转动画效果 - 用于加载状态
 * 
 * @param duration 旋转周期
 * @param direction 旋转方向（1=顺时针, -1=逆时针）
 * @param content 内容
 */
@Composable
fun RotatingAnimation(
    modifier: Modifier = Modifier,
    duration: Int = 2000,
    direction: Int = 1,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "rotating")
    
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f * direction,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    Box(
        modifier = modifier.graphicsLayer { rotationZ = rotation },
        contentAlignment = Alignment.Center
    ) {
        content()
    }
}

/**
 * 多层旋转效果 - 用于复杂加载动画
 */
@Composable
fun MultiLayerRotation(
    modifier: Modifier = Modifier,
    size: Float = 100f,
    primaryColor: Color = Color(0xFF1D4ED8),
    secondaryColor: Color = Color(0xFF06B6D4)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "multiRotation")
    
    val rotation1 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot1"
    )
    
    val rotation2 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = -360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot2"
    )
    
    val rotation3 by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rot3"
    )
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.7f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Canvas(modifier = modifier.size(size.dp)) {
        val centerX = sizeCenter.x
        val centerY = sizeCenter.y
        val baseRadius = size / 2 * 0.8f
        
        // 外层圆环
        drawArcRing(
            centerX = centerX,
            centerY = centerY,
            radius = baseRadius,
            strokeWidth = 4f,
            color = primaryColor,
            rotation = rotation1,
            startAngle = 0f,
            sweepAngle = 120f
        )
        
        // 中层圆环
        drawArcRing(
            centerX = centerX,
            centerY = centerY,
            radius = baseRadius * 0.7f,
            strokeWidth = 3f,
            color = secondaryColor,
            rotation = rotation2,
            startAngle = 120f,
            sweepAngle = 100f
        )
        
        // 内层圆环
        drawArcRing(
            centerX = centerX,
            centerY = centerY,
            radius = baseRadius * 0.45f,
            strokeWidth = 2f,
            color = primaryColor.copy(alpha = 0.8f),
            rotation = rotation3,
            startAngle = 240f,
            sweepAngle = 80f
        )
        
        // 中心发光点
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    primaryColor.copy(alpha = 0.8f * pulse),
                    primaryColor.copy(alpha = 0.3f * pulse),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = baseRadius * 0.25f
            ),
            radius = baseRadius * 0.25f,
            center = Offset(centerX, centerY)
        )
    }
}

private fun DrawScope.drawArcRing(
    centerX: Float,
    centerY: Float,
    radius: Float,
    strokeWidth: Float,
    color: Color,
    rotation: Float,
    startAngle: Float,
    sweepAngle: Float
) {
    drawArc(
        color = color,
        startAngle = startAngle + rotation,
        sweepAngle = sweepAngle,
        useCenter = false,
        topLeft = Offset(centerX - radius, centerY - radius),
        size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
        style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    )
}

// ==================== 波浪动画 ====================

/**
 * 波浪动画效果 - 用于背景装饰
 * 
 * @param waveCount 波浪数量
 * @param waveColor 波浪颜色
 * @param amplitude 振幅
 */
@Composable
fun WaveAnimation(
    modifier: Modifier = Modifier,
    waveCount: Int = 3,
    waveColor: Color = Color(0xFF1D4ED8),
    amplitude: Float = 30f
) {
    val infiniteTransition = rememberInfiniteTransition(label = "wave")
    
    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 2 * PI.toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "wavePhase"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        for (i in 0 until waveCount) {
            val wavePhase = phase + i * (2 * PI.toFloat() / waveCount)
            val waveAlpha = 0.3f - i * 0.08f
            val waveAmplitude = amplitude * (1f - i * 0.2f)
            val waveY = height * (0.3f + i * 0.25f)
            
            drawWave(
                width = width,
                baseY = waveY,
                amplitude = waveAmplitude,
                phase = wavePhase,
                color = waveColor.copy(alpha = waveAlpha),
                frequency = 0.02f + i * 0.005f
            )
        }
    }
}

private fun DrawScope.drawWave(
    width: Float,
    baseY: Float,
    amplitude: Float,
    phase: Float,
    color: Color,
    frequency: Float
) {
    val path = androidx.compose.ui.graphics.Path()
    path.moveTo(0f, baseY)
    
    for (x in 0..width.toInt() step 5) {
        val y = baseY + sin(x * frequency + phase) * amplitude
        path.lineTo(x.toFloat(), y.toFloat())
    }
    
    path.lineTo(width, size.height)
    path.lineTo(0f, size.height)
    path.close()
    
    drawPath(path = path, color = color)
}

// ==================== 波纹动画 ====================

/**
 * 波纹动画效果 - 用于点击反馈
 * 
 * @param trigger 触发波纹
 * @param rippleColor 波纹颜色
 * @param maxRadius 最大半径
 * @param onComplete 完成回调
 */
@Composable
fun RippleAnimation(
    modifier: Modifier = Modifier,
    trigger: Boolean,
    rippleColor: Color = Color(0xFF1D4ED8),
    maxRadius: Float = 150f,
    onComplete: (() -> Unit)? = null
) {
    var isAnimating by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            isAnimating = true
        }
    }
    
    if (isAnimating) {
        val animatable = remember { Animatable(0f) }
        
        LaunchedEffect(Unit) {
            animatable.animateTo(
                targetValue = 1f,
                animationSpec = tween(600, easing = FastOutSlowInEasing)
            )
            isAnimating = false
            onComplete?.invoke()
        }
        
        val progress = animatable.value
        
        Canvas(modifier = modifier.fillMaxSize()) {
            val centerX = size.width / 2
            val centerY = size.height / 2
            
            // 多层波纹
            for (i in 0..2) {
                val rippleProgress = (progress - i * 0.2f).coerceIn(0f, 1f)
                if (rippleProgress > 0) {
                    val radius = maxRadius * rippleProgress
                    val alpha = (1f - rippleProgress) * 0.5f
                    
                    drawCircle(
                        color = rippleColor.copy(alpha = alpha),
                        radius = radius,
                        center = Offset(centerX, centerY),
                        style = Stroke(width = 3f * (1f - rippleProgress))
                    )
                }
            }
        }
    }
}

// ==================== 闪光动画 ====================

/**
 * 闪光动画效果 - 用于骨架屏
 * 
 * @param shimmerColor 闪光颜色
 * @param duration 动画周期
 * @param content 内容
 */
@Composable
fun ShimmerAnimation(
    modifier: Modifier = Modifier,
    shimmerColor: Color = Color.White,
    duration: Int = 1500,
    content: @Composable () -> Unit
) {
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmerProgress by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(duration, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmerProgress"
    )
    
    Box(modifier = modifier) {
        content()
        
        // 闪光遮罩
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height
            
            val shimmerWidth = width * 0.3f
            val shimmerX = shimmerProgress * (width + shimmerWidth) - shimmerWidth
            
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        shimmerColor.copy(alpha = 0.1f),
                        shimmerColor.copy(alpha = 0.3f),
                        shimmerColor.copy(alpha = 0.1f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerX, 0f),
                    end = Offset(shimmerX + shimmerWidth, 0f)
                ),
                topLeft = Offset(0f, 0f),
                size = androidx.compose.ui.geometry.Size(width, height)
            )
        }
    }
}

/**
 * 骨架屏闪光效果
 */
@Composable
fun SkeletonShimmer(
    modifier: Modifier = Modifier,
    shimmerColor: Color = Color.White
) {
    ShimmerAnimation(
        modifier = modifier,
        shimmerColor = shimmerColor
    ) {
        Column(
            modifier = modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 标题骨架
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.6f)
                    .height(24.dp)
                    .background(Color(0xFF374151), RoundedCornerShape(4.dp))
            )
            
            // 内容骨架
            repeat(3) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(16.dp)
                        .background(Color(0xFF374151), RoundedCornerShape(4.dp))
                )
            }
        }
    }
}

// ==================== 预览 ====================

@Preview(device = "id:pixel_5")
@Composable
private fun BreathingAnimationPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        BreathingAnimation(
            minScale = 0.9f,
            maxScale = 1.1f,
            duration = 2000
        ) {
            Surface(
                shape = CircleShape,
                color = Color(0xFF1D4ED8),
                modifier = Modifier.size(100.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Text(
                        text = "VPN",
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun RotatingAnimationPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        RotatingAnimation(duration = 2000) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = null,
                tint = Color(0xFFF59E0B),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun MultiLayerRotationPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        MultiLayerRotation(
            size = 150f,
            primaryColor = Color(0xFF1D4ED8),
            secondaryColor = Color(0xFF06B6D4)
        )
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun WaveAnimationPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
    ) {
        WaveAnimation(
            waveCount = 3,
            waveColor = Color(0xFF1D4ED8),
            amplitude = 40f
        )
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "CryptoVPN",
                color = Color.White,
                style = MaterialTheme.typography.headlineLarge
            )
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun RippleAnimationPreview() {
    var trigger by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        RippleAnimation(
            trigger = trigger,
            rippleColor = Color(0xFF22C55E),
            onComplete = { trigger = false }
        )
        
        Button(
            onClick = { trigger = true },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E)
            )
        ) {
            Text("Click for Ripple")
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun ShimmerAnimationPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        SkeletonShimmer(
            modifier = Modifier.fillMaxWidth(0.9f)
        )
    }
}

/**
 * 使用示例：
 * 
 * ```kotlin
 * // 呼吸动画 - Logo
 * BreathingAnimation {
 *     Logo()
 * }
 * 
 * // 旋转动画 - 加载中
 * RotatingAnimation {
 *     LoadingIcon()
 * }
 * 
 * // 多层旋转 - VPN连接中
 * MultiLayerRotation(
 *     size = 120f,
 *     primaryColor = Color(0xFFF59E0B)
 * )
 * 
 * // 波浪背景
 * WaveAnimation(
 *     waveCount = 3,
 *     waveColor = Color(0xFF1D4ED8)
 * )
 * 
 * // 波纹点击效果
 * RippleAnimation(
 *     trigger = isClicked,
 *     rippleColor = Color(0xFF22C55E)
 * )
 * 
 * // 骨架屏闪光
 * SkeletonShimmer()
 * ```
 */