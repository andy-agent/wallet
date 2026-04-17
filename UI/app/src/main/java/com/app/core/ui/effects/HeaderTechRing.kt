package com.app.core.ui.effects

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.app.core.theme.BluePrimary
import com.app.core.theme.BlueSecondary
import com.app.core.theme.GlowBlue
import com.app.core.theme.GlowCyan
import com.app.core.theme.GlowPurple
import com.app.core.theme.VioletAccent
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun HeaderTechRing(
    modifier: Modifier = Modifier,
    preset: HeaderRingPreset,
    enabledLayers: Set<HeaderRingLayer> = preset.layers,
    glyph: HeaderRingGlyph = preset.glyph,
) {
    val transition = rememberInfiniteTransition(label = "header-tech-ring")
    val orbitProgress = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(preset.orbitDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "header-orbit-progress",
    )
    val pulse = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "header-pulse",
    )
    val scanProgress = transition.animateFloat(
        initialValue = 1f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween((preset.orbitDurationMs * 0.72f).toInt(), easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "header-scan-progress",
    )

    Box(
        modifier = modifier.aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        if (HeaderRingLayer.PulseHalo in enabledLayers) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(10.dp)
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                GlowCyan.copy(alpha = 0.22f + (pulse.value * 0.08f)),
                                Color.Transparent,
                            ),
                        ),
                        shape = CircleShape,
                    ),
            )
        }
        Canvas(modifier = Modifier.fillMaxSize()) {
            val base = size.minDimension
            val outerRadius = base * 0.42f
            val innerRadius = base * 0.31f
            val orbitStroke = base * 0.056f
            val subtleStroke = base * 0.024f
            val centerOffset = center

            if (HeaderRingLayer.DashedOuter in enabledLayers) {
                drawCircle(
                    color = BluePrimary.copy(alpha = 0.28f),
                    radius = outerRadius,
                    center = centerOffset,
                    style = Stroke(
                        width = subtleStroke,
                        cap = StrokeCap.Round,
                        pathEffect = PathEffect.dashPathEffect(
                            intervals = floatArrayOf(base * 0.08f, base * 0.05f),
                        ),
                    ),
                )
            }

            if (HeaderRingLayer.TickMarks in enabledLayers) {
                repeat(12) { index ->
                    val angle = (index * 30f) - 90f
                    val radians = Math.toRadians(angle.toDouble())
                    val start = Offset(
                        x = centerOffset.x + cos(radians).toFloat() * (outerRadius + base * 0.028f),
                        y = centerOffset.y + sin(radians).toFloat() * (outerRadius + base * 0.028f),
                    )
                    val end = Offset(
                        x = centerOffset.x + cos(radians).toFloat() * (outerRadius + base * 0.068f),
                        y = centerOffset.y + sin(radians).toFloat() * (outerRadius + base * 0.068f),
                    )
                    drawLine(
                        color = BlueSecondary.copy(alpha = 0.22f),
                        start = start,
                        end = end,
                        strokeWidth = base * 0.014f,
                        cap = StrokeCap.Round,
                    )
                }
            }

            if (HeaderRingLayer.OrbitSweep in enabledLayers) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            BluePrimary.copy(alpha = 0.86f),
                            BlueSecondary.copy(alpha = 0.92f),
                            VioletAccent.copy(alpha = 0.72f),
                            Color.Transparent,
                        ),
                    ),
                    startAngle = orbitProgress.value * 360f,
                    sweepAngle = preset.sweepAngle,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - outerRadius, centerOffset.y - outerRadius),
                    size = Size(outerRadius * 2f, outerRadius * 2f),
                    style = Stroke(width = orbitStroke, cap = StrokeCap.Round),
                )
            }

            if (HeaderRingLayer.ScanArc in enabledLayers) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(
                            Color.Transparent,
                            GlowPurple.copy(alpha = 0.12f),
                            BlueSecondary.copy(alpha = 0.48f),
                            Color.Transparent,
                        ),
                    ),
                    startAngle = scanProgress.value * 360f,
                    sweepAngle = 46f,
                    useCenter = false,
                    topLeft = Offset(centerOffset.x - outerRadius * 0.92f, centerOffset.y - outerRadius * 0.92f),
                    size = Size(outerRadius * 1.84f, outerRadius * 1.84f),
                    style = Stroke(width = base * 0.024f, cap = StrokeCap.Round),
                )
            }

            if (HeaderRingLayer.InnerRing in enabledLayers) {
                drawCircle(
                    color = BluePrimary.copy(alpha = 0.22f),
                    radius = innerRadius,
                    center = centerOffset,
                    style = Stroke(width = base * 0.04f),
                )
                drawCircle(
                    color = BlueSecondary.copy(alpha = 0.16f),
                    radius = innerRadius * 0.78f,
                    center = centerOffset,
                    style = Stroke(width = base * 0.018f),
                )
            }

            if (HeaderRingLayer.SatelliteDots in enabledLayers && preset.nodeCount > 0) {
                repeat(preset.nodeCount) { index ->
                    val angle = ((orbitProgress.value * 360f) + (360f / preset.nodeCount) * index) - 90f
                    val radians = Math.toRadians(angle.toDouble())
                    val radius = outerRadius + base * 0.016f
                    val point = Offset(
                        x = centerOffset.x + cos(radians).toFloat() * radius,
                        y = centerOffset.y + sin(radians).toFloat() * radius,
                    )
                    drawCircle(
                        color = if (index % 2 == 0) BlueSecondary else VioletAccent,
                        radius = base * 0.03f,
                        center = point,
                        alpha = 0.74f,
                    )
                }
            }

            if (HeaderRingLayer.CoreGlow in enabledLayers) {
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            Color.White.copy(alpha = 0.92f),
                            GlowCyan.copy(alpha = 0.18f + (pulse.value * 0.08f)),
                            Color.Transparent,
                        ),
                        center = centerOffset,
                        radius = innerRadius * 1.25f,
                    ),
                    radius = innerRadius * 1.25f,
                    center = centerOffset,
                )
                drawCircle(
                    color = GlowBlue.copy(alpha = 0.18f),
                    radius = innerRadius * (0.92f + pulse.value * 0.05f),
                    center = centerOffset,
                )
            }
        }
        Box(
            modifier = Modifier
                .size(38.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(Color.White, Color(0xFFF4FAFF)),
                    ),
                    shape = CircleShape,
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = glyph.icon,
                contentDescription = glyph.title,
                tint = BluePrimary,
                modifier = Modifier.size(18.dp),
            )
        }
    }
}
