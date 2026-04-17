package com.app.common.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.app.core.theme.AppDimens
import com.app.core.theme.BorderSubtle
import com.app.core.theme.CardGlass
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.GlowBlue
import com.app.core.theme.GlowCyan
import com.app.core.theme.TextSecondary
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.ProductionMotionProfile

@Composable
fun GradientCard(
    modifier: Modifier = Modifier,
    title: String? = null,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    var entered by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { entered = true }

    val entranceEnabled = ProductionMotionProfile.isEnabled(EffectToggle.CardEntrance)
    val shimmerEnabled = ProductionMotionProfile.isEnabled(EffectToggle.GlassShimmer)
    val alpha by animateFloatAsState(
        targetValue = if (entranceEnabled && entered) 1f else if (entranceEnabled) 0f else 1f,
        animationSpec = tween(durationMillis = 520),
        label = "gradient-card-alpha",
    )
    val translationY by animateFloatAsState(
        targetValue = if (entranceEnabled && entered) 0f else if (entranceEnabled) 36f else 0f,
        animationSpec = tween(durationMillis = 560),
        label = "gradient-card-translate",
    )
    val transition = rememberInfiniteTransition(label = "gradient-card-shimmer")
    val shimmerShift = transition.animateFloat(
        initialValue = -0.5f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "gradient-card-shift",
    )

    Box(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer(
                alpha = alpha,
                translationY = translationY,
            ),
    ) {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, BorderSubtle, RoundedCornerShape(AppDimens.cardRadius))
                .clip(RoundedCornerShape(AppDimens.cardRadius)),
            color = CardGlassStrong,
            shadowElevation = 14.dp,
            shape = RoundedCornerShape(AppDimens.cardRadius),
        ) {
            Box(
                modifier = Modifier.background(
                    Brush.verticalGradient(
                        listOf(CardGlassStrong, CardGlass, Color.White),
                    ),
                ),
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(GlowCyan, Color.Transparent),
                                center = Offset(920f, 80f),
                                radius = 360f,
                            ),
                        ),
                )
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(GlowBlue, Color.Transparent),
                                center = Offset(80f, 40f),
                                radius = 320f,
                            ),
                        ),
                )
                if (shimmerEnabled) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.White.copy(alpha = 0.16f),
                                        Color.Transparent,
                                    ),
                                    start = Offset(shimmerShift.value * 1100f, 0f),
                                    end = Offset((shimmerShift.value + 0.25f) * 1100f, 860f),
                                ),
                            ),
                    )
                }
                Column(
                    modifier = Modifier.padding(AppDimens.cardPadding),
                ) {
                    if (title != null) {
                        Text(title, style = MaterialTheme.typography.titleMedium)
                    }
                    if (subtitle != null) {
                        Spacer(Modifier.height(4.dp))
                        Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                        Spacer(Modifier.height(12.dp))
                    }
                    content()
                }
            }
        }
    }
}
