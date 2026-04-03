package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*
import kotlin.random.Random

/**
 * 能量粒子效果 - 用于按钮点击效果和能量爆发
 * 
 * 特点：
 * - 从中心向外扩散的能量粒子
 * - 粒子带有能量拖尾效果
 * - 支持多种能量颜色（蓝色、绿色、橙色等）
 * - 可触发一次性爆发效果
 * 
 * @param isActive 是否激活效果
 * @param energyColor 能量颜色
 * @param particleCount 粒子数量
 * @param burstMode 爆发模式（true=一次性爆发）
 */
@Composable
fun EnergyParticles(
    modifier: Modifier = Modifier,
    isActive: Boolean = true,
    energyColor: Color = Color(0xFF1D4ED8),
    particleCount: Int = 30,
    burstMode: Boolean = false,
    onBurstComplete: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "energy")
    
    // 持续脉冲动画
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // 粒子状态
    var particles by remember { mutableStateOf<List<EnergyParticle>>(emptyList()) }
    
    // 生成或更新粒子
    LaunchedEffect(isActive, burstMode) {
        if (burstMode && isActive) {
            // 爆发模式：一次性生成所有粒子
            particles = List(particleCount) {
                EnergyParticle(
                    angle = Random.nextFloat() * 360f,
                    speed = Random.nextFloat() * 3f + 2f,
                    size = Random.nextFloat() * 4f + 2f,
                    life = 1f,
                    decay = Random.nextFloat() * 0.02f + 0.01f,
                    colorVariation = Random.nextFloat()
                )
            }
            
            // 等待爆发完成
            delay(2000)
            onBurstComplete?.invoke()
        } else if (isActive && !burstMode) {
            // 持续模式：循环生成粒子
            while (true) {
                if (particles.size < particleCount) {
                    particles = particles + EnergyParticle(
                        angle = Random.nextFloat() * 360f,
                        speed = Random.nextFloat() * 2f + 1f,
                        size = Random.nextFloat() * 3f + 2f,
                        life = 1f,
                        decay = Random.nextFloat() * 0.01f + 0.005f,
                        colorVariation = Random.nextFloat()
                    )
                }
                delay(50)
            }
        }
    }
    
    // 更新粒子状态
    LaunchedEffect(particles, isActive) {
        if (!isActive) {
            particles = emptyList()
            return@LaunchedEffect
        }
        
        while (particles.isNotEmpty()) {
            delay(16) // ~60fps
            particles = particles.mapNotNull { particle ->
                val newLife = particle.life - particle.decay
                if (newLife > 0) {
                    particle.copy(life = newLife)
                } else null
            }
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        
        // 绘制中心能量核心
        if (isActive) {
            drawEnergyCore(centerX, centerY, energyColor, pulse)
        }
        
        // 绘制能量粒子
        particles.forEach { particle ->
            drawEnergyParticle(particle, centerX, centerY, energyColor)
        }
    }
}

/**
 * 能量粒子数据类
 */
private data class EnergyParticle(
    val angle: Float,
    val speed: Float,
    val size: Float,
    val life: Float,
    val decay: Float,
    val colorVariation: Float
)

/**
 * 绘制能量核心
 */
private fun DrawScope.drawEnergyCore(
    centerX: Float,
    centerY: Float,
    color: Color,
    pulse: Float
) {
    // 核心光晕
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = 0.8f),
                color.copy(alpha = 0.4f * pulse),
                color.copy(alpha = 0.1f * pulse),
                Color.Transparent
            ),
            center = Offset(centerX, centerY),
            radius = 40f + 20f * pulse
        ),
        radius = 40f + 20f * pulse,
        center = Offset(centerX, centerY)
    )
    
    // 内核心
    drawCircle(
        color = Color.White.copy(alpha = 0.9f),
        radius = 8f + 4f * pulse,
        center = Offset(centerX, centerY)
    )
    
    // 能量环
    drawCircle(
        color = color.copy(alpha = 0.6f * (1 - pulse)),
        radius = 25f + 15f * pulse,
        center = Offset(centerX, centerY),
        style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2f)
    )
}

/**
 * 绘制单个能量粒子
 */
private fun DrawScope.drawEnergyParticle(
    particle: EnergyParticle,
    centerX: Float,
    centerY: Float,
    baseColor: Color
) {
    val angleRad = particle.angle * Math.PI / 180f
    val distance = (1f - particle.life) * particle.speed * 100f
    
    val x = centerX + (cos(angleRad) * distance).toFloat()
    val y = centerY + (sin(angleRad) * distance).toFloat()
    
    // 计算颜色（带变化）
    val particleColor = if (particle.colorVariation > 0.7f) {
        Color.White
    } else if (particle.colorVariation > 0.4f) {
        baseColor.copy(red = min(1f, baseColor.red + 0.3f))
    } else {
        baseColor
    }
    
    // 计算透明度
    val alpha = particle.life * 0.8f
    
    // 绘制拖尾
    val tailLength = particle.size * 4f * particle.life
    val tailX = x - (cos(angleRad) * tailLength).toFloat()
    val tailY = y - (sin(angleRad) * tailLength).toFloat()
    
    drawLine(
        brush = Brush.linearGradient(
            colors = listOf(
                particleColor.copy(alpha = alpha),
                particleColor.copy(alpha = 0f)
            ),
            start = Offset(x, y),
            end = Offset(tailX, tailY)
        ),
        start = Offset(x, y),
        end = Offset(tailX, tailY),
        strokeWidth = particle.size * particle.life
    )
    
    // 绘制粒子主体
    drawCircle(
        color = particleColor.copy(alpha = alpha),
        radius = particle.size * particle.life,
        center = Offset(x, y)
    )
    
    // 高光
    drawCircle(
        color = Color.White.copy(alpha = alpha * 0.5f),
        radius = particle.size * 0.3f * particle.life,
        center = Offset(
            x - particle.size * 0.2f * particle.life,
            y - particle.size * 0.2f * particle.life
        )
    )
}

/**
 * 能量爆发效果 - 一次性爆发
 */
@Composable
fun EnergyBurst(
    modifier: Modifier = Modifier,
    trigger: Boolean,
    energyColor: Color = Color(0xFF1D4ED8),
    particleCount: Int = 40,
    onComplete: (() -> Unit)? = null
) {
    var showBurst by remember { mutableStateOf(false) }
    
    LaunchedEffect(trigger) {
        if (trigger) {
            showBurst = true
        }
    }
    
    if (showBurst) {
        EnergyParticles(
            modifier = modifier,
            isActive = true,
            energyColor = energyColor,
            particleCount = particleCount,
            burstMode = true,
            onBurstComplete = {
                showBurst = false
                onComplete?.invoke()
            }
        )
    }
}

/**
 * 预览 - 能量粒子效果（持续）
 */
@Preview(device = "id:pixel_5")
@Composable
private fun EnergyParticlesContinuousPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    ) {
        EnergyParticles(
            isActive = true,
            energyColor = Color(0xFF1D4ED8),
            particleCount = 30,
            burstMode = false
        )
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                )
            ) {
                Text("Connect VPN", color = Color.White)
            }
        }
    }
}

/**
 * 预览 - 能量爆发效果
 */
@Preview(device = "id:pixel_5")
@Composable
private fun EnergyBurstPreview() {
    var trigger by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827))
    ) {
        EnergyBurst(
            trigger = trigger,
            energyColor = Color(0xFF22C55E),
            particleCount = 50,
            onComplete = { trigger = false }
        )
        
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            Button(
                onClick = { trigger = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF22C55E)
                )
            ) {
                Text("Trigger Burst", color = Color.White)
            }
        }
    }
}

// 导入需要的组件
private val Int.dp: androidx.compose.ui.unit.Dp
    get() = androidx.compose.ui.unit.Dp(this.toFloat())

/**
 * 使用示例：
 * 
 * ```kotlin
 * // 按钮点击能量效果
 * Box(modifier = Modifier.fillMaxSize()) {
 *     EnergyParticles(
 *         isActive = isButtonPressed,
 *         energyColor = Color(0xFF1D4ED8),
 *         particleCount = 20
 *     )
 *     
 *     Button(onClick = { isButtonPressed = true }) {
 *         Text("Click Me")
 *     }
 * }
 * 
 * // 连接成功爆发效果
 * EnergyBurst(
 *     trigger = connectionSuccess,
 *     energyColor = Color(0xFF22C55E),
 *     particleCount = 50
 * )
 * ```
 */