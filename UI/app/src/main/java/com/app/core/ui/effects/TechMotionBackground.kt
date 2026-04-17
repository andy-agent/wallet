package com.app.core.ui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.core.theme.AppWhite
import com.app.core.theme.GlowBlue
import com.app.core.theme.GlowCyan
import com.app.core.theme.GlowPurple
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

private data class ParticleSeed(
    val xRatio: Float,
    val yRatio: Float,
    val radius: Float,
    val drift: Float,
)

@Composable
fun TechMotionBackground(
    modifier: Modifier = Modifier,
    particleCount: Int,
    orbitDurationMs: Int,
    showParticles: Boolean,
    showNetwork: Boolean,
    showGridScan: Boolean,
    showOrb: Boolean,
    showOrbitalRings: Boolean,
    showScanBeam: Boolean,
    showDataRain: Boolean,
    showCornerBeacons: Boolean,
) {
    val transition = rememberInfiniteTransition(label = "effect-lab-bg")
    val particleShift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(orbitDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "particle-shift",
    )
    val gridShift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "grid-shift",
    )
    val seeds = remember(particleCount) {
        List(particleCount) { index ->
            val random = Random(index * 97 + particleCount * 13)
            ParticleSeed(
                xRatio = random.nextFloat(),
                yRatio = random.nextFloat(),
                radius = random.nextFloat() * 5f + 1.5f,
                drift = random.nextFloat() * 24f + 8f,
            )
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AppWhite, Color(0xFFF8FBFF), Color(0xFFEEF4FF)),
                ),
            ),
    ) {
        if (showOrb) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(60.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GlowCyan, Color.Transparent),
                            center = Offset(760f, 110f),
                            radius = 300f,
                        ),
                        shape = CircleShape,
                    ),
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(70.dp)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(GlowPurple, Color.Transparent),
                            center = Offset(860f, 90f),
                            radius = 240f,
                        ),
                        shape = CircleShape,
                    ),
            )
        }

        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            if (showGridScan) {
                val step = 32.dp.toPx()
                val scanOffset = gridShift.value * step
                var x = -step + scanOffset
                while (x <= width + step) {
                    drawLine(
                        color = GlowBlue.copy(alpha = 0.14f),
                        start = Offset(x, 0f),
                        end = Offset(x, height),
                        strokeWidth = 1f,
                    )
                    x += step
                }
                var y = -step + scanOffset
                while (y <= height + step) {
                    drawLine(
                        color = GlowBlue.copy(alpha = 0.11f),
                        start = Offset(0f, y),
                        end = Offset(width, y),
                        strokeWidth = 1f,
                    )
                    y += step
                }
            }

            if (showParticles || showNetwork) {
                val points = seeds.mapIndexed { index, seed ->
                    Offset(
                        x = seed.xRatio * width + sin(particleShift.value * 6.2831f + index) * seed.drift,
                        y = seed.yRatio * height + cos(particleShift.value * 6.2831f + index) * seed.drift,
                    )
                }
                if (showNetwork) {
                    for (i in points.indices step 2) {
                        val start = points[i]
                        val end = points[(i + 5) % points.size]
                        drawLine(
                            color = GlowBlue.copy(alpha = 0.42f),
                            start = start,
                            end = end,
                            strokeWidth = 1.2f,
                        )
                    }
                }
                if (showParticles) {
                    points.forEachIndexed { index, point ->
                        drawCircle(
                            color = if (index % 3 == 0) Color(0xFF25D7FF) else Color(0xFF8B78FF),
                            radius = seeds[index].radius,
                            center = point,
                            alpha = 0.18f + (index % 5) * 0.03f,
                        )
                    }
                }
            }

            if (showDataRain) {
                val columns = 18
                repeat(columns) { index ->
                    val x = (width / columns) * index + (gridShift.value * 14f)
                    val head = ((particleShift.value * height * 1.4f) + index * 42f) % (height + 120f)
                    drawLine(
                        color = Color(0xFF25D7FF).copy(alpha = 0.28f),
                        start = Offset(x, head),
                        end = Offset(x, head - 46f),
                        strokeWidth = 2f,
                    )
                    drawLine(
                        color = Color(0xFF8B78FF).copy(alpha = 0.18f),
                        start = Offset(x + 8f, (head + 120f) % (height + 120f)),
                        end = Offset(x + 8f, ((head + 120f) % (height + 120f)) - 28f),
                        strokeWidth = 1.4f,
                    )
                }
            }

            if (showScanBeam) {
                val beamY = (gridShift.value * (height + 220f)) - 110f
                drawRect(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color(0x3325D7FF), Color.Transparent),
                    ),
                    topLeft = Offset(0f, beamY),
                    size = Size(width, 110f),
                )
            }

            if (showCornerBeacons) {
                val beaconAlpha = 0.18f + (sin(particleShift.value * 6.2831f) * 0.08f).toFloat()
                val corners = listOf(
                    Offset(28f, 28f),
                    Offset(width - 28f, 28f),
                    Offset(28f, height - 28f),
                    Offset(width - 28f, height - 28f),
                )
                corners.forEach { corner ->
                    drawCircle(
                        color = GlowBlue.copy(alpha = beaconAlpha.coerceIn(0.1f, 0.32f)),
                        radius = 14f,
                        center = corner,
                    )
                    drawCircle(
                        color = Color.White.copy(alpha = 0.8f),
                        radius = 3.6f,
                        center = corner,
                    )
                }
            }

            if (showOrbitalRings) {
                val center = Offset(width * 0.82f, 86f)
                drawCircle(
                    color = GlowCyan.copy(alpha = 0.26f),
                    radius = 42f + particleShift.value * 4f,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 2.4f),
                )
                drawCircle(
                    color = GlowPurple.copy(alpha = 0.18f),
                    radius = 56f + gridShift.value * 6f,
                    center = center,
                    style = androidx.compose.ui.graphics.drawscope.Stroke(width = 1.8f),
                )
            }

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0x20FFFFFF), Color(0x14E5F4FF)),
                ),
                size = Size(width, height),
            )
        }
    }
}
