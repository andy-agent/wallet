package com.v2ray.ang.composeui.pages.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.pages.vpn.VpnAccent
import com.v2ray.ang.composeui.pages.vpn.VpnAccentSoft
import com.v2ray.ang.composeui.pages.vpn.VpnBitgetBackground
import com.v2ray.ang.composeui.pages.vpn.VpnCandleChart
import com.v2ray.ang.composeui.pages.vpn.VpnGlassCard
import com.v2ray.ang.composeui.pages.vpn.VpnLabelValueRow
import com.v2ray.ang.composeui.pages.vpn.VpnMetricColumn
import com.v2ray.ang.composeui.pages.vpn.VpnMetricPill
import com.v2ray.ang.composeui.pages.vpn.VpnPageBottomPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageHorizontalPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageTopPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPrimaryButton
import com.v2ray.ang.composeui.pages.vpn.VpnRangeSelector
import com.v2ray.ang.composeui.pages.vpn.VpnSectionHeading
import com.v2ray.ang.composeui.pages.vpn.VpnStatusChip
import com.v2ray.ang.composeui.pages.vpn.VpnSurface
import com.v2ray.ang.composeui.pages.vpn.VpnSurfaceStrong
import com.v2ray.ang.composeui.pages.vpn.VpnTabStrip
import com.v2ray.ang.composeui.pages.vpn.VpnValueBlock
import com.v2ray.ang.composeui.pages.vpn.VpnHeroMetric
import com.v2ray.ang.composeui.pages.vpn.VpnOutline
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary

@Composable
internal fun MarketQuoteDetailPage(
    detail: MarketQuoteDetail = marketSampleQuoteDetail(),
    onNavigateBack: () -> Unit = {},
    onTrade: () -> Unit = {},
) {
    var selectedTopTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedRangeIndex by rememberSaveable { mutableIntStateOf(3) }
    var selectedIndicatorIndex by rememberSaveable { mutableIntStateOf(0) }
    var starred by rememberSaveable { mutableStateOf(true) }

    val currentRange = detail.ranges[selectedRangeIndex]
    val trendColor = remember(detail.changePercent) {
        if (detail.changePercent.trim().startsWith("-")) {
            Color(0xFFFF5D85)
        } else {
            VpnAccent
        }
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                Surface(
                    color = VpnSurface,
                    border = BorderStroke(1.dp, VpnOutline),
                ) {
                    VpnPrimaryButton(
                        text = "交易",
                        onClick = onTrade,
                        modifier = Modifier
                            .fillMaxWidth()
                            .navigationBarsPadding()
                            .padding(horizontal = VpnPageHorizontalPadding, vertical = 16.dp),
                    )
                }
            },
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = VpnPageBottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    MarketQuoteDetailTopBar(
                        detail = detail,
                        starred = starred,
                        onNavigateBack = onNavigateBack,
                        onToggleStar = { starred = !starred },
                    )
                }
                item {
                    VpnTabStrip(
                        tabs = listOf("行情", "详情"),
                        selectedIndex = selectedTopTab,
                        onSelect = { selectedTopTab = it },
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        Column(
                            modifier = Modifier.weight(1.1f),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            VpnValueBlock(
                                value = detail.lastPrice,
                                change = "${detail.changeAmount} ${detail.changePercent}",
                                helper = detail.sessionLabel,
                                changeColor = trendColor,
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                VpnStatusChip(
                                    text = detail.marketLabel,
                                    containerColor = VpnSurfaceStrong,
                                    contentColor = TextSecondary,
                                )
                                VpnStatusChip(
                                    text = "详情",
                                    containerColor = VpnSurfaceStrong,
                                    contentColor = TextSecondary,
                                )
                            }
                        }
                        VpnMetricColumn(
                            metrics = detail.metrics.map { metric ->
                                VpnHeroMetric(metric.label, metric.value)
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        VpnMetricPill(
                            label = "主趋势",
                            value = detail.changePercent,
                            modifier = Modifier.weight(1f),
                            valueColor = trendColor,
                        )
                        VpnMetricPill(
                            label = "K 线视图",
                            value = currentRange.label,
                            modifier = Modifier.weight(1f),
                            valueColor = TextPrimary,
                        )
                    }
                }
                if (selectedTopTab == 0) {
                    item {
                        VpnRangeSelector(
                            labels = detail.ranges.map { it.label },
                            selectedIndex = selectedRangeIndex,
                            trailingIcon = Icons.Default.Tune,
                            onTrailingClick = {},
                            onSelect = { selectedRangeIndex = it },
                        )
                    }
                    item {
                        VpnCandleChart(
                            entries = currentRange.candles,
                            calloutLines = currentRange.calloutLines,
                            rightLabels = currentRange.rightLabels,
                            bottomLabels = currentRange.bottomLabels,
                        )
                    }
                    item {
                        IndicatorStrip(
                            indicators = detail.indicators,
                            selectedIndex = selectedIndicatorIndex,
                            onSelect = { selectedIndicatorIndex = it },
                        )
                    }
                    item {
                        VpnGlassCard {
                            VpnSectionHeading(
                                title = "市场概览",
                                subtitle = "主价格、右侧 KPI、主图表和底部 CTA 均对齐 CRWVon detail 结构。",
                            )
                            detail.overviewFacts.forEach { (label, value) ->
                                VpnLabelValueRow(label = label, value = value)
                            }
                        }
                    }
                } else {
                    item {
                        VpnGlassCard {
                            VpnSectionHeading(
                                title = "标的详情",
                                subtitle = "详情 tab 保留同一顶部骨架，只切换下半区的信息表和说明层。",
                            )
                            detail.detailFacts.forEach { (label, value) ->
                                VpnLabelValueRow(label = label, value = value)
                            }
                        }
                    }
                    item {
                        VpnGlassCard {
                            VpnSectionHeading(
                                title = "页面落地说明",
                                subtitle = "当前页面仅保留行情浏览与详情结构，不接入真实交易和复杂导航。",
                            )
                            detail.thesis.forEach { note ->
                                ThesisRow(text = note)
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketQuoteDetailTopBar(
    detail: MarketQuoteDetail,
    starred: Boolean,
    onNavigateBack: () -> Unit,
    onToggleStar: () -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        DetailIconButton(
            icon = Icons.AutoMirrored.Filled.ArrowBack,
            tint = TextPrimary,
            onClick = onNavigateBack,
        )
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = detail.symbol,
                style = MaterialTheme.typography.titleLarge,
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = detail.companyName,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        DetailIconButton(
            icon = Icons.Default.Star,
            tint = if (starred) VpnAccent else TextTertiary,
            onClick = onToggleStar,
        )
        DetailIconButton(
            icon = Icons.Default.Share,
            tint = TextPrimary,
            onClick = {},
        )
    }
}

@Composable
private fun DetailIconButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    tint: Color,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(36.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = VpnSurfaceStrong,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = tint,
            )
        }
    }
}

@Composable
private fun IndicatorStrip(
    indicators: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        indicators.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onSelect(index) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) VpnAccentSoft else VpnSurfaceStrong,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) VpnAccent else TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun ThesisRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 6.dp)
                .size(6.dp)
                .clip(CircleShape)
                .background(VpnAccent),
        )
        Text(
            text = text,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary,
        )
    }
}

@Preview
@Composable
private fun MarketQuoteDetailPagePreview() {
    CryptoVPNTheme {
        MarketQuoteDetailPage()
    }
}
