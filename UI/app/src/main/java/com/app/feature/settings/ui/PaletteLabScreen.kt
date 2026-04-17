package com.app.feature.settings.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.common.components.SecondaryButton
import com.app.common.components.SectionHeader
import com.app.core.theme.AppTypography
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold

@Composable
fun PaletteLabScreen(
    onBack: () -> Unit = {},
    onOpenEffectLab: () -> Unit = {},
) {
    var focusedPaletteId by rememberSaveable { mutableStateOf(PalettePreviewPalettes.first().id) }
    val focusedPalette = PalettePreviewPalettes.firstOrNull { it.id == focusedPaletteId } ?: PalettePreviewPalettes.first()

    AppScaffold(title = "Palette Lab", onBack = onBack, useProductionMotion = false) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(title = "配色实验室", subtitle = "只做 Dashboard 方向预览，不替换正式主题") {
                    Text(
                        "这里固定同一套 dashboard 版式，只切换色彩方向和模块 accent。先在这里看真机观感，再决定后续是否进入正式 rollout。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        "当前聚焦：${focusedPalette.title}。它更适合 ${focusedPalette.bestFor}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(text = "查看动效实验室", onClick = onOpenEffectLab)
                }
            }
            item { SectionHeader("方向切换") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PalettePreviewPalettes.forEach { palette ->
                        FilterChip(
                            selected = focusedPaletteId == palette.id,
                            onClick = { focusedPaletteId = palette.id },
                            label = { Text(palette.title) },
                        )
                    }
                }
            }
            item {
                GradientCard(title = focusedPalette.title, subtitle = focusedPalette.summary) {
                    Text(
                        "适配页面：${focusedPalette.bestFor}",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "模块 accent：${focusedPalette.moduleAccent}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        "试点建议：${focusedPalette.rolloutNote}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                }
            }
            item { SectionHeader("同版式对比") }
            items(PalettePreviewPalettes, key = { it.id }) { palette ->
                GradientCard(title = palette.title, subtitle = palette.rolloutNote) {
                    Text(
                        "同一套 dashboard 内容，只替换调色和模块 accent，方便直接判断哪种方向更稳。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    PaletteDashboardPreview(
                        palette = palette,
                        modifier = Modifier.fillMaxWidth(),
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    if (focusedPaletteId == palette.id) {
                        PrimaryButton(text = "当前聚焦方案", onClick = {})
                    } else {
                        SecondaryButton(text = "设为聚焦方案", onClick = { focusedPaletteId = palette.id })
                    }
                }
            }
            item {
                GradientCard(title = "使用说明", subtitle = "这里不是正式主题切换器") {
                    Text(
                        "这个页面只用来帮助人工确认颜色方向。真正 rollout 时，应该把选中的 palette 拆进 token 和模块级样式，而不是直接把这里的预览代码接到全局主题。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun PaletteDashboardPreview(
    palette: PalettePreviewPalette,
    modifier: Modifier = Modifier,
) {
    val colors = palette.colors
    Box(
        modifier = modifier
            .height(558.dp)
            .clip(RoundedCornerShape(34.dp))
            .border(
                width = 1.dp,
                color = colors.frameBorder.copy(alpha = 0.72f),
                shape = RoundedCornerShape(34.dp),
            )
            .background(
                brush = Brush.verticalGradient(
                    listOf(colors.canvasTop, colors.canvasBottom),
                ),
            ),
    ) {
        PaletteBackdrop(colors = colors)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            PaletteTopBar(colors = colors)
            PaletteHeroCard(colors = colors)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                PaletteMiniMetricCard(
                    title = "节点健康",
                    value = "96%",
                    subtitle = "Latency 42ms",
                    colors = colors,
                    modifier = Modifier.weight(1f),
                )
                PaletteMiniMetricCard(
                    title = "模块联动",
                    value = "7 / 9",
                    subtitle = "Wallet + VPN + Market",
                    colors = colors,
                    modifier = Modifier.weight(1f),
                )
            }
            PaletteSectionLabel(title = "模块速览", colors = colors)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
                PaletteActionTile(title = "钱包", subtitle = "USDT +8.4%", colors = colors, modifier = Modifier.weight(1f))
                PaletteActionTile(title = "网络", subtitle = "东京 / 新加坡", colors = colors, modifier = Modifier.weight(1f))
                PaletteActionTile(title = "市场", subtitle = "SOL 强势", colors = colors, modifier = Modifier.weight(1f))
            }
            PaletteChartCard(colors = colors)
            PaletteWatchlistCard(colors = colors)
            PaletteBottomBar(colors = colors)
        }
    }
}

@Composable
private fun PaletteBackdrop(colors: PalettePreviewColors) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(54.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.glowPrimary, Color.Transparent),
                        center = Offset(140f, 120f),
                        radius = 420f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(62.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(colors.glowSecondary, Color.Transparent),
                        center = Offset(900f, 220f),
                        radius = 560f,
                    ),
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            val step = 42.dp.toPx()
            var x = 0f
            while (x <= size.width) {
                drawLine(
                    color = colors.frameBorder.copy(alpha = 0.12f),
                    start = Offset(x, 0f),
                    end = Offset(x, size.height),
                    strokeWidth = 1f,
                )
                x += step
            }
            var y = 0f
            while (y <= size.height) {
                drawLine(
                    color = colors.frameBorder.copy(alpha = 0.09f),
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1f,
                )
                y += step
            }
        }
    }
}

@Composable
private fun PaletteTopBar(colors: PalettePreviewColors) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(
                text = "Dashboard",
                style = AppTypography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.title,
            )
            Text(
                text = "Screenshot-inspired preview shell",
                style = AppTypography.bodySmall,
                color = colors.body,
            )
        }
        PaletteBadge(
            text = "Preview",
            background = colors.accentSoft.copy(alpha = 0.88f),
            foreground = colors.accentStart,
        )
    }
}

@Composable
private fun PaletteHeroCard(colors: PalettePreviewColors) {
    PalettePanel(colors = colors) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.weight(1f)) {
                Text(
                    text = "总览资产",
                    style = AppTypography.bodyMedium,
                    color = colors.body,
                )
                Text(
                    text = "¥ 182,640.00",
                    style = AppTypography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                    color = colors.title,
                )
                Text(
                    text = "同一版式下只切配色，方便对比真实观感。",
                    style = AppTypography.bodySmall,
                    color = colors.quiet,
                )
            }
            Spacer(modifier = Modifier.size(12.dp))
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.linearGradient(
                            listOf(colors.accentStart, colors.accentEnd),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = "24h",
                    style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.accentText,
                )
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            PaletteBadge(text = "+12.4%", background = colors.accentSoft, foreground = colors.accentStart)
            PaletteBadge(text = "节点稳定", background = colors.positive.copy(alpha = 0.14f), foreground = colors.positive)
            PaletteBadge(text = "风险可控", background = colors.warning.copy(alpha = 0.14f), foreground = colors.warning)
        }
        Spacer(modifier = Modifier.height(14.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            PaletteInlineMetric(label = "净流入", value = "¥ 24,800", colors = colors, modifier = Modifier.weight(1f))
            PaletteInlineMetric(label = "活跃模块", value = "3", colors = colors, modifier = Modifier.weight(1f))
            PaletteInlineMetric(label = "今日告警", value = "2", colors = colors, modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun PaletteMiniMetricCard(
    title: String,
    value: String,
    subtitle: String,
    colors: PalettePreviewColors,
    modifier: Modifier = Modifier,
) {
    PalettePanel(colors = colors, modifier = modifier) {
        Text(
            text = title,
            style = AppTypography.bodySmall,
            color = colors.body,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = value,
            style = AppTypography.titleLarge.copy(fontWeight = FontWeight.Bold),
            color = colors.title,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = AppTypography.bodySmall,
            color = colors.quiet,
        )
    }
}

@Composable
private fun PaletteSectionLabel(
    title: String,
    colors: PalettePreviewColors,
) {
    Text(
        text = title,
        style = AppTypography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
        color = colors.title,
    )
}

@Composable
private fun PaletteActionTile(
    title: String,
    subtitle: String,
    colors: PalettePreviewColors,
    modifier: Modifier = Modifier,
) {
    PalettePanel(colors = colors, modifier = modifier) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .clip(CircleShape)
                .background(colors.accentSoft),
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
            text = title,
            style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colors.title,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = subtitle,
            style = AppTypography.bodySmall,
            color = colors.body,
        )
    }
}

@Composable
private fun PaletteChartCard(colors: PalettePreviewColors) {
    PalettePanel(colors = colors) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "资金趋势",
                    style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = colors.title,
                )
                Text(
                    text = "同一数据，观察不同 palette 下的图表可读性",
                    style = AppTypography.bodySmall,
                    color = colors.quiet,
                )
            }
            PaletteBadge(
                text = "Live +12.4%",
                background = colors.accentSoft,
                foreground = colors.accentStart,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(122.dp),
        ) {
            val points = listOf(0.28f, 0.44f, 0.36f, 0.58f, 0.54f, 0.79f, 0.72f, 0.88f)
            val stepX = size.width / (points.size - 1)
            val linePath = Path().apply {
                points.forEachIndexed { index, value ->
                    val x = stepX * index
                    val y = size.height - (size.height * value)
                    if (index == 0) {
                        moveTo(x, y)
                    } else {
                        lineTo(x, y)
                    }
                }
            }
            val fillPath = Path().apply {
                addPath(linePath)
                lineTo(size.width, size.height)
                lineTo(0f, size.height)
                close()
            }

            drawRoundRect(
                color = colors.accentSoft.copy(alpha = 0.22f),
                cornerRadius = CornerRadius(28f, 28f),
            )
            drawPath(
                path = fillPath,
                brush = Brush.verticalGradient(
                    colors = listOf(colors.chartFill.copy(alpha = 0.34f), Color.Transparent),
                    startY = 0f,
                    endY = size.height,
                ),
            )
            drawPath(
                path = linePath,
                color = colors.chartLine,
                style = Stroke(width = 5f, cap = StrokeCap.Round),
            )
            points.forEachIndexed { index, value ->
                drawCircle(
                    color = colors.shellTop,
                    radius = 6f,
                    center = Offset(stepX * index, size.height - (size.height * value)),
                )
                drawCircle(
                    color = colors.chartLine,
                    radius = 3.4f,
                    center = Offset(stepX * index, size.height - (size.height * value)),
                )
            }
        }
    }
}

@Composable
private fun PaletteWatchlistCard(colors: PalettePreviewColors) {
    PalettePanel(colors = colors) {
        Text(
            text = "重点模块",
            style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colors.title,
        )
        Spacer(modifier = Modifier.height(12.dp))
        PaletteWatchlistRow(
            ticker = "SOL",
            subtitle = "资金偏强 · 风险可控",
            value = "+7.42%",
            valueColor = colors.positive,
            colors = colors,
        )
        Spacer(modifier = Modifier.height(10.dp))
        PaletteWatchlistRow(
            ticker = "USDT",
            subtitle = "支付模块核心资产",
            value = "核心",
            valueColor = colors.accentStart,
            colors = colors,
        )
    }
}

@Composable
private fun PaletteWatchlistRow(
    ticker: String,
    subtitle: String,
    value: String,
    valueColor: Color,
    colors: PalettePreviewColors,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.weight(1f)) {
            Text(
                text = ticker,
                style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                color = colors.title,
            )
            Text(
                text = subtitle,
                style = AppTypography.bodySmall,
                color = colors.body,
            )
        }
        Spacer(modifier = Modifier.size(12.dp))
        Text(
            text = value,
            style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = valueColor,
        )
    }
}

@Composable
private fun PaletteBottomBar(colors: PalettePreviewColors) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = colors.shellTop.copy(alpha = 0.86f),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, colors.frameBorder.copy(alpha = 0.48f)),
        shadowElevation = 0.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            listOf("总览", "钱包", "市场", "我的").forEachIndexed { index, item ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                ) {
                    Box(
                        modifier = Modifier
                            .size(width = 22.dp, height = 5.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(
                                if (index == 0) {
                                    Brush.horizontalGradient(listOf(colors.accentStart, colors.accentEnd))
                                } else {
                                    Brush.horizontalGradient(
                                        listOf(colors.frameBorder.copy(alpha = 0.28f), colors.frameBorder.copy(alpha = 0.14f)),
                                    )
                                },
                            ),
                    )
                    Text(
                        text = item,
                        style = AppTypography.bodySmall,
                        color = if (index == 0) colors.title else colors.body,
                    )
                }
            }
        }
    }
}

@Composable
private fun PalettePanel(
    colors: PalettePreviewColors,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        color = Color.Transparent,
        shape = RoundedCornerShape(26.dp),
        border = BorderStroke(1.dp, colors.frameBorder.copy(alpha = 0.58f)),
        shadowElevation = 0.dp,
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.verticalGradient(
                        listOf(
                            colors.shellTop.copy(alpha = 0.96f),
                            colors.shellBottom.copy(alpha = 0.92f),
                        ),
                    ),
                )
                .padding(14.dp),
        ) {
            Column(
                verticalArrangement = Arrangement.spacedBy(0.dp),
                content = content,
            )
        }
    }
}

@Composable
private fun PaletteBadge(
    text: String,
    background: Color,
    foreground: Color,
) {
    Surface(
        color = background,
        shape = RoundedCornerShape(999.dp),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
            style = AppTypography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = foreground,
        )
    }
}

@Composable
private fun PaletteInlineMetric(
    label: String,
    value: String,
    colors: PalettePreviewColors,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            style = AppTypography.bodySmall,
            color = colors.body,
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = AppTypography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
            color = colors.title,
        )
    }
}
