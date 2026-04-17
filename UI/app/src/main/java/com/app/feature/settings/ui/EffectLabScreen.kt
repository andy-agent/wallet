package com.app.feature.settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.PrimaryButton
import com.app.common.components.SearchBar
import com.app.common.components.SectionHeader
import com.app.common.components.StatusChip
import com.app.common.widgets.MetricPill
import com.app.core.theme.BluePrimary
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.core.ui.effects.CompactAnimatedBottomBar
import com.app.core.ui.effects.EffectLabPreset
import com.app.core.ui.effects.EffectToggle
import com.app.core.ui.effects.TechMotionBackground
import kotlinx.coroutines.delay

@Composable
fun EffectLabScreen(
    onBack: () -> Unit = {},
) {
    var preset by remember { mutableStateOf(EffectLabPreset.P2) }
    var enabledNames by remember { mutableStateOf(preset.enabled.map { it.name }.toSet()) }
    var query by remember { mutableStateOf("") }
    var currentTab by remember { mutableStateOf("wallet_home") }
    var previewArmed by remember { mutableStateOf(false) }

    LaunchedEffect(preset) {
        enabledNames = preset.enabled.map { it.name }.toSet()
        previewArmed = false
        delay(120)
        previewArmed = true
    }

    val enabled = enabledNames.mapNotNull { name -> EffectToggle.entries.find { it.name == name } }.toSet()

    AppScaffold(title = "Effect Lab", onBack = onBack) { padding ->
        androidx.compose.foundation.lazy.LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                GradientCard(title = "动效实验室", subtitle = "先在真机确认，再决定接到正式页面") {
                    Text(
                        "这里会预览你关心的粒子流、科技感、能量球、图表和紧凑底栏点击反馈。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                }
            }
            item {
                SearchBar(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = "筛选效果，例如 粒子 / 底栏 / 图表",
                )
            }
            item { SectionHeader("预设方案") }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    EffectLabPreset.entries.forEach { item ->
                        FilterChip(
                            selected = preset == item,
                            onClick = { preset = item },
                            label = { Text(item.label) },
                        )
                    }
                }
            }
            item {
                GradientCard(title = preset.label, subtitle = preset.summary) {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricPill("粒子数", preset.particleCount.toString())
                        MetricPill("周期", "${preset.orbitDurationMs / 1000}s")
                    }
                }
            }
            item { SectionHeader("实时预览") }
            item {
                GradientCard(title = "Preview Canvas", subtitle = "切预设或切开关，这里会立即响应") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(460.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(Color.White.copy(alpha = 0.22f)),
                    ) {
                        TechMotionBackground(
                            particleCount = preset.particleCount,
                            orbitDurationMs = preset.orbitDurationMs,
                            showParticles = EffectToggle.ParticleDrift in enabled,
                            showNetwork = EffectToggle.ParticleLinks in enabled,
                            showGridScan = EffectToggle.GridScan in enabled,
                            showOrb = EffectToggle.EnergyOrb in enabled,
                        )
                        androidx.compose.animation.AnimatedVisibility(
                            visible = previewArmed,
                            enter = fadeIn(animationSpec = tween(420)) + slideInVertically(
                                animationSpec = tween(420),
                                initialOffsetY = { it / 6 },
                            ),
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(18.dp),
                                verticalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                    StatusChip("预览态")
                                    StatusChip(if (EffectToggle.GridScan in enabled) "Grid On" else "Grid Off")
                                }
                                GradientCard(title = "CryptoVPN", subtitle = "Effect Lab Hero") {
                                    Text("这里用来帮你确认最终是否保留粒子、能量球、图表动效和紧凑底栏。")
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                        MetricPill("Preset", preset.label.removePrefix("P"))
                                        MetricPill("Effects", enabled.size.toString())
                                    }
                                }
                                PreviewActionRow(pulsing = EffectToggle.ButtonPulse in enabled)
                                PreviewChartCard(animated = EffectToggle.ChartDraw in enabled)
                                CompactAnimatedBottomBar(
                                    currentRoute = currentTab,
                                    onRouteSelected = { currentTab = it },
                                    animated = EffectToggle.BottomBarMotion in enabled,
                                )
                            }
                        }
                    }
                }
            }
            item { SectionHeader("单项开关") }
            items(
                EffectToggle.entries.filter { toggle ->
                    query.isBlank() || toggle.title.contains(query, ignoreCase = true) || toggle.description.contains(query, ignoreCase = true)
                },
            ) { toggle ->
                GradientCard(title = toggle.title, subtitle = toggle.description) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Text(
                            if (toggle in enabled) "已启用" else "已关闭",
                            style = MaterialTheme.typography.bodyLarge,
                        )
                        Switch(
                            checked = toggle in enabled,
                            onCheckedChange = { checked ->
                                enabledNames = if (checked) enabledNames + toggle.name else enabledNames - toggle.name
                            },
                        )
                    }
                }
            }
            item {
                GradientCard(title = "推荐起点", subtitle = "如果你不想一个个挑") {
                    Text(
                        "先看 P2。它会同时启用粒子漂浮、粒子连线、右上角能量球、底栏动效和图表绘制，最适合做主方案基线。",
                        style = MaterialTheme.typography.bodyLarge,
                        color = TextSecondary,
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    PrimaryButton(text = "切到推荐预设 P2", onClick = { preset = EffectLabPreset.P2 })
                }
            }
        }
    }
}

@Composable
private fun PreviewActionRow(
    pulsing: Boolean,
) {
    val transition = rememberInfiniteTransition(label = "button-pulse")
    val pulse = transition.animateFloat(
        initialValue = 0.96f,
        targetValue = 1.06f,
        animationSpec = infiniteRepeatable(
            animation = tween(1600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "pulse-value",
    )
    Row(modifier = Modifier.fillMaxWidth()) {
        PrimaryButton(
            text = "按钮预览",
            onClick = {},
            modifier = Modifier.scale(if (pulsing) pulse.value else 1f),
        )
    }
}

@Composable
private fun PreviewChartCard(
    animated: Boolean,
) {
    val reveal by animateFloatAsState(
        targetValue = if (animated) 1f else 0.35f,
        animationSpec = tween(1600, easing = LinearEasing),
        label = "chart-reveal",
    )
    val points = listOf(0.18f, 0.26f, 0.24f, 0.32f, 0.28f, 0.35f, 0.42f, 0.48f)
    GradientCard(title = "图表动画", subtitle = if (animated) "Chart Draw 已启用" else "静态线图") {
        Canvas(modifier = Modifier.fillMaxWidth().height(120.dp)) {
            val max = points.max()
            val min = points.min()
            val range = (max - min).takeIf { it != 0f } ?: 1f
            val gap = size.width / (points.size - 1)
            val progressWidth = size.width * reveal
            for (index in 1 until points.size) {
                val startX = gap * (index - 1)
                val endX = gap * index
                if (startX > progressWidth) break
                val startY = size.height - ((points[index - 1] - min) / range) * size.height
                val endY = size.height - ((points[index] - min) / range) * size.height
                drawLine(
                    color = BluePrimary,
                    start = Offset(startX, startY),
                    end = Offset(endX.coerceAtMost(progressWidth), endY),
                    strokeWidth = 6f,
                )
            }
            points.forEachIndexed { index, point ->
                val x = gap * index
                if (x > progressWidth) return@forEachIndexed
                val y = size.height - ((point - min) / range) * size.height
                drawCircle(
                    color = BluePrimary,
                    radius = 5f,
                    center = Offset(x, y),
                )
            }
        }
    }
}
