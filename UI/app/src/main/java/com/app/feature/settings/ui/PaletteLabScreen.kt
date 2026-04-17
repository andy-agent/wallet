package com.app.feature.settings.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.app.common.components.GlassOutlinePanel
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.common.components.SectionHeader
import com.app.common.components.StatusChip
import com.app.common.widgets.TokenIcon
import com.app.core.ui.AppScaffold
import com.app.core.ui.effects.CompactAnimatedBottomBar

@Composable
fun PaletteLabScreen(
    onBack: () -> Unit = {},
    onOpenEffectLab: () -> Unit = {},
) {
    var selected by rememberSaveable { mutableStateOf(PalettePreviewPalettes.first()) }
    var currentTab by rememberSaveable { mutableStateOf("wallet_home") }

    AppScaffold(
        title = "配色实验室",
        onBack = onBack,
        useProductionMotion = false,
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(title = "颜色方向预览", subtitle = "基于你给的截图，先比较层级与模块分组，再决定是否全局替换") {
                    Text(
                        "当前问题不是“少几个颜色”，而是头部、主卡、提醒卡、图表、底栏的情绪太接近。这里固定用同一套内容来比较 A1 / A2 / A3。",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(text = "返回动效实验室", onClick = onOpenEffectLab)
                }
            }
            item { SectionHeader("候选方案") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    PalettePreviewPalettes.forEach { palette ->
                        FilterChip(
                            selected = palette.id == selected.id,
                            onClick = { selected = palette },
                            label = { Text(palette.title) },
                        )
                    }
                }
            }
            item {
                GradientCard(title = selected.title, subtitle = selected.summary) {
                    PaletteMetaRow(label = "最适合", value = selected.bestFor)
                    PaletteMetaRow(label = "模块强调", value = selected.moduleAccent)
                    PaletteMetaRow(label = "上线建议", value = selected.rolloutNote)
                }
            }
            item { SectionHeader("真机预览") }
            item {
                PalettePreviewFrame(
                    palette = selected,
                    currentTab = currentTab,
                    onRouteSelected = { currentTab = it },
                )
            }
        }
    }
}

@Composable
private fun PaletteMetaRow(
    label: String,
    value: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.primary,
        )
        Text(
            text = value,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
    }
}

@Composable
private fun PalettePreviewFrame(
    palette: PalettePreviewPalette,
    currentTab: String,
    onRouteSelected: (String) -> Unit,
) {
    val colors = palette.colors
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(660.dp)
            .clip(RoundedCornerShape(32.dp))
            .background(
                Brush.verticalGradient(
                    listOf(colors.canvasTop, colors.canvasBottom),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(colors.glowPrimary, Color.Transparent),
                        radius = 620f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.radialGradient(
                        listOf(colors.glowSecondary, Color.Transparent),
                        radius = 760f,
                    ),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            PaletteHeaderCard(colors)
            PalettePrimaryCard(colors)
            PaletteReminderCard(colors)
            PaletteChartCard(colors)
            Spacer(modifier = Modifier.weight(1f))
            CompactAnimatedBottomBar(
                currentRoute = currentTab,
                onRouteSelected = onRouteSelected,
                animated = true,
            )
        }
    }
}

@Composable
private fun PaletteHeaderCard(colors: PalettePreviewColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(colors.accentStart, colors.accentEnd),
                ),
            )
            .padding(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "网络与资产总览",
                    style = MaterialTheme.typography.headlineSmall,
                    color = colors.accentText,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "确认顶部锚点是否足够稳，不再和下面卡片混成一层。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = colors.accentText.copy(alpha = 0.84f),
                )
            }
            StatusChip(text = "预览中", positive = null)
        }
    }
}

@Composable
private fun PalettePrimaryCard(colors: PalettePreviewColors) {
    GlassOutlinePanel(
        modifier = Modifier.fillMaxWidth(),
        radius = 28.dp,
        contentPadding = PaddingValues(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            TokenIcon(symbol = "ETH", chainId = "ethereum", size = 56.dp)
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("主卡区域", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                Text("主信息区要稳、亮，但和背景、提醒卡明显分层。", style = MaterialTheme.typography.bodyMedium)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("$12,480", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = colors.title)
                Text("+12.4%", style = MaterialTheme.typography.labelLarge, color = colors.positive, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
private fun PaletteReminderCard(colors: PalettePreviewColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.horizontalGradient(
                    listOf(colors.accentSoft, colors.shellBottom),
                ),
            )
            .padding(16.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text("提醒卡", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, color = colors.title)
                Text("这里要和主卡明显区分，不能再“白上加白”。", style = MaterialTheme.typography.bodyMedium, color = colors.body)
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text("剩余 28 天", style = MaterialTheme.typography.labelLarge, color = colors.warning, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun PaletteChartCard(colors: PalettePreviewColors) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(
                Brush.verticalGradient(
                    listOf(colors.shellTop, colors.shellBottom),
                ),
            )
            .padding(16.dp),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text("图表面板", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold, color = colors.title)
                Text("+6.8%", style = MaterialTheme.typography.labelLarge, color = colors.positive, fontWeight = FontWeight.SemiBold)
            }
            Canvas(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp),
            ) {
                repeat(4) { index ->
                    val y = (size.height / 3f) * index
                    drawLine(
                        color = colors.frameBorder.copy(alpha = 0.38f),
                        start = androidx.compose.ui.geometry.Offset(0f, y),
                        end = androidx.compose.ui.geometry.Offset(size.width, y),
                        strokeWidth = 1f,
                    )
                }
                val points = listOf(0.18f, 0.24f, 0.23f, 0.41f, 0.37f, 0.56f, 0.61f, 0.72f)
                val gap = size.width / (points.size - 1)
                val strokePath = Path()
                val fillPath = Path()
                points.forEachIndexed { index, value ->
                    val x = gap * index
                    val y = size.height - (size.height * value)
                    if (index == 0) {
                        strokePath.moveTo(x, y)
                        fillPath.moveTo(x, size.height)
                        fillPath.lineTo(x, y)
                    } else {
                        strokePath.lineTo(x, y)
                        fillPath.lineTo(x, y)
                    }
                }
                fillPath.lineTo(size.width, size.height)
                fillPath.close()
                drawPath(
                    path = fillPath,
                    brush = Brush.verticalGradient(listOf(colors.chartFill, Color.Transparent)),
                )
                drawPath(
                    path = strokePath,
                    color = colors.chartLine,
                    style = Stroke(width = 5f),
                )
            }
        }
    }
}
