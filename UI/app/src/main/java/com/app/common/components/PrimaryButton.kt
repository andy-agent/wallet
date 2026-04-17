package com.app.common.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.unit.dp
import com.app.core.theme.AppDimens
import com.app.core.theme.BluePrimary
import com.app.core.theme.BlueSecondary
import com.app.core.theme.TextTertiary
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.ProductionMotionProfile

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val background = if (enabled) {
        Brush.horizontalGradient(listOf(BluePrimary, BlueSecondary))
    } else {
        Brush.horizontalGradient(listOf(TextTertiary.copy(alpha = 0.4f), TextTertiary.copy(alpha = 0.28f)))
    }
    val transition = rememberInfiniteTransition(label = "primary-button")
    val pulse = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1700, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "primary-button-pulse",
    )
    val pulseEnabled = enabled && ProductionMotionProfile.isEnabled(EffectToggle.ButtonPulse)
    val scale = if (pulseEnabled) 0.992f + (pulse.value * 0.028f) else 1f
    val glowBrush = Brush.horizontalGradient(
        listOf(
            BluePrimary.copy(alpha = if (pulseEnabled) 0.1f + (pulse.value * 0.12f) else 0f),
            BlueSecondary.copy(alpha = if (pulseEnabled) 0.08f + (pulse.value * 0.1f) else 0f),
        ),
    )
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(AppDimens.buttonHeight),
        contentAlignment = Alignment.Center,
    ) {
        if (pulseEnabled) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .scale(scale)
                    .clip(RoundedCornerShape(20.dp))
                    .background(glowBrush),
            )
        }
        Box(
            modifier = Modifier
                .fillMaxSize()
                .scale(scale)
                .clip(RoundedCornerShape(20.dp))
                .background(background)
                .clickable(enabled = enabled, onClick = onClick),
            contentAlignment = Alignment.Center,
        ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
        )
        }
    }
}
