package com.app.common.widgets

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.unit.dp
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.ProductionMotionProfile

@Composable
fun MetricPill(label: String, value: String) {
    val transition = rememberInfiniteTransition(label = "metric-pill")
    val ticker = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "metric-pill-ticker",
    )
    val animated = ProductionMotionProfile.isEnabled(EffectToggle.CounterTicker)
    val scale = if (animated) 0.992f + (ticker.value * 0.04f) else 1f
    Surface(
        modifier = Modifier.scale(scale),
        color = CardGlassStrong.copy(alpha = 0.66f),
        shape = RoundedCornerShape(22.dp),
        shadowElevation = 2.dp,
    ) {
        Column(Modifier.padding(horizontal = 14.dp, vertical = 10.dp)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
            Text(
                value,
                style = MaterialTheme.typography.labelLarge,
                color = if (animated) MaterialTheme.colorScheme.primary.copy(alpha = 0.8f + (ticker.value * 0.2f)) else MaterialTheme.colorScheme.onSurface,
            )
        }
    }
}
