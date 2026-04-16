package com.v2ray.ang.composeui.effects

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.theme.AuroraPurple
import com.v2ray.ang.composeui.theme.ElectricBlue
import com.v2ray.ang.composeui.theme.ElectricCyan
import com.v2ray.ang.composeui.theme.SignalGreen
import com.v2ray.ang.composeui.theme.StrokeSoft

@Deprecated("Keep as a VPN-specific visual effect; do not treat as a common component.")
@Composable
fun ConnectionHero(
    status: VpnConnectionStatus,
    motionProfile: MotionProfile,
    modifier: Modifier = Modifier,
) {
    val transition = rememberInfiniteTransition(label = "hero")
    val rotation = transition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(motionProfile.orbitDurationMs, easing = LinearEasing),
            repeatMode = RepeatMode.Restart,
        ),
        label = "rotation",
    )
    val pulse = animateFloatAsState(
        targetValue = when (status) {
            VpnConnectionStatus.DISCONNECTED -> 0.90f
            VpnConnectionStatus.CONNECTING -> 1.08f
            VpnConnectionStatus.CONNECTED -> 1.02f
        },
        animationSpec = tween(motionProfile.pulseDurationMs),
        label = "pulse",
    )
    val statusColor = when (status) {
        VpnConnectionStatus.DISCONNECTED -> StrokeSoft
        VpnConnectionStatus.CONNECTING -> ElectricCyan
        VpnConnectionStatus.CONNECTED -> SignalGreen
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stroke = 4.dp.toPx()
            val radiusBase = size.minDimension / 2.8f
            repeat(3) { index ->
                drawCircle(
                    brush = Brush.sweepGradient(
                        listOf(
                            statusColor.copy(alpha = 0.08f),
                            ElectricBlue.copy(alpha = 0.14f),
                            AuroraPurple.copy(alpha = 0.10f),
                            statusColor.copy(alpha = 0.08f),
                        ),
                    ),
                    radius = radiusBase + index * 42f,
                    style = Stroke(width = stroke),
                    alpha = 0.9f,
                )
            }

            drawArc(
                brush = Brush.sweepGradient(
                    listOf(ElectricBlue, ElectricCyan, AuroraPurple, ElectricBlue),
                ),
                startAngle = rotation.value,
                sweepAngle = 110f,
                useCenter = false,
                style = Stroke(width = 10.dp.toPx()),
                size = androidx.compose.ui.geometry.Size(size.width * 0.72f, size.height * 0.72f),
                topLeft = androidx.compose.ui.geometry.Offset(
                    x = size.width * 0.14f,
                    y = size.height * 0.14f,
                ),
                alpha = 0.72f,
            )
            drawCircle(
                color = statusColor.copy(alpha = 0.12f),
                radius = radiusBase * pulse.value,
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.72f),
                radius = radiusBase * 0.72f,
            )
        }
        Icon(
            imageVector = Icons.Rounded.Language,
            contentDescription = null,
            tint = statusColor,
            modifier = Modifier.fillMaxWidth(0.20f),
        )
    }
}
