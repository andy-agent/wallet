package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlin.math.*

/**
 * 特殊效果合集
 * 
 * 包含：
 * 1. GradientBackground - 渐变背景
 * 2. AnimatedBackground - 动态背景
 * 3. ProgressAnimation - 进度动画
 * 4. CountdownAnimation - 倒计时动画
 * 5. MatrixRainEffect - 矩阵雨效果
 */

// ==================== 渐变背景 ====================

/**
 * 渐变背景 - 支持多种渐变类型
 * 
 * @param gradientType 渐变类型
 * @param colors 渐变颜色列表
 * @param animate 是否启用动画
 */
@Composable
fun GradientBackground(
    modifier: Modifier = Modifier,
    gradientType: GradientType = GradientType.Linear,
    colors: List<Color> = listOf(
        Color(0xFF0B1020),
        Color(0xFF111827),
        Color(0xFF1F2937)
    ),
    animate: Boolean = true
) {
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    
    val shift by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "gradientShift"
    )
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val brush = when (gradientType) {
            GradientType.Linear -> Brush.linearGradient(
                colors = colors,
                start = Offset(0f, 0f),
                end = Offset(size.width, size.height)
            )
            GradientType.Radial -> Brush.radialGradient(
                colors = colors,
                center = center,
                radius = size.width * 0.8f
            )
            GradientType.Sweep -> Brush.sweepGradient(
                colors = colors,
                center = center
            )
            GradientType.AnimatedLinear -> {
                val animatedStart = Offset(
                    size.width * sin(shift * 2 * PI.toFloat()),
                    size.height * cos(shift * 2 * PI.toFloat())
                )
                val animatedEnd = Offset(
                    size.width * (1 - sin(shift * 2 * PI.toFloat())),
                    size.height * (1 - cos(shift * 2 * PI.toFloat()))
                )
                Brush.linearGradient(
                    colors = colors,
                    start = animatedStart,
                    end = animatedEnd
                )
            }
        }
        
        drawRect(brush = brush)
    }
}

enum class GradientType {
    Linear, Radial, Sweep, AnimatedLinear
}

// ==================== 动态背景 ====================

/**
 * 动态背景 - 带有浮动形状和光效
 */
@Composable
fun AnimatedBackground(
    modifier: Modifier = Modifier,
    shapeCount: Int = 5,
    primaryColor: Color = Color(0xFF1D4ED8),
    secondaryColor: Color = Color(0xFF06B6D4)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "animatedBg")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(20000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "time"
    )
    
    // 生成浮动形状
    val shapes = remember {
        List(shapeCount) { index ->
            FloatingShape(
                id = index,
                baseX = Random.nextFloat() * 0.8f + 0.1f,
                baseY = Random.nextFloat() * 0.8f + 0.1f,
                size = Random.nextFloat() * 100f + 50f,
                speed = Random.nextFloat() * 0.5f + 0.2f,
                phase = Random.nextFloat() * 360f,
                colorBlend = Random.nextFloat()
            )
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        // 基础渐变
        drawRect(
            brush = Brush.radialGradient(
                colors = listOf(
                    Color(0xFF111827),
                    Color(0xFF0B1020)
                ),
                center = center,
                radius = width
            )
        )
        
        // 绘制浮动形状
        shapes.forEach { shape ->
            drawFloatingShape(shape, time, width, height, primaryColor, secondaryColor)
        }
        
        // 绘制光斑
        drawLightSpots(time, width, height, primaryColor)
    }
}

private data class FloatingShape(
    val id: Int,
    val baseX: Float,
    val baseY: Float,
    val size: Float,
    val speed: Float,
    val phase: Float,
    val colorBlend: Float
)

private fun DrawScope.drawFloatingShape(
    shape: FloatingShape,
    time: Float,
    width: Float,
    height: Float,
    primaryColor: Color,
    secondaryColor: Color
) {
    val x = shape.baseX * width + sin((time * shape.speed + shape.phase) * PI / 180f).toFloat() * 50f
    val y = shape.baseY * height + cos((time * shape.speed * 0.7f + shape.phase) * PI / 180f).toFloat() * 30f
    
    val color = blendColors(primaryColor, secondaryColor, shape.colorBlend)
    val alpha = 0.1f + 0.1f * sin((time * 0.5f + shape.phase) * PI / 180f).toFloat()
    
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(
                color.copy(alpha = alpha),
                color.copy(alpha = alpha * 0.3f),
                Color.Transparent
            ),
            center = Offset(x, y),
            radius = shape.size
        ),
        radius = shape.size,
        center = Offset(x, y)
    )
}

private fun DrawScope.drawLightSpots(
    time: Float,
    width: Float,
    height: Float,
    color: Color
) {
    repeat(3) { i ->
        val spotX = width * (0.2f + i * 0.3f) + sin((time * 0.3f + i * 120f) * PI / 180f).toFloat() * 100f
        val spotY = height * (0.3f + i * 0.2f) + cos((time * 0.2f + i * 90f) * PI / 180f).toFloat() * 50f
        val spotAlpha = 0.05f + 0.05f * sin((time * 0.5f + i * 60f) * PI / 180f).toFloat()
        
        drawCircle(
            brush = Brush.radialGradient(
                colors = listOf(
                    color.copy(alpha = spotAlpha),
                    Color.Transparent
                ),
                center = Offset(spotX, spotY),
                radius = 200f
            ),
            radius = 200f,
            center = Offset(spotX, spotY)
        )
    }
}

private fun blendColors(color1: Color, color2: Color, ratio: Float): Color {
    return Color(
        red = color1.red * (1 - ratio) + color2.red * ratio,
        green = color1.green * (1 - ratio) + color2.green * ratio,
        blue = color1.blue * (1 - ratio) + color2.blue * ratio
    )
}

// ==================== 进度动画 ====================

/**
 * 进度动画 - 圆形进度条
 * 
 * @param progress 进度值 0-1
 * @param strokeWidth 线条宽度
 * @param color 进度颜色
 * @param trackColor 轨道颜色
 */
@Composable
fun CircularProgressAnimation(
    modifier: Modifier = Modifier,
    progress: Float,
    strokeWidth: Float = 8f,
    color: Color = Color(0xFF1D4ED8),
    trackColor: Color = Color(0xFF374151),
    animated: Boolean = true
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = if (animated) {
            tween(500, easing = FastOutSlowInEasing)
        } else {
            snap()
        },
        label = "progress"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "progressPulse")
    
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )
    
    Canvas(modifier = modifier) {
        val centerX = sizeCenter.x
        val centerY = sizeCenter.y
        val radius = (size.minDimension - strokeWidth) / 2
        
        // 轨道
        drawCircle(
            color = trackColor,
            radius = radius,
            center = Offset(centerX, centerY),
            style = Stroke(width = strokeWidth)
        )
        
        // 进度弧
        drawArc(
            color = color,
            startAngle = -90f,
            sweepAngle = 360f * animatedProgress,
            useCenter = false,
            topLeft = Offset(centerX - radius, centerY - radius),
            size = androidx.compose.ui.geometry.Size(radius * 2, radius * 2),
            style = Stroke(width = strokeWidth, cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        
        // 进度点发光
        if (animatedProgress > 0) {
            val angle = (-90f + 360f * animatedProgress) * PI / 180f
            val dotX = centerX + (cos(angle) * radius).toFloat()
            val dotY = centerY + (sin(angle) * radius).toFloat()
            
            drawCircle(
                color = color.copy(alpha = 0.5f),
                radius = strokeWidth * pulse,
                center = Offset(dotX, dotY)
            )
            
            drawCircle(
                color = Color.White,
                radius = strokeWidth * 0.4f,
                center = Offset(dotX, dotY)
            )
        }
    }
}

/**
 * 线性进度动画
 */
@Composable
fun LinearProgressAnimation(
    modifier: Modifier = Modifier,
    progress: Float,
    color: Color = Color(0xFF1D4ED8),
    trackColor: Color = Color(0xFF374151),
    height: Float = 8f
) {
    val animatedProgress by animateFloatAsState(
        targetValue = progress.coerceIn(0f, 1f),
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "linearProgress"
    )
    
    val infiniteTransition = rememberInfiniteTransition(label = "shimmer")
    
    val shimmer by infiniteTransition.animateFloat(
        initialValue = -1f,
        targetValue = 2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer"
    )
    
    Canvas(modifier = modifier) {
        val width = size.width
        val centerY = size.height / 2
        
        // 轨道
        drawRoundRect(
            color = trackColor,
            topLeft = Offset(0f, centerY - height / 2),
            size = androidx.compose.ui.geometry.Size(width, height),
            cornerRadius = androidx.compose.ui.geometry.CornerRadius(height / 2)
        )
        
        // 进度条
        if (animatedProgress > 0) {
            val progressWidth = width * animatedProgress
            
            drawRoundRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        color.copy(alpha = 0.8f),
                        color,
                        color.copy(alpha = 0.8f)
                    ),
                    start = Offset(0f, 0f),
                    end = Offset(progressWidth, 0f)
                ),
                topLeft = Offset(0f, centerY - height / 2),
                size = androidx.compose.ui.geometry.Size(progressWidth, height),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(height / 2)
            )
            
            // 闪光效果
            val shimmerX = shimmer * width
            drawRect(
                brush = Brush.linearGradient(
                    colors = listOf(
                        Color.Transparent,
                        Color.White.copy(alpha = 0.3f),
                        Color.Transparent
                    ),
                    start = Offset(shimmerX - 50f, 0f),
                    end = Offset(shimmerX + 50f, 0f)
                ),
                topLeft = Offset(0f, centerY - height / 2),
                size = androidx.compose.ui.geometry.Size(progressWidth, height)
            )
        }
    }
}

// ==================== 倒计时动画 ====================

/**
 * 倒计时动画
 * 
 * @param seconds 倒计时秒数
 * @param onComplete 完成回调
 */
@Composable
fun CountdownAnimation(
    modifier: Modifier = Modifier,
    seconds: Int = 3,
    color: Color = Color(0xFF1D4ED8),
    onComplete: (() -> Unit)? = null
) {
    var current by remember { mutableStateOf(seconds) }
    var isRunning by remember { mutableStateOf(true) }
    
    LaunchedEffect(isRunning) {
        if (isRunning) {
            while (current > 0) {
                delay(1000)
                current--
            }
            isRunning = false
            onComplete?.invoke()
        }
    }
    
    val scale by animateFloatAsState(
        targetValue = if (isRunning) 1f else 0f,
        animationSpec = tween(300),
        label = "countdownScale"
    )
    
    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        if (current > 0) {
            Text(
                text = current.toString(),
                color = color,
                style = MaterialTheme.typography.displayLarge,
                modifier = Modifier.scale(scale)
            )
        }
    }
}

// ==================== 矩阵雨效果 ====================

/**
 * 矩阵雨效果 - 科幻数字雨
 * 
 * @param columnCount 列数
 * @param speed 下落速度
 */
@Composable
fun MatrixRainEffect(
    modifier: Modifier = Modifier,
    columnCount: Int = 20,
    speed: Float = 1f,
    color: Color = Color(0xFF22C55E)
) {
    val infiniteTransition = rememberInfiniteTransition(label = "matrix")
    
    val time by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "matrixTime"
    )
    
    // 生成列数据
    val columns = remember {
        List(columnCount) { index ->
            MatrixColumn(
                x = index.toFloat() / columnCount,
                speed = Random.nextFloat() * 2f + 1f,
                length = Random.nextInt(5, 15),
                chars = List(20) { getRandomMatrixChar() }
            )
        }
    }
    
    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height
        
        columns.forEach { column ->
            drawMatrixColumn(column, time * speed, width, height, color)
        }
    }
}

private data class MatrixColumn(
    val x: Float,
    val speed: Float,
    val length: Int,
    val chars: List<Char>
)

private fun getRandomMatrixChar(): Char {
    val chars = "0123456789ABCDEF"
    return chars[Random.nextInt(chars.length)]
}

private fun DrawScope.drawMatrixColumn(
    column: MatrixColumn,
    time: Float,
    width: Float,
    height: Float,
    color: Color
) {
    val columnX = column.x * width
    val charHeight = 20f
    val headY = (time * column.speed * 2) % (height + column.length * charHeight)
    
    for (i in 0 until column.length) {
        val charY = headY - i * charHeight
        if (charY in -charHeight..height) {
            val alpha = when {
                i == 0 -> 1f
                i < 3 -> 0.8f - i * 0.2f
                else -> 0.4f - (i - 3) * 0.05f
            }.coerceIn(0f, 1f)
            
            val charIndex = ((charY / charHeight).toInt() + i) % column.chars.size
            val char = column.chars[charIndex.coerceIn(0, column.chars.size - 1)]
            
            // 绘制字符
            drawContext.canvas.nativeCanvas.apply {
                val paint = android.graphics.Paint().apply {
                    this.color = android.graphics.Color.argb(
                        (alpha * 255).toInt(),
                        (color.red * 255).toInt(),
                        (color.green * 255).toInt(),
                        (color.blue * 255).toInt()
                    )
                    textSize = 24f
                    typeface = android.graphics.Typeface.MONOSPACE
                }
                drawText(
                    char.toString(),
                    columnX,
                    charY,
                    paint
                )
            }
        }
    }
}

// ==================== 预览 ====================

@Preview(device = "id:pixel_5")
@Composable
private fun GradientBackgroundPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        GradientBackground(
            gradientType = GradientType.AnimatedLinear,
            colors = listOf(
                Color(0xFF0B1020),
                Color(0xFF1D4ED8),
                Color(0xFF0B1020)
            )
        )
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun AnimatedBackgroundPreview() {
    Box(modifier = Modifier.fillMaxSize()) {
        AnimatedBackground(
            shapeCount = 6,
            primaryColor = Color(0xFF1D4ED8),
            secondaryColor = Color(0xFF06B6D4)
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
private fun ProgressAnimationPreview() {
    var progress by remember { mutableStateOf(0.3f) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            // 圆形进度
            CircularProgressAnimation(
                progress = progress,
                strokeWidth = 12f,
                color = Color(0xFF22C55E),
                modifier = Modifier.size(150.dp)
            )
            
            // 线性进度
            LinearProgressAnimation(
                progress = progress,
                color = Color(0xFF1D4ED8),
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(16.dp)
            )
            
            // 控制按钮
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = { progress = (progress - 0.1f).coerceIn(0f, 1f) }) {
                    Text("-10%")
                }
                Button(onClick = { progress = (progress + 0.1f).coerceIn(0f, 1f) }) {
                    Text("+10%")
                }
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun CountdownAnimationPreview() {
    var showCountdown by remember { mutableStateOf(false) }
    
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF111827)),
        contentAlignment = Alignment.Center
    ) {
        if (showCountdown) {
            CountdownAnimation(
                seconds = 3,
                color = Color(0xFFF59E0B),
                onComplete = { showCountdown = false }
            )
        } else {
            Button(onClick = { showCountdown = true }) {
                Text("Start Countdown")
            }
        }
    }
}

@Preview(device = "id:pixel_5")
@Composable
private fun MatrixRainPreview() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0B1020))
    ) {
        MatrixRainEffect(
            columnCount = 25,
            speed = 1.5f,
            color = Color(0xFF22C55E)
        )
    }
}

// 导入需要的类
private val Random = kotlin.random.Random

/**
 * 使用示例：
 * 
 * ```kotlin
 * // 渐变背景
 * GradientBackground(
 *     gradientType = GradientType.AnimatedLinear,
 *     colors = listOf(Color(0xFF0B1020), Color(0xFF1D4ED8))
 * )
 * 
 * // 动态背景
 * AnimatedBackground(
 *     shapeCount = 6,
 *     primaryColor = Color(0xFF1D4ED8)
 * )
 * 
 * // 圆形进度
 * CircularProgressAnimation(
 *     progress = downloadProgress,
 *     color = Color(0xFF22C55E)
 * )
 * 
 * // 倒计时
 * CountdownAnimation(
 *     seconds = 3,
 *     onComplete = { startConnection() }
 * )
 * ```
 */