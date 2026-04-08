package com.v2ray.ang.composeui.pages.market

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
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Tune
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
import com.v2ray.ang.composeui.pages.vpn.VpnCodeBadge
import com.v2ray.ang.composeui.pages.vpn.VpnEmptyPanel
import com.v2ray.ang.composeui.pages.vpn.VpnGlassCard
import com.v2ray.ang.composeui.pages.vpn.VpnListDivider
import com.v2ray.ang.composeui.pages.vpn.VpnMetricPill
import com.v2ray.ang.composeui.pages.vpn.VpnRangeSelector
import com.v2ray.ang.composeui.pages.vpn.VpnSearchField
import com.v2ray.ang.composeui.pages.vpn.VpnSectionHeading
import com.v2ray.ang.composeui.pages.vpn.VpnStatusChip
import com.v2ray.ang.composeui.pages.vpn.VpnSurfaceStrong
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary

@Composable
internal fun MarketOverviewPage(
    quotes: List<MarketQuote> = marketSampleQuotes,
    spotlights: List<MarketSpotlight> = marketSampleSpotlights,
    onOpenQuote: (MarketQuote) -> Unit = {},
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedCategoryIndex by rememberSaveable { mutableIntStateOf(1) }
    var selectedBoardIndex by rememberSaveable { mutableIntStateOf(0) }

    val selectedCategory = marketOverviewCategories[selectedCategoryIndex]
    val selectedBoard = marketOverviewBoards[selectedBoardIndex]
    val visibleQuotes = remember(query, selectedCategory, selectedBoard, quotes) {
        filterMarketQuotes(
            quotes = quotes,
            query = query,
            category = selectedCategory,
            board = selectedBoard,
        )
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { innerPadding ->
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.Top,
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            VpnSectionHeading(
                                title = "Market",
                                subtitle = "Bitget-style quote browsing with public overview and detail surfaces.",
                                modifier = Modifier.weight(1f),
                            )
                            VpnStatusChip(
                                text = "Public",
                                containerColor = VpnAccentSoft,
                                contentColor = VpnAccent,
                            )
                        }
                        VpnSearchField(
                            value = query,
                            onValueChange = { query = it },
                            placeholder = "搜索标的、主题或夜盘",
                        )
                    }
                }
                item {
                    SpotlightRow(
                        spotlights = spotlights,
                        onOpenQuote = { symbol ->
                            val target = quotes.firstOrNull { it.symbol == symbol } ?: quotes.firstOrNull()
                            if (target != null) {
                                onOpenQuote(target)
                            }
                        },
                    )
                }
                item {
                    MarketChipStrip(
                        labels = marketOverviewCategories.map { it.label },
                        selectedIndex = selectedCategoryIndex,
                        onSelect = { selectedCategoryIndex = it },
                    )
                }
                item {
                    VpnRangeSelector(
                        labels = marketOverviewBoards.map { it.label },
                        selectedIndex = selectedBoardIndex,
                        trailingIcon = Icons.Default.Tune,
                        onTrailingClick = {},
                        onSelect = { selectedBoardIndex = it },
                    )
                }
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        VpnMetricPill(
                            label = "榜单模式",
                            value = selectedBoard.label,
                            modifier = Modifier.weight(1f),
                            valueColor = VpnAccent,
                        )
                        VpnMetricPill(
                            label = "筛选范围",
                            value = selectedCategory.label,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                if (visibleQuotes.isEmpty()) {
                    item {
                        VpnEmptyPanel(
                            title = "没有匹配的行情",
                            subtitle = "调整搜索词或切换分段即可恢复榜单内容。",
                        )
                    }
                } else {
                    item {
                        VpnGlassCard {
                            VpnSectionHeading(
                                title = "热门行情",
                                subtitle = "列表可直接进入 Quote detail，保持 Market -> Quote 的稳定链路。",
                            )
                            visibleQuotes.forEachIndexed { index, quote ->
                                MarketQuoteRow(
                                    quote = quote,
                                    onClick = { onOpenQuote(quote) },
                                )
                                if (index != visibleQuotes.lastIndex) {
                                    VpnListDivider()
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpotlightRow(
    spotlights: List<MarketSpotlight>,
    onOpenQuote: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        spotlights.forEach { spotlight ->
            VpnGlassCard(
                modifier = Modifier.width(246.dp),
                accent = VpnAccent,
            ) {
                Text(
                    text = spotlight.eyebrow,
                    style = MaterialTheme.typography.labelMedium,
                    color = VpnAccent,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = spotlight.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = spotlight.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = spotlight.primaryValue,
                            style = MaterialTheme.typography.headlineSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = spotlight.secondaryValue,
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary,
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(999.dp))
                            .clickable { onOpenQuote(spotlight.symbol) },
                        shape = RoundedCornerShape(999.dp),
                        color = VpnSurfaceStrong,
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                        ) {
                            Text(
                                text = spotlight.symbol,
                                style = MaterialTheme.typography.bodySmall,
                                color = TextPrimary,
                                fontWeight = FontWeight.Medium,
                            )
                            androidx.compose.material3.Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketChipStrip(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier.clickable { onSelect(index) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) VpnAccentSoft else VpnSurfaceStrong,
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) VpnAccent else TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MarketQuoteRow(
    quote: MarketQuote,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(22.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        VpnCodeBadge(text = quote.symbol.take(2))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = quote.symbol,
                    style = MaterialTheme.typography.titleMedium,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = quote.market,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                )
            }
            Text(
                text = quote.name,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                quote.tags.forEach { tag ->
                    MarketTagChip(tag = tag)
                }
            }
            Text(
                text = "区间 ${quote.dayRange}  ·  市值 ${quote.marketCap}  ·  PE ${quote.peRatio}",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = quote.lastPrice,
                style = MaterialTheme.typography.titleMedium,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = if (quote.changeRateValue >= 0f) VpnAccentSoft else Error.copy(alpha = 0.18f),
            ) {
                Text(
                    text = "${quote.changeAmount} ${quote.changePercent}",
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (quote.changeRateValue >= 0f) VpnAccent else Error,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            Text(
                text = "成交额 ${quote.volume24h}",
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
            )
        }
    }
}

@Composable
private fun MarketTagChip(tag: MarketTag) {
    val containerColor = when (tag.tone) {
        MarketTagTone.ACCENT -> VpnAccentSoft
        MarketTagTone.POSITIVE -> Color(0x2010C88C)
        MarketTagTone.NEGATIVE -> Error.copy(alpha = 0.18f)
        MarketTagTone.NEUTRAL -> VpnSurfaceStrong
    }
    val contentColor = when (tag.tone) {
        MarketTagTone.ACCENT -> VpnAccent
        MarketTagTone.POSITIVE -> Color(0xFF10C88C)
        MarketTagTone.NEGATIVE -> Error
        MarketTagTone.NEUTRAL -> TextSecondary
    }
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor,
    ) {
        Text(
            text = tag.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
        )
    }
}

@Preview
@Composable
private fun MarketOverviewPagePreview() {
    CryptoVPNTheme {
        MarketOverviewPage()
    }
}
