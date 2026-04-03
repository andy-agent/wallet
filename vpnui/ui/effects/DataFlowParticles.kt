package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import kotlin.math.sin
import kotlin.random.Random

/**
 * 数据流粒子效果 - 用于VPN连接状态可视化
 * 
 * 特点：
 * - 模拟数据包在网络中流动的效果
 * - 粒子沿曲线路径移动
 * - 支持双向数据流（上传/下载）
 * - 粒子带有拖尾效果
 * 
 * @param particleCount 粒子数量，默认50
 * @param flowDirection 流动方向：0=双向, 1=上传, -1=下载
 * @param primaryColor 主色调，默认蓝色
 * @param secondaryColor 次色调，默认青色
 */
@Composable
fun DataFlowParticles(
    modifier: Modifier = Modifier,
    particleCount: Int = 50,
    flowDirection: Int = 0, // 0=双向, 1=上传, -1=下载
    primaryColor: Color = Color(0xFF1D4ED8),
    secondaryColor: Color = Color(0xFF06B6D4),
    isConnected: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "dataflow")
    
    // 全局时间动画
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    // 连接状态脉冲
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // 生成数据粒子
    val particles = remember {
        List(particleCount) { index ->
            DataParticle(
                id = index,
                pathOffset = Random.nextFloat(),
                speed = Random.nextFloat() * 0.3f + 0.2f,
                size = Random.nextFloat() * 3f + 2f,
                direction = when (flowDirection) {
                    1 -> 1
                    -1 -> -1
                    else -> if (Random.nextBoolean()) 1 else -1
                },
                pathIndex = Random.nextInt(5),
                colorBlend = Random.nextFloat()
            )
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        val centerX = width / 2
        val centerY = height / 2
        
        // 绘制中心连接点发光效果
        if (isConnected) {
            drawCenterGlow(centerX, centerY, primaryColor, pulse)
        }
        
        // 绘制数据流路径和粒子
        particles.forEach { particle ->
            drawDataParticle(
                particle = particle,
                time = time,
                centerX = centerX,
                centerY = centerY,
                maxRadius = minOf(width, height) * 0.4f,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                isConnected = isConnected
            )
        }
    }
}

/**
 * 数据粒子数据类
 */
private data class DataParticle(
    val id: Int,
    val pathOffset: Float,
    val speed: Float,
    val size: Float,
    val direction: Int,
    val pathIndex: Int,
    val colorBlend: Float
)

/**
 * 绘制中心发光效果
 */
private fun DrawScope.drawCenterGlow(
    centerX: Float,
    centerY: Float,
    color: Color,
    pulse: Float
) {
    // 内层光晕
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.6f * pulse),
                color.copy(alpha = 0.2f * pulse),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = 60f * pulse
        ),
        radius = 60f * pulse,
        center = Offset(centerX, centerY)
    )
    
    // 外层光晕
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.2f * pulse),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = 120f * pulse
        ),
        radius = 120f * pulse,
        center = Offset(centerX, centerY)
    )
}

/**
 * 绘制单个数据粒子
 */
private fun DrawScope.drawDataParticle(
    particle: DataParticle,
    time: Float,
    centerX: Float,
    centerY: Float,
    maxRadius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    isConnected: Boolean
) {
    // 计算粒子在路径上的位置
    val pathProgress = ((time * particle.speed * 0.01f + particle.pathOffset) % 1f)
    val adjustedProgress = if (particle.direction == 1) pathProgress else 1f - pathProgress
    
    // 计算螺旋路径位置
    val angle = adjustedProgress * 4f * Math.PI + particle.pathIndex * (2 * Math.PI / 5)
    val radius = adjustedProgress * maxRadius
    
    val x = centerX + (cos(angle) * radius).toFloat()
    val y = centerY + (sin(angle) * radius).toFloat() * 0.6f // 压扁成椭圆
    
    // 混合颜色
    val particleColor = blendColors(primaryColor, secondaryColor, particle.colorBlend)
    
    // 计算透明度（根据连接状态）
    val baseAlpha = if (isConnected) 0.8f else 0.3f
    val fadeAlpha = sin(adjustedProgress * Math.PI).toFloat()
    val alpha = baseAlpha * fadeAlpha
    
    // 绘制粒子拖尾
    val trailLength = particle.size * 3f
    val trailX = x - (cos(angle) * trailLength).toFloat()
    val trailY = y - (sin(angle) * trailLength * 0.6f).toFloat()
    
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                particleColor.copy(alpha = alpha),
                particleColor.copy(alpha = 0f)
            ),
            start = Offset(x, y),
            end = Offset(trailX, trailY)
        ),
        start = Offset(x, y),
        end = Offset(trailX, trailY),
        strokeWidth = particle.size
    )
    
    // 绘制粒子主体
    drawCircle(
        color = particleColor.copy(alpha = alpha),
        radius = particle.size,
        center = Offset(x, y)
    )
    
    // 高光点
    drawCircle(
        color = Color.White.copy(alpha = alpha * 0.8f),
        radius = particle.size * 0.4f,
        center = Offset(x - particle.size * 0.3f, y - particle.size * 0.3f)
    )
}

/**
 * 颜色混合函数
 */
private fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
    return Color(
        red = color1.red * (1 - ratio) + color2.red * ratio,
        green = color1.green * (1 - ratio) + color2.green * ratio,
        blue = color1.blue * (1 - ratio) + color2.blue * ratio,
        alpha = color1.alpha * (1 - ratio) + color2.alpha * ratio
    )
}

/**
 * 预览 - 数据流粒子效果（连接中）
 */
@Preview(device = "id:pixel_5")
@Composable
private fun DataFlowParticlesConnectingPreview() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.background(
            Color(0xFF111827),
            modifier = Modifier.fillMaxSize()
        )
        
        DataFlowParticles(
            particleCount = 40,
            flowDirection = 0,
            primaryColor = Color(0xFFF59E0B),
            secondaryColor = Color(0xFFFBBF24),
            isConnected = false
        )
        
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.foundation.layout.Column(
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
            ) {
                androidx.compose.material3.CircularProgressIndicator(
                    color = Color(0xFFF59E0B)
                )
                androidx.compose.material3.Text(
                    text = "Connecting...",
                    color = Color(0xFFF59E0B),
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }
    }
}

/**
 * 预览 - 数据流粒子效果（已连接）
 */
@Preview(device = "id:pixel_5")
@Composable
private fun DataFlowParticlesConnectedPreview() {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier.fillMaxSize()
    ) {
        androidx.compose.foundation.background(
            Color(0xFF111827),
            modifier = Modifier.fillMaxSize()
        )
        
        DataFlowParticles(
            particleCount = 50,
            flowDirection = 0,
            primaryColor = Color(0xFF22C55E),
            secondaryColor = Color(0xFF10B981),
            isConnected = true
        )
        
        androidx.compose.foundation.layout.Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Icon(
                imageVector = androidx.compose.material.icons.Icons.Default.CheckCircle,
                contentDescription = null,
                tint = Color(0xFF22C55E),
                modifier = Modifier.size(64.dp)
            )
        }
    }
}

// 导入需要的扩展
private val Int.dp: androidx.compose.ui.unit.Dp
    get() = androidx.compose.ui.unit.Dp(this.toFloat())

/**
 * 使用示例：
 * 
 * ```kotlin
 * // VPN连接状态页面
 * Box(modifier = Modifier.fillMaxSize()) {
 *     // 背景
 *     BackgroundPrimary()
 *     
 *     // 数据流粒子效果
 *     DataFlowParticles(
 *         particleCount = 50,
 *         flowDirection = 0, // 双向
 *         primaryColor = if (isConnected) Color(0xFF22C55E) else Color(0xFFF59E0B),
 *         isConnected = isConnected
 *     )
 *     
 *     // 连接状态UI
 *     ConnectionStatusUI()
 * }
 * ```
 */