package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.*

/**
 * VPN连接状态可视化组件
 * 
 * 包含：
 * 1. ConnectionVisualizer - 连接状态可视化（地球图标+动画）
 * 2. ConnectionButtonAnimation - 连接按钮动画
 * 3. ConnectionStatusIndicator - 连接状态指示器
 */

/**
 * 连接状态枚举
 */
enum class ConnectionState {
    DISCONNECTED,   // 未连接 - 灰色静态
    CONNECTING,     // 连接中 - 蓝色旋转+脉冲
    CONNECTED       // 已连接 - 绿色发光+呼吸
}

/**
 * 连接状态可视化 - 地球图标动画
 * 
 * @param state 连接状态
 * @param size 组件大小
 * @param onClick 点击回调
 */
@Composable
fun ConnectionVisualizer(
    modifier: Modifier = Modifier,
    state: ConnectionState = ConnectionState.DISCONNECTED,
    size: Float = 200f,
    onClick: (() -> Unit)? = null
) {
    val infiniteTransition = rememberInfiniteTransition(label = "connection")
    
    // 旋转动画（连接中）
    val rotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = if (state == ConnectionState.CONNECTING) 360f else 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation"
    )
    
    // 脉冲动画（已连接）
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    // 呼吸动画（已连接）
    val breathe by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )
    
    // 颜色根据状态变化
    val (primaryColor, secondaryColor, glowIntensity) = when (state) {
        ConnectionState.DISCONNECTED -> Triple(
            Color(0xFF6B7280),
            Color(0xFF4B5563),
            0f
        )
        ConnectionState.CONNECTING -> Triple(
            Color(0xFFF59E0B),
            Color(0xFFFBBF24),
            0.5f
        )
        ConnectionState.CONNECTED -> Triple(
            Color(0xFF22C55E),
            Color(0xFF10B981),
            1f
        )
    }
    
    val scale = if (state == ConnectionState.CONNECTED) breathe else 1f
    
    Box(
        modifier = modifier
            .size(size.dp)
            .aspectRatio(1f),
        contentAlignment = Alignment.Center
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationZ = rotation
                    scaleX = scale
                    scaleY = scale
                }
        ) {
            val centerX = sizeCenter.x
            val centerY = sizeCenter.y
            val radius = size / 2 * 0.7f
            
            // 绘制发光效果
            if (state != ConnectionState.DISCONNECTED) {
                drawGlowEffect(
                    centerX = centerX,
                    centerY = centerY,
                    radius = radius,
                    color = primaryColor,
                    intensity = glowIntensity * pulse
                )
            }
            
            // 绘制地球主体
            drawGlobe(
                centerX = centerX,
                centerY = centerY,
                radius = radius,
                primaryColor = primaryColor,
                secondaryColor = secondaryColor,
                state = state
            )
            
            // 绘制轨道环（连接中/已连接）
            if (state != ConnectionState.DISCONNECTED) {
                drawOrbitRings(
                    centerX = centerX,
                    centerY = centerY,
                    radius = radius,
                    color = primaryColor,
                    pulse = pulse,
                    rotation = rotation
                )
            }
            
            // 绘制数据流粒子
            if (state == ConnectionState.CONNECTED) {
                drawDataParticles(
                    centerX = centerX,
                    centerY = centerY,
                    radius = radius * 1.3f,
                    color = secondaryColor,
                    time = rotation
                )
            }
        }
        
        // 状态图标覆盖
        ConnectionStateOverlay(
            state = state,
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

/**
 * 绘制发光效果
 */
private fun DrawScope.drawGlowEffect(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color,
    intensity: Float
) {
    // 多层光晕
    for (i in 3 downTo 1) {
        val glowRadius = radius * (1 + i * 0.3f)
        val alpha = intensity * (0.4f / i)
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = alpha),
                    color.copy(alpha = alpha * 0.3f),
                    Color.Transparent
                ),
                center = Offset(centerX, centerY),
                radius = glowRadius
            ),
            radius = glowRadius,
            center = Offset(centerX, centerY)
        )
    }
}

/**
 * 绘制地球
 */
private fun DrawScope.drawGlobe(
    centerX: Float,
    centerY: Float,
    radius: Float,
    primaryColor: Color,
    secondaryColor: Color,
    state: ConnectionState
) {
    // 地球主体
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                primaryColor.copy(alpha = 0.9f),
                primaryColor.copy(alpha = 0.7f),
                primaryColor.copy(alpha = 0.5f)
            ),
            center = Offset(centerX - radius * 0.2f, centerY - radius * 0.2f),
            radius = radius
        ),
        radius = radius,
        center = Offset(centerX, centerY)
    )
    
    // 经纬线
    val lineAlpha = if (state == ConnectionState.DISCONNECTED) 0.2f else 0.4f
    
    // 经线
    for (i in 0..3) {
        val angle = i * 45f
        drawArc(
            color = Color.White.copy(alpha = lineAlpha),
            startAngle = angle,
            sweepAngle = 180f,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius * 0.6f),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 1.2f),
            style = Stroke(width = 1.5f)
        )
    }
    
    // 纬线
    for (i in -1..1) {
        val yOffset = i * radius * 0.4f
        val lineRadius = sqrt(radius * radius - yOffset * yOffset)
        drawLine(
            color = Color.White.copy(alpha = lineAlpha),
            start = Offset(centerX - lineRadius, centerY + yOffset),
            end = Offset(centerX + lineRadius, centerY + yOffset),
            strokeWidth = 1.5f
        )
    }
    
    // 高光
    drawCircle(
        color = Color.White.copy(alpha = 0.3f),
        radius = radius * 0.3f,
        center = Offset(centerX - radius * 0.3f, centerY - radius * 0.3f)
    )
}

/**
 * 绘制轨道环
 */
private fun DrawScope.drawOrbitRings(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color,
    pulse: Float,
    rotation: Float
) {
    // 外环
    drawCircle(
        color = color.copy(alpha = 0.3f * pulse),
        radius = radius * 1.4f,
        center = Offset(centerX, centerY),
        style = Stroke(width = 2f)
    )
    
    // 旋转的弧段
    drawArc(
        color = color.copy(alpha = 0.6f),
        startAngle = rotation,
        sweepAngle = 60f,
        useCenter = false,
        topLeft = Offset(centerX - radius * 1.4f, centerY - radius * 1.4f),
        size = androidx.compose.ui.geometry.Size(radius * 2.8f, radius * 2.8f),
        style = Stroke(width = 3f, cap = androidx.compose.ui.graphics.StrokeCap.Round)
    )
    
    // 内环
    drawCircle(
        color = color.copy(alpha = 0.2f * pulse),
        radius = radius * 1.15f,
        center = Offset(centerX, centerY),
        style = Stroke(width = 1f)
    )
}

/**
 * 绘制数据粒子
 */
private fun DrawScope.drawDataParticles(
    centerX: Float,
    centerY: Float,
    radius: Float,
    color: Color,
    time: Float
) {
    repeat(6) { i ->
        val angle = (time + i * 60f) * Math.PI / 180f
        val particleRadius = radius * (0.9f + 0.1f * sin(time * 0.1f + i))
        
        val x = centerX + (cos(angle) * particleRadius).toFloat()
        val y = centerY + (sin(angle) * particleRadius * 0.6f).toFloat()
        
        drawCircle(
            color = color.copy(alpha = 0.8f),
            radius = 4f,
            center = Offset(x, y)
        )
        
        // 粒子拖尾
        val tailX = centerX + (cos(angle - 0.3) * particleRadius).toFloat()
        val tailY = centerY + (sin(angle - 0.3) * particleRadius * 0.6f).toFloat()
        
        drawLine(
            color = color.copy(alpha = 0.4f),
            start = Offset(x, y),
            end = Offset(tailX, tailY),
            strokeWidth = 2f
        )
    }
}

/**
 * 状态图标覆盖
 */
@Composable
private fun ConnectionStateOverlay(
    state: ConnectionState,
    modifier: Modifier = Modifier
) {
    val (icon, color) = when (state) {
        ConnectionState.DISCONNECTED -> Pair(Icons.Default.Close, Color(0xFF6B7280))
        ConnectionState.CONNECTING -> Pair(Icons.Default.Refresh, Color(0xFFF59E0B))
        ConnectionState.CONNECTED -> Pair(Icons.Default.Check, Color(0xFF22C55E))
    }
    
    Surface(
        shape = CircleShape,
        color = Color(0xFF1F2937),
        modifier = modifier
            .size(40.dp)
            .padding(4.dp)
    ) {
        Box(contentAlignment = Alignment.Center) {
            if (state == ConnectionState.CONNECTING) {
                RotatingAnimation(duration = 1000) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = color,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

/**
 * 连接按钮动画
 */
@Composable
fun ConnectionButtonAnimation(
    modifier: Modifier = Modifier,
    state: ConnectionState = ConnectionState.DISCONNECTED,
    onClick: () -> Unit = {}
) {
    val infiniteTransition = rememberInfiniteTransition(label = "button")
    
    // 脉冲动画（已连接）
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "buttonPulse"
    )
    
    val (buttonColor, glowColor, glowRadius) = when (state) {
        ConnectionState.DISCONNECTED -> Triple(
            Color(0xFF1D4ED8),
            Color(0xFF1D4ED8),
            0.dp
        )
        ConnectionState.CONNECTING -> Triple(
            Color(0xFFF59E0B),
            Color(0xFFF59E0B),
            20.dp
        )
        ConnectionState.CONNECTED -> Triple(
            Color(0xFF22C55E),
            Color(0xFF22C55E),
            (30.dp * pulse)
        )
    }
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        // 发光效果
        if (state != ConnectionState.DISCONNECTED) {
            Canvas(modifier = Modifier.size(120.dp)) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            glowColor.copy(alpha = 0.5f),
                            glowColor.copy(alpha = 0.2f),
                            Color.Transparent
                        ),
                        center = center,
                        radius = glowRadius.toPx()
                    ),
                    radius = glowRadius.toPx(),
                    center = center
                )
            }
        }
        
        // 按钮
        Button(
            onClick = onClick,
            colors = ButtonDefaults.buttonColors(
                containerColor = buttonColor
            ),
            shape = CircleShape,
            modifier = Modifier.size(80.dp)
        ) {
            when (state) {
                ConnectionState.DISCONNECTED -> {
                    Text("GO", color = Color.White)
                }
                ConnectionState.CONNECTING -> {
                    RotatingAnimation(duration = 1000) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp
                        )
                    }
                }
                ConnectionState.CONNECTED -> {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }
        }
    }
}

/**
 * 连接状态指示器
 */
@Composable
fun ConnectionStatusIndicator(
    modifier: Modifier = Modifier,
    state: ConnectionState = ConnectionState.DISCONNECTED,
    serverName: String = "",
    duration: String = ""
) {
    val (statusText, statusColor) = when (state) {
        ConnectionState.DISCONNECTED -> Pair("Disconnected", Color(0xFF6B7280))
        ConnectionState.CONNECTING -> Pair("Connecting...", Color(0xFFF59E0B))
        ConnectionState.CONNECTED -> Pair("Connected", Color(0xFF22C55E))
    }
    
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 状态点
        Box(
            modifier = Modifier
                .size(12.dp)
                .background(statusColor, CircleShape)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        // 状态文字
        Text(
            text = statusText,
            color = statusColor,
            style = MaterialTheme.typography.titleMedium
        )
        
        // 服务器信息
        if (state == ConnectionState.CONNECTED && serverName.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = serverName,
                color = Color.Gray,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        
        // 连接时长
        if (state == ConnectionState.CONNECTED && duration.isNotEmpty()) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = duration,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

// ==================== 预览 ====================

@Preview(device = "id:pixel_5")
@Composable
private fun ConnectionVisualizerDisconnectedPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        ConnectionVisualizer(
            state = ConnectionState.DISCONNECTED,
            size = 200f
        )
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun ConnectionVisualizerConnectingPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        ConnectionVisualizer(
            state = ConnectionState.CONNECTING,
            size = 200f
        )
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun ConnectionVisualizerConnectedPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        ConnectionVisualizer(
            state = ConnectionState.CONNECTED,
            size = 200f
        )
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun ConnectionButtonAnimationPreview() {
    var state by remember { mutableStateOf(ConnectionState.DISCONNECTED) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            ConnectionButtonAnimation(
                state = state,
                onClick = {
                    state = when (state) {
                        ConnectionState.DISCONNECTED -> ConnectionState.CONNECTING
                        ConnectionState.CONNECTING -> ConnectionState.CONNECTED
                        ConnectionState.CONNECTED -> ConnectionState.DISCONNECTED
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(32.dp))
            
            ConnectionStatusIndicator(
                state = state,
                serverName = if (state == ConnectionState.CONNECTED) "USA - New York" else "",
                duration = if (state == ConnectionState.CONNECTED) "00:05:32" else ""
            )
        }
    }
}

/**
 * 使用示例：
 * 
 * ```kotlin
 * // 主连接页面
 * Column {
 *     // 连接可视化
 *     ConnectionVisualizer(
 *         state = connectionState,
 *         size = 200f,
 *         onClick = { toggleConnection() }
 *     )
 *     
 *     // 连接按钮
 *     ConnectionButtonAnimation(
 *         state = connectionState,
 *         onClick = { toggleConnection() }
 *     )
 *     
 *     // 状态指示器
 *     ConnectionStatusIndicator(
 *         state = connectionState,
 *         serverName = "USA - New York",
 *         duration = "00:05:32"
 *     )
 * }
 * ```
 */