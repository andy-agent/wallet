package com.app.common.widgets

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipRect
import androidx.compose.ui.unit.dp
import com.app.common.components.GlassOutlinePanel
import com.app.common.components.StatusChip
import com.app.core.theme.BluePrimary
import com.app.core.theme.BorderSubtle
import com.app.core.theme.GridLine
import com.app.core.theme.TextPrimary
import com.app.core.theme.TextSecondary
import com.app.core.theme.TextTertiary
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.ProductionMotionProfile
import com.app.core.utils.Formatters
import com.app.data.model.TokenPricePoint

@Composable
fun TokenPriceChart(
    points: List<TokenPricePoint>,
    symbol: String? = null,
    chainId: String? = null,
    title: String? = null,
    modifier: Modifier = Modifier,
) {
    if (points.isEmpty()) return

    var armed by remember(points) { mutableStateOf(false) }
    LaunchedEffect(points) { armed = true }

    val animated = ProductionMotionProfile.isEnabled(EffectToggle.ChartDraw)
    val reveal by animateFloatAsState(
        targetValue = if (animated && armed) 1f else if (animated) 0.08f else 1f,
        animationSpec = tween(durationMillis = 1500, easing = LinearEasing),
        label = "token-chart-reveal",
    )

    val latest = points.last()
    val first = points.first()
    val high = points.maxOf { it.price }
    val low = points.minOf { it.price }
    val rawRange = high - low
    val plotRange = rawRange.takeIf { it != 0f } ?: 1f
    val mid = if (rawRange == 0f) high else low + (rawRange / 2f)
    val percentChange = if (first.price != 0f) ((latest.price - first.price) / first.price) * 100f else 0f
    val subtitleLine = listOfNotNull(title, chainId?.uppercase()).distinct().joinToString(" · ").ifBlank { "Blockchain index panel" }
    val xTickIndices = remember(points) { chartTickIndices(points.size) }
    val yAxisLabels = remember(points) { listOf(high, mid, low) }

    GlassOutlinePanel(
        modifier = modifier.fillMaxWidth(),
        radius = 28.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    TokenIcon(
                        symbol = symbol ?: "INDEX",
                        chainId = chainId,
                        size = 44.dp,
                    )
                    Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                        Text(
                            text = symbol?.uppercase() ?: "TOKEN INDEX",
                            style = MaterialTheme.typography.titleMedium,
                            color = TextPrimary,
                        )
                        Text(
                            text = subtitleLine,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                }
                Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = Formatters.money(latest.price.toDouble()),
                        style = MaterialTheme.typography.headlineSmall,
                        color = TextPrimary,
                    )
                    StatusChip(
                        text = Formatters.percent(percentChange.toDouble()),
                        positive = percentChange >= 0f,
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                ChartStatBlock(
                    label = "HIGH",
                    value = Formatters.money(high.toDouble()),
                    modifier = Modifier.weight(1f),
                )
                ChartStatBlock(
                    label = "LOW",
                    value = Formatters.money(low.toDouble()),
                    modifier = Modifier.weight(1f),
                )
                ChartStatBlock(
                    label = "LAST",
                    value = latest.label,
                    modifier = Modifier.weight(1f),
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(188.dp),
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val leftPad = 44.dp.toPx()
                    val rightPad = 12.dp.toPx()
                    val topPad = 10.dp.toPx()
                    val bottomPad = 24.dp.toPx()
                    val plotWidth = size.width - leftPad - rightPad
                    val plotHeight = size.height - topPad - bottomPad
                    val stepX = plotWidth / (points.size - 1).coerceAtLeast(1)
                    val revealRight = leftPad + (plotWidth * reveal)

                    drawRoundRect(
                        color = BorderSubtle.copy(alpha = 0.55f),
                        topLeft = Offset(leftPad, topPad),
                        size = Size(plotWidth, plotHeight),
                        cornerRadius = CornerRadius(26f, 26f),
                        style = Stroke(width = 2f),
                    )

                    repeat(4) { index ->
                        val y = topPad + (plotHeight / 3f) * index
                        drawLine(
                            color = GridLine.copy(alpha = 0.82f),
                            start = Offset(leftPad, y),
                            end = Offset(leftPad + plotWidth, y),
                            strokeWidth = 1f,
                        )
                    }
                    xTickIndices.forEach { index ->
                        val x = leftPad + (stepX * index)
                        drawLine(
                            color = GridLine.copy(alpha = 0.56f),
                            start = Offset(x, topPad),
                            end = Offset(x, topPad + plotHeight),
                            strokeWidth = 1f,
                        )
                    }

                    val linePath = Path()
                    val fillPath = Path()
                    var lastX = leftPad
                    points.forEachIndexed { index, item ->
                        val x = leftPad + (stepX * index)
                        val y = topPad + plotHeight - (((item.price - low) / plotRange) * plotHeight)
                        if (index == 0) {
                            linePath.moveTo(x, y)
                            fillPath.moveTo(x, topPad + plotHeight)
                            fillPath.lineTo(x, y)
                        } else {
                            linePath.lineTo(x, y)
                            fillPath.lineTo(x, y)
                        }
                        lastX = x
                    }
                    fillPath.lineTo(lastX, topPad + plotHeight)
                    fillPath.close()

                    clipRect(left = leftPad, right = revealRight) {
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    BluePrimary.copy(alpha = 0.28f),
                                    BluePrimary.copy(alpha = 0.08f),
                                    Color.Transparent,
                                ),
                                startY = topPad,
                                endY = topPad + plotHeight,
                            ),
                        )
                        drawPath(
                            path = linePath,
                            color = BluePrimary.copy(alpha = 0.2f),
                            style = Stroke(width = 10f),
                        )
                        drawPath(
                            path = linePath,
                            color = BluePrimary,
                            style = Stroke(width = 5f),
                        )
                        points.forEachIndexed { index, item ->
                            val x = leftPad + (stepX * index)
                            if (x > revealRight + 10f) return@forEachIndexed
                            val y = topPad + plotHeight - (((item.price - low) / plotRange) * plotHeight)
                            drawCircle(
                                color = Color.White.copy(alpha = 0.88f),
                                radius = 4.4f,
                                center = Offset(x, y),
                            )
                            drawCircle(
                                color = BluePrimary,
                                radius = 7.4f,
                                center = Offset(x, y),
                                style = Stroke(width = 2.6f),
                            )
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .fillMaxHeight()
                        .padding(top = 4.dp, bottom = 26.dp),
                    verticalArrangement = Arrangement.SpaceBetween,
                ) {
                    yAxisLabels.forEach { label ->
                        Text(
                            text = Formatters.compact(label.toDouble()),
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = 44.dp, end = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    xTickIndices.forEach { index ->
                        Text(
                            text = points[index].label,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ChartStatBlock(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .border(1.dp, BorderSubtle.copy(alpha = 0.65f), RoundedCornerShape(18.dp))
            .background(Color.White.copy(alpha = 0.42f), RoundedCornerShape(18.dp))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall,
            color = TextTertiary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.labelLarge,
            color = TextPrimary,
        )
    }
}

private fun chartTickIndices(size: Int): List<Int> {
    if (size <= 1) return listOf(0)
    return listOf(
        0,
        size / 3,
        (size - 1) * 2 / 3,
        size - 1,
    ).distinct().sorted()
}
