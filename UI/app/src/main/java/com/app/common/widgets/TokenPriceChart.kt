package com.app.common.widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.app.core.theme.BluePrimary
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.ProductionMotionProfile
import com.app.data.model.TokenPricePoint

@Composable
fun TokenPriceChart(points: List<TokenPricePoint>) {
    if (points.isEmpty()) return
    var armed by remember(points) { mutableStateOf(false) }
    LaunchedEffect(points) { armed = true }
    val animated = ProductionMotionProfile.isEnabled(EffectToggle.ChartDraw)
    val reveal by animateFloatAsState(
        targetValue = if (animated && armed) 1f else if (animated) 0.08f else 1f,
        animationSpec = tween(durationMillis = 1500, easing = LinearEasing),
        label = "token-chart-reveal",
    )
    Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
        val max = points.maxOf { it.price }
        val min = points.minOf { it.price }
        val range = (max - min).takeIf { it != 0f } ?: 1f
        val gap = size.width / (points.size - 1).coerceAtLeast(1)
        val path = Path()
        val progressWidth = size.width * reveal
        points.forEachIndexed { index, item ->
            val x = gap * index
            val y = size.height - ((item.price - min) / range) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        clipRect(right = progressWidth) {
            drawPath(path, color = BluePrimary, style = Stroke(width = 6f))
            points.forEachIndexed { index, item ->
                val x = gap * index
                if (x > progressWidth + 8f) return@forEachIndexed
                val y = size.height - ((item.price - min) / range) * size.height
                drawCircle(color = BluePrimary, radius = 7f, center = Offset(x, y))
            }
        }
    }
}
