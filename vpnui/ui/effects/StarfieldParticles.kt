package com.cryptovpn.ui.effects

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.theme.CryptoVPNTheme
import kotlin.random.Random

@Composable
fun StarfieldParticles(
    starCount: Int = 200,
    shootingStarEnabled: Boolean = true,
    modifier: Modifier = Modifier
) {
    val stars = remember { generateStars(starCount) }
    val shootingStars = remember { generateShootingStars(3) }

    val infiniteTransition = rememberInfiniteTransition(label = "starfield")

    val twinkle = infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "twinkle"
    )

    Canvas(modifier = modifier.fillMaxSize()) {
        // Draw static stars with twinkle effect
        stars.forEach { star ->
            val alpha = if (star.twinkle) {
                twinkle.value * star.alpha
            } else {
                star.alpha
            }
            drawCircle(
                color = Color.White.copy(alpha = alpha),
                radius = star.size,
                center = Offset(star.x * size.width, star.y * size.height)
            )
        }

        // Draw shooting stars
        if (shootingStarEnabled) {
            shootingStars.forEach { shootingStar ->
                // Simplified shooting star visualization
                drawLine(
                    color = Color.White.copy(alpha = 0.8f),
                    start = Offset(
                        shootingStar.x * size.width,
                        shootingStar.y * size.height
                    ),
                    end = Offset(
                        (shootingStar.x - 0.1f) * size.width,
                        (shootingStar.y + 0.1f) * size.height
                    ),
                    strokeWidth = 2f
                )
            }
        }
    }
}

private data class Star(
    val x: Float,
    val y: Float,
    val size: Float,
    val alpha: Float,
    val twinkle: Boolean
)

private data class ShootingStar(
    val x: Float,
    val y: Float,
    val speed: Float
)

private fun generateStars(count: Int): List<Star> {
    return List(count) {
        Star(
            x = Random.nextFloat(),
            y = Random.nextFloat(),
            size = Random.nextFloat() * 2f + 0.5f,
            alpha = Random.nextFloat() * 0.7f + 0.3f,
            twinkle = Random.nextBoolean()
        )
    }
}

private fun generateShootingStars(count: Int): List<ShootingStar> {
    return List(count) {
        ShootingStar(
            x = Random.nextFloat(),
            y = Random.nextFloat() * 0.5f,
            speed = Random.nextFloat() * 0.5f + 0.2f
        )
    }
}

@Preview
@Composable
fun StarfieldParticlesPreview() {
    CryptoVPNTheme {
        StarfieldParticles(starCount = 100)
    }
}