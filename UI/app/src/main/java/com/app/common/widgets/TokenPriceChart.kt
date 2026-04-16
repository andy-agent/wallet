package com.app.common.widgets

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.app.data.model.TokenPricePoint
import com.app.core.theme.BluePrimary

@Composable
fun TokenPriceChart(points: List<TokenPricePoint>) {
    if (points.isEmpty()) return
    Canvas(modifier = Modifier.fillMaxWidth().height(140.dp)) {
        val max = points.maxOf { it.price }
        val min = points.minOf { it.price }
        val range = (max - min).takeIf { it != 0f } ?: 1f
        val gap = size.width / (points.size - 1).coerceAtLeast(1)
        val path = Path()
        points.forEachIndexed { index, item ->
            val x = gap * index
            val y = size.height - ((item.price - min) / range) * size.height
            if (index == 0) path.moveTo(x, y) else path.lineTo(x, y)
        }
        drawPath(path, color = BluePrimary, style = Stroke(width = 6f))
        points.forEachIndexed { index, item ->
            val x = gap * index
            val y = size.height - ((item.price - min) / range) * size.height
            drawCircle(color = BluePrimary, radius = 7f, center = Offset(x, y))
        }
    }
}
