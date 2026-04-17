package com.app.feature.settings.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.common.components.SectionHeader
import com.app.common.widgets.MetricPill
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.core.ui.effects.HeaderRingGlyph
import com.app.core.ui.effects.HeaderRingLayer
import com.app.core.ui.effects.HeaderRingPreset
import com.app.core.ui.effects.HeaderTechRing

@Composable
fun HeaderRingLabScreen(
    onBack: () -> Unit = {},
) {
    var preset by remember { mutableStateOf(HeaderRingPreset.R3) }
    var glyph by remember { mutableStateOf(preset.glyph) }
    var enabledLayers by remember { mutableStateOf(preset.layers.map { it.name }.toSet()) }

    val layers = enabledLayers.mapNotNull { name -> HeaderRingLayer.entries.find { it.name == name } }.toSet()

    AppScaffold(title = "Header Ring Lab", onBack = onBack, useProductionMotion = false) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(title = "头部科技圆环实验室", subtitle = "先选圆环，再决定接入统一头部公共组件") {
                    Text(
                        "这里专门预览标题栏右上角的科技圆环。先挑整体风格，再切中心图标，最后按层开关微调。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                }
            }
            item { SectionHeader("预设组合") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HeaderRingPreset.entries.forEach { item ->
                        FilterChip(
                            selected = preset == item,
                            onClick = {
                                preset = item
                                glyph = item.glyph
                                enabledLayers = item.layers.map { layer -> layer.name }.toSet()
                            },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
            item {
                GradientCard(title = preset.label, subtitle = preset.summary) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricPill("轨道周期", "${preset.orbitDurationMs / 1000}s")
                        MetricPill("节点数", preset.nodeCount.toString())
                        MetricPill("图层", layers.size.toString())
                    }
                }
            }
            item { SectionHeader("图标类型") }
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    HeaderRingGlyph.entries.forEach { item ->
                        FilterChip(
                            selected = glyph == item,
                            onClick = { glyph = item },
                            label = { Text(item.title) },
                        )
                    }
                }
            }
            item { SectionHeader("真实头部预览") }
            item {
                GradientCard(title = "Top Bar Preview", subtitle = "看它放进页面头部时是否太抢、太弱或刚好") {
                    HeaderBarMock(
                        title = "市场监控",
                        subtitle = "Header Ring 组件预览",
                        preset = preset,
                        glyph = glyph,
                        enabledLayers = layers,
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    HeaderBarMock(
                        title = "钱包首页",
                        subtitle = "另一种标题长度下的观感",
                        preset = preset,
                        glyph = glyph,
                        enabledLayers = layers,
                    )
                }
            }
            item { SectionHeader("放大预览") }
            item {
                GradientCard(title = "Detail Preview", subtitle = "专门确认轨道、节点、扫描弧和核心玻璃层") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(280.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(CardGlassStrong.copy(alpha = 0.78f)),
                        contentAlignment = Alignment.Center,
                    ) {
                        HeaderTechRing(
                            modifier = Modifier.size(168.dp),
                            preset = preset,
                            enabledLayers = layers,
                            glyph = glyph,
                        )
                    }
                }
            }
            item { SectionHeader("图层开关") }
            items(HeaderRingLayer.entries) { layer ->
                GradientCard(title = layer.title, subtitle = layer.description) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            text = if (layer in layers) "已启用" else "已关闭",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Switch(
                            checked = layer in layers,
                            onCheckedChange = { checked ->
                                enabledLayers = if (checked) {
                                    enabledLayers + layer.name
                                } else {
                                    enabledLayers - layer.name
                                }
                            },
                        )
                    }
                }
            }
            item {
                GradientCard(title = "推荐起点", subtitle = "如果你要我先默认接到正式头部") {
                    Text(
                        "推荐先从 R3 双轨能量环 或 R4 扫描雷达 开始。R3 更通用，R4 更适合 VPN / 市场页。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(
                        text = "一键切到推荐 R3",
                        onClick = {
                            preset = HeaderRingPreset.R3
                            glyph = HeaderRingPreset.R3.glyph
                            enabledLayers = HeaderRingPreset.R3.layers.map { it.name }.toSet()
                        },
                    )
                }
            }
        }
    }
}

@Composable
private fun HeaderBarMock(
    title: String,
    subtitle: String,
    preset: HeaderRingPreset,
    glyph: HeaderRingGlyph,
    enabledLayers: Set<HeaderRingLayer>,
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
            Text(title, style = MaterialTheme.typography.titleMedium)
            Text(subtitle, style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
        }
        Spacer(modifier = Modifier.size(12.dp))
        HeaderTechRing(
            modifier = Modifier.size(74.dp),
            preset = preset,
            enabledLayers = enabledLayers,
            glyph = glyph,
        )
    }
}
