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
import androidx.compose.foundation.layout.heightIn
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.pages.vpn.VpnAccent
import com.v2ray.ang.composeui.pages.vpn.VpnBitgetBackground
import com.v2ray.ang.composeui.pages.vpn.VpnCandleChart
import com.v2ray.ang.composeui.pages.vpn.VpnEmptyPanel
import com.v2ray.ang.composeui.pages.vpn.VpnGlassCard
import com.v2ray.ang.composeui.pages.vpn.VpnHeroMetric
import com.v2ray.ang.composeui.pages.vpn.VpnLabelValueRow
import com.v2ray.ang.composeui.pages.vpn.VpnLoadingPanel
import com.v2ray.ang.composeui.pages.vpn.VpnMetricColumn
import com.v2ray.ang.composeui.pages.vpn.VpnOutline
import com.v2ray.ang.composeui.pages.vpn.VpnPageBottomPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageHorizontalPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageTopPadding
import com.v2ray.ang.composeui.pages.vpn.VpnRangeSelector
import com.v2ray.ang.composeui.pages.vpn.VpnSectionHeading
import com.v2ray.ang.composeui.pages.vpn.VpnValueBlock
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.Success
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val MarketDetailPaperSurface = Color(0xFFFFFCF8)
private val MarketDetailSubtleSurface = Color(0xFFF9F4ED)
private val MarketDetailOutlineSoft = VpnOutline.copy(alpha = 0.58f)
private val MarketDetailSelectionFill = VpnAccent.copy(alpha = 0.08f)
private val MarketDetailSelectionBorder = VpnAccent.copy(alpha = 0.14f)
private val MarketDetailAccentText = VpnAccent.copy(alpha = 0.84f)
private val MarketDetailPositive = Success
private val MarketDetailButtonFill = Color(0xFFDDE8E2)

internal sealed interface MarketQuoteDetailUiState {
    data object Loading : MarketQuoteDetailUiState

    data class Loaded(val detail: MarketQuoteDetail) : MarketQuoteDetailUiState

    data class Error(val message: String) : MarketQuoteDetailUiState
}

internal class MarketQuoteDetailViewModel : ViewModel() {
    private val repository = MarketRemoteRepository()
    private val _state = MutableStateFlow<MarketQuoteDetailUiState>(MarketQuoteDetailUiState.Loading)
    private var currentSymbol: String? = null

    val state: StateFlow<MarketQuoteDetailUiState> = _state

    fun load(symbol: String, force: Boolean = false) {
        if (!force && symbol == currentSymbol && _state.value is MarketQuoteDetailUiState.Loaded) {
            return
        }
        currentSymbol = symbol
        viewModelScope.launch {
            _state.value = MarketQuoteDetailUiState.Loading
            val instrumentId = repository.resolveInstrumentId(symbol).getOrElse {
                _state.value = MarketQuoteDetailUiState.Error(it.message ?: "解析行情标的失败")
                return@launch
            }
            val detailPayload = repository.getInstrumentDetail(instrumentId).getOrElse {
                _state.value = MarketQuoteDetailUiState.Error(it.message ?: "加载行情详情失败")
                return@launch
            }
            val ranges = detailPayload.supportedTimeframes.mapNotNull { option ->
                repository.getCandles(
                    instrumentId = instrumentId,
                    timeframe = option.key,
                ).getOrNull()?.toMarketTimeframe(
                    label = option.label,
                    precision = detailPayload.instrument.displayPrecision,
                )
            }
            _state.value = MarketQuoteDetailUiState.Loaded(
                detail = detailPayload.toMarketQuoteDetail(ranges),
            )
        }
    }

    fun refresh() {
        currentSymbol?.let { load(it, force = true) }
    }
}

@Composable
internal fun MarketQuoteDetailPage(
    instrumentId: String,
    viewModel: MarketQuoteDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onTrade: () -> Unit = {},
) {
    LaunchedEffect(instrumentId) {
        viewModel.load(instrumentId)
    }
    val state by viewModel.state.collectAsState()
    when (val currentState = state) {
        MarketQuoteDetailUiState.Loading -> {
            VpnBitgetBackground {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = VpnPageHorizontalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    VpnLoadingPanel(
                        title = "加载详情中",
                        subtitle = "正在获取实时市场详情和 K 线。",
                    )
                }
            }
        }

        is MarketQuoteDetailUiState.Error -> {
            VpnBitgetBackground {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = VpnPageHorizontalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    VpnEmptyPanel(
                        title = "详情加载失败",
                        subtitle = currentState.message,
                        actionText = "重新加载",
                        onAction = viewModel::refresh,
                    )
                }
            }
        }

        is MarketQuoteDetailUiState.Loaded -> MarketQuoteDetailContent(
            detail = currentState.detail,
            onNavigateBack = onNavigateBack,
            onTrade = onTrade,
        )
    }
}

@Composable
private fun MarketQuoteDetailContent(
    detail: MarketQuoteDetail,
    onNavigateBack: () -> Unit,
    onTrade: () -> Unit,
) {
    var selectedTopTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedRangeIndex by rememberSaveable { mutableIntStateOf(0) }
    var selectedIndicatorIndex by rememberSaveable { mutableIntStateOf(0) }
    var starred by rememberSaveable { mutableStateOf(true) }

    val currentRange = detail.ranges.getOrNull(selectedRangeIndex.coerceIn(0, detail.ranges.lastIndex.coerceAtLeast(0)))
    val trendColor = remember(detail.changePercent) {
        if (detail.changePercent.trim().startsWith("-")) {
            Error
        } else {
            MarketDetailPositive
        }
    }

    VpnBitgetBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = TextPrimary,
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                Surface(
                    color = MarketDetailPaperSurface,
                    border = BorderStroke(1.dp, MarketDetailOutlineSoft),
                ) {
                    MarketTradeButton(
                        text = detail.tradeActionLabel,
                        onClick = onTrade,
                        enabled = detail.tradeActionEnabled,
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
                    MarketDetailTabs(
                        tabs = listOf("行情", "详情"),
                        selectedIndex = selectedTopTab,
                        onSelect = { selectedTopTab = it },
                    )
                }
                item {
                    Surface(
                        shape = RoundedCornerShape(28.dp),
                        color = MarketDetailPaperSurface,
                        border = BorderStroke(1.dp, MarketDetailOutlineSoft),
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 18.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp),
                        ) {
                            Column(
                                modifier = Modifier.weight(1.1f),
                                verticalArrangement = Arrangement.spacedBy(12.dp),
                            ) {
                                VpnValueBlock(
                                    value = detail.lastPrice,
                                    change = "${detail.changeAmount} ${detail.changePercent}",
                                    helper = null,
                                    changeColor = trendColor,
                                )
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                                ) {
                                    if (detail.sessionLabel.isNotBlank()) {
                                        MarketDetailChip(
                                            text = detail.sessionLabel,
                                            containerColor = MarketDetailSelectionFill,
                                            contentColor = MarketDetailAccentText,
                                        )
                                    }
                                    MarketDetailChip(
                                        text = detail.marketLabel,
                                        containerColor = MarketDetailSubtleSurface,
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
                }
                if (selectedTopTab == 0) {
                    if (detail.ranges.isEmpty()) {
                        item {
                            VpnEmptyPanel(
                                title = "暂无 K 线数据",
                                subtitle = "当前标的未返回可展示的实时 K 线。",
                            )
                        }
                    } else {
                        item {
                            VpnRangeSelector(
                                labels = detail.ranges.map { it.label },
                                selectedIndex = selectedRangeIndex.coerceIn(0, detail.ranges.lastIndex),
                                trailingIcon = Icons.Default.Tune,
                                onTrailingClick = {},
                                onSelect = { selectedRangeIndex = it },
                            )
                        }
                        item {
                            currentRange?.let { range ->
                                VpnCandleChart(
                                    entries = range.candles,
                                    calloutLines = range.calloutLines,
                                    rightLabels = range.rightLabels,
                                    bottomLabels = range.bottomLabels,
                                )
                            }
                        }
                        if (detail.indicators.isNotEmpty()) {
                            item {
                                IndicatorStrip(
                                    indicators = detail.indicators,
                                    selectedIndex = selectedIndicatorIndex.coerceIn(0, detail.indicators.lastIndex),
                                    onSelect = { selectedIndicatorIndex = it },
                                )
                            }
                        }
                    }
                    item {
                        VpnGlassCard {
                            VpnSectionHeading(
                                title = "关键数据",
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
                                title = "详情",
                            )
                            detail.detailFacts.forEach { (label, value) ->
                                VpnLabelValueRow(label = label, value = value)
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
            tint = if (starred) MarketDetailAccentText else TextTertiary,
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
            .size(38.dp)
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = MarketDetailPaperSurface,
        border = BorderStroke(1.dp, MarketDetailOutlineSoft),
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
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        indicators.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onSelect(index) },
                shape = RoundedCornerShape(999.dp),
                color = if (selected) MarketDetailSelectionFill else MarketDetailPaperSurface,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) MarketDetailSelectionBorder else MarketDetailOutlineSoft,
                ),
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    style = MaterialTheme.typography.bodySmall,
                    color = if (selected) TextPrimary else TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MarketDetailTabs(
    tabs: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        tabs.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier.clickable { onSelect(index) },
                shape = RoundedCornerShape(18.dp),
                color = if (selected) MarketDetailSelectionFill else Color.Transparent,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) MarketDetailSelectionBorder else Color.Transparent,
                ),
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (selected) TextPrimary else TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MarketDetailChip(
    text: String,
    containerColor: Color,
    contentColor: Color,
) {
    Surface(
        shape = RoundedCornerShape(999.dp),
        color = containerColor,
        border = BorderStroke(1.dp, contentColor.copy(alpha = 0.1f)),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 11.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun MarketTradeButton(
    text: String,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        modifier = modifier.heightIn(min = 54.dp),
        enabled = enabled,
        shape = RoundedCornerShape(26.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MarketDetailButtonFill,
            contentColor = TextPrimary,
            disabledContainerColor = MarketDetailSubtleSurface,
            disabledContentColor = TextTertiary,
        ),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Preview
@Composable
private fun MarketQuoteDetailPagePreview() {
    CryptoVPNTheme {
        MarketQuoteDetailContent(
            detail = MarketQuoteDetail(
                symbol = "SOL",
                companyName = "Solana",
                marketLabel = "合约",
                shareUrl = null,
                lastPrice = "\$84.47",
                changeAmount = "+\$5.23",
                changePercent = "+6.59%",
                sessionLabel = "24x7",
                metrics = listOf(
                    MarketDetailMetric("24H 最高", "\$86.37"),
                    MarketDetailMetric("24H 最低", "\$78.48"),
                    MarketDetailMetric("成交额", "\$5.06B"),
                    MarketDetailMetric("总市值", "\$48.38B"),
                    MarketDetailMetric("标签", "热门 / 公链"),
                ),
                ranges = listOf(
                    MarketTimeframe(
                        label = "1小时",
                        candles = listOf(
                            com.v2ray.ang.composeui.pages.vpn.VpnChartCandle(79.19f, 79.15f, 79.29f, 78.89f),
                            com.v2ray.ang.composeui.pages.vpn.VpnChartCandle(79.09f, 78.95f, 79.15f, 78.92f),
                            com.v2ray.ang.composeui.pages.vpn.VpnChartCandle(78.98f, 79.36f, 79.36f, 78.84f),
                        ),
                        rightLabels = listOf("79.36", "79.20", "79.04", "78.89"),
                        bottomLabels = listOf("12:00", "16:00", "20:00", "00:00"),
                        calloutLines = listOf(
                            "时间" to "2026-04-08 00:00",
                            "开盘" to "\$78.98",
                            "最高" to "\$79.36",
                            "最低" to "\$78.84",
                            "收盘" to "\$79.36",
                            "涨跌额" to "+\$0.38",
                            "涨跌幅" to "+0.48%",
                        ),
                    ),
                ),
                indicators = listOf("MA", "BOLL", "MACD"),
                overviewFacts = listOf(
                    "24H 区间" to "\$78.48 - \$86.37",
                    "24H 成交额" to "\$5.06B",
                ),
                detailFacts = listOf(
                    "市场" to "CRYPTO",
                    "标的名称" to "Solana",
                ),
                tradeActionEnabled = true,
                tradeActionLabel = "查看市场",
            ),
            onNavigateBack = {},
            onTrade = {},
        )
    }
}
