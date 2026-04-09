package com.cryptovpn.ui.effects

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
import androidx.compose.ui.graphics.drawscope.drawCircle
import androidx.compose.ui.graphics.drawscope.drawLine
import androidx.compose.ui.unit.dp
import com.cryptovpn.ui.theme.AppWhite
import com.cryptovpn.ui.theme.AuroraPurple
import com.cryptovpn.ui.theme.ElectricBlue
import com.cryptovpn.ui.theme.ElectricCyan
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
fun TechParticleBackground(
    motionProfile: MotionProfile,
    modifier: Modifier = Modifier,
    showNetwork: Boolean = true,
) {
    val seeds = remember(motionProfile) {
        List(motionProfile.particleCount) { index ->
            val random = Random(index * 91 + motionProfile.ordinal * 17)
            ParticleSeed(
                xRatio = random.nextFloat(),
                yRatio = random.nextFloat(),
                radius = random.nextFloat() * 5f + 1.5f,
                drift = random.nextFloat() * 28f + 8f,
            )
        }
    }
    val transition = rememberInfiniteTransition(label = "particle")
    val shift = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(motionProfile.orbitDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "shift",
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(AppWhite, Color(0xFFF8FBFF), Color(0xFFEEF4FF)),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(60.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(ElectricCyan.copy(alpha = 0.12f), Color.Transparent),
                        center = Offset(280f, 180f),
                        radius = 560f,
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
                        colors = listOf(AuroraPurple.copy(alpha = 0.10f), Color.Transparent),
                        center = Offset(840f, 260f),
                        radius = 600f,
                    ),
                    shape = CircleShape,
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            seeds.forEachIndexed { index, seed ->
                val cx = seed.xRatio * width + sin(shift.value * 6.2831f + index) * seed.drift
                val cy = seed.yRatio * height + cos(shift.value * 6.2831f + index) * seed.drift
                drawCircle(
                    color = if (index % 3 == 0) ElectricCyan else AuroraPurple,
                    radius = seed.radius,
                    center = Offset(cx, cy),
                    alpha = 0.16f + (index % 5) * 0.04f,
                )
            }

            if (showNetwork) {
                for (i in seeds.indices step 2) {
                    val startSeed = seeds[i]
                    val endSeed = seeds[(i + 5) % seeds.size]
                    val start = Offset(
                        x = startSeed.xRatio * width + sin(shift.value * 4f + i) * startSeed.drift,
                        y = startSeed.yRatio * height + cos(shift.value * 4f + i) * startSeed.drift,
                    )
                    val end = Offset(
                        x = endSeed.xRatio * width + sin(shift.value * 5f + i) * endSeed.drift,
                        y = endSeed.yRatio * height + cos(shift.value * 5f + i) * endSeed.drift,
                    )
                    drawLine(
                        color = ElectricBlue.copy(alpha = motionProfile.networkAlpha * 0.5f),
                        start = start,
                        end = end,
                        strokeWidth = 1.2f,
                    )
                    drawCircle(
                        color = ElectricBlue.copy(alpha = motionProfile.networkAlpha),
                        radius = 2.3f,
                        center = start,
                    )
                }
            }

            drawRect(
                brush = Brush.verticalGradient(
                    colors = listOf(Color.Transparent, Color(0x33FFFFFF), Color(0x18DCE9FF)),
                ),
                size = Size(width, height),
            )
        }
    }
}
