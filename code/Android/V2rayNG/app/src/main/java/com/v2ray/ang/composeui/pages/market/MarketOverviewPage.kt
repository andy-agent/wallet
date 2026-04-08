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
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.v2ray.ang.composeui.pages.vpn.VpnEmptyPanel
import com.v2ray.ang.composeui.pages.vpn.VpnLoadingPanel
import com.v2ray.ang.composeui.pages.vpn.VpnOutline
import com.v2ray.ang.composeui.pages.vpn.VpnPageBottomPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageHorizontalPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageTopPadding
import com.v2ray.ang.composeui.pages.vpn.VpnSearchField
import com.v2ray.ang.composeui.pages.vpn.VpnSurface
import com.v2ray.ang.composeui.pages.vpn.VpnSurfaceStrong
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.TextPrimary
import com.v2ray.ang.composeui.theme.TextSecondary
import com.v2ray.ang.composeui.theme.TextTertiary
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

internal sealed interface MarketOverviewUiState {
    data object Loading : MarketOverviewUiState

    data class Loaded(
        val quotes: List<MarketQuote>,
        val spotlights: List<MarketSpotlight>,
    ) : MarketOverviewUiState

    data class Error(val message: String) : MarketOverviewUiState
}

internal class MarketOverviewViewModel : ViewModel() {
    private val repository = MarketRemoteRepository()
    private val _state = MutableStateFlow<MarketOverviewUiState>(MarketOverviewUiState.Loading)
    val state: StateFlow<MarketOverviewUiState> = _state

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = MarketOverviewUiState.Loading
            val overview = repository.getOverview()
            if (overview.isFailure) {
                _state.value = MarketOverviewUiState.Error(
                    overview.exceptionOrNull()?.message ?: "加载行情概览失败",
                )
                return@launch
            }
            val spotlights = repository.getSpotlights().getOrNull()?.toMarketSpotlights().orEmpty()
            _state.value = MarketOverviewUiState.Loaded(
                quotes = overview.getOrThrow().toMarketQuotes(),
                spotlights = spotlights,
            )
        }
    }
}

@Composable
internal fun MarketOverviewPage(
    viewModel: MarketOverviewViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onOpenQuote: (MarketQuote) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    when (val currentState = state) {
        MarketOverviewUiState.Loading -> {
            VpnBitgetBackground {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = VpnPageHorizontalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    VpnLoadingPanel(
                        title = "加载行情中",
                        subtitle = "正在获取实时市场概览。",
                    )
                }
            }
        }

        is MarketOverviewUiState.Error -> {
            VpnBitgetBackground {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = VpnPageHorizontalPadding),
                    contentAlignment = Alignment.Center,
                ) {
                    VpnEmptyPanel(
                        title = "行情加载失败",
                        subtitle = currentState.message,
                        actionText = "重新加载",
                        onAction = viewModel::refresh,
                    )
                }
            }
        }

        is MarketOverviewUiState.Loaded -> MarketOverviewContent(
            quotes = currentState.quotes,
            spotlights = currentState.spotlights,
            onOpenQuote = onOpenQuote,
        )
    }
}

@Composable
private fun MarketOverviewContent(
    quotes: List<MarketQuote>,
    spotlights: List<MarketSpotlight>,
    onOpenQuote: (MarketQuote) -> Unit,
) {
    var query by rememberSaveable { mutableStateOf("") }
    var selectedPrimaryIndex by rememberSaveable { mutableIntStateOf(1) }
    var selectedBoardIndex by rememberSaveable { mutableIntStateOf(0) }

    val selectedPrimary = marketPrimarySections[selectedPrimaryIndex]
    val boardOptions = remember(selectedPrimary) { marketBoardsFor(selectedPrimary) }
    if (selectedBoardIndex > boardOptions.lastIndex) {
        selectedBoardIndex = 0
    }
    val selectedBoard = boardOptions[selectedBoardIndex]
    val visibleQuotes = remember(query, selectedPrimary, selectedBoard, quotes) {
        filterMarketQuotes(
            quotes = quotes,
            query = query,
            primarySection = selectedPrimary,
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
                contentPadding = PaddingValues(
                    start = VpnPageHorizontalPadding,
                    end = VpnPageHorizontalPadding,
                    top = VpnPageTopPadding,
                    bottom = VpnPageBottomPadding,
                ),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                item {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        VpnSearchField(
                            value = query,
                            onValueChange = { query = it },
                            modifier = Modifier.height(52.dp),
                            placeholder = "搜索代币/合约/股票/DApp",
                            trailingIcon = Icons.Default.Tune,
                            onTrailingClick = {},
                        )
                    }
                }
                item {
                    MarketTopTabs(
                        labels = marketPrimarySections.map { it.label },
                        selectedIndex = selectedPrimaryIndex,
                        onSelect = { selectedPrimaryIndex = it },
                    )
                }
                item {
                    MarketBoardTabs(
                        labels = boardOptions.map { it.label },
                        selectedIndex = selectedBoardIndex,
                        onSelect = { selectedBoardIndex = it },
                    )
                }
                if (visibleQuotes.isEmpty()) {
                    item {
                        VpnEmptyPanel(
                            title = "未找到相关行情",
                            subtitle = "尝试更换搜索词或切换榜单。",
                        )
                    }
                } else {
                    item {
                        MarketQuoteBoard(
                            quotes = visibleQuotes,
                            board = selectedBoard,
                            onOpenQuote = onOpenQuote,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketSpotlightStrip(
    spotlights: List<MarketSpotlight>,
    onOpenQuote: (String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        spotlights.forEach { spotlight ->
            val metricColor = when {
                spotlight.primaryValue.trim().startsWith("-") -> Error
                spotlight.primaryValue.trim().startsWith("+") -> VpnAccent
                else -> TextPrimary
            }
            Surface(
                modifier = Modifier
                    .width(164.dp)
                    .clickable { onOpenQuote(spotlight.symbol) },
                shape = RoundedCornerShape(18.dp),
                color = VpnSurface,
                border = BorderStroke(1.dp, VpnOutline.copy(alpha = 0.78f)),
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 11.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(3.dp),
                    ) {
                        Text(
                            text = spotlight.eyebrow,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextTertiary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = spotlight.title,
                            style = MaterialTheme.typography.titleSmall,
                            color = TextPrimary,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = spotlight.primaryValue,
                            style = MaterialTheme.typography.bodyLarge,
                            color = metricColor,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                        )
                    }
                    Column(
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                    ) {
                        Text(
                            text = spotlight.secondaryValue,
                            style = MaterialTheme.typography.labelSmall,
                            color = TextSecondary,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextTertiary,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketTopTabs(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .clickable { onSelect(index) },
                verticalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodyLarge,
                    color = if (selected) TextPrimary else TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                )
                Box(
                    modifier = Modifier
                        .width(18.dp)
                        .height(2.dp)
                        .background(
                            color = if (selected) VpnAccent else Color.Transparent,
                            shape = RoundedCornerShape(999.dp),
                        ),
                )
            }
        }
    }
}

@Composable
private fun MarketBoardTabs(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier.clickable { onSelect(index) },
                shape = RoundedCornerShape(14.dp),
                color = if (selected) VpnSurfaceStrong else Color.Transparent,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) VpnAccent.copy(alpha = 0.34f) else VpnOutline.copy(alpha = 0.64f),
                ),
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = if (selected) TextPrimary else TextSecondary,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MarketQuoteBoard(
    quotes: List<MarketQuote>,
    board: MarketBoard,
    onOpenQuote: (MarketQuote) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = VpnSurface,
        border = BorderStroke(1.dp, VpnOutline.copy(alpha = 0.82f)),
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                Text(
                    text = "名称",
                    modifier = Modifier.weight(1f),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary,
                )
                Text(
                    text = "最新价",
                    modifier = Modifier.width(88.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary,
                )
                Text(
                    text = board.columnLabel,
                    modifier = Modifier.width(96.dp),
                    style = MaterialTheme.typography.labelMedium,
                    color = TextTertiary,
                )
            }
            MarketBoardDivider()
            quotes.forEachIndexed { index, quote ->
                MarketQuoteRow(
                    quote = quote,
                    board = board,
                    onClick = { onOpenQuote(quote) },
                )
                if (index != quotes.lastIndex) {
                    MarketBoardDivider()
                }
            }
        }
    }
}

@Composable
private fun MarketQuoteRow(
    quote: MarketQuote,
    board: MarketBoard,
    onClick: () -> Unit,
) {
    val trendColor = if (quote.changeRateValue >= 0f) VpnAccent else Error
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 13.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp),
            ) {
                Text(
                    text = quote.symbol,
                    style = MaterialTheme.typography.titleSmall,
                    color = TextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = quote.market,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                )
                if (quote.isFavorite) {
                    MarketInlineMarker(tag = MarketTag(label = "收藏", tone = MarketTagTone.ACCENT))
                }
            }
            Text(
                text = quote.name,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Column(
            modifier = Modifier.width(88.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = quote.lastPrice,
                style = MaterialTheme.typography.titleSmall,
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = quote.changeAmount,
                style = MaterialTheme.typography.labelSmall,
                color = trendColor,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier.width(96.dp),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = board.primaryMetric(quote),
                style = MaterialTheme.typography.titleSmall,
                color = board.primaryMetricColor(quote),
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = board.secondaryMetric(quote),
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MarketInlineMarker(tag: MarketTag) {
    val containerColor = when (tag.tone) {
        MarketTagTone.ACCENT -> VpnAccent.copy(alpha = 0.16f)
        MarketTagTone.POSITIVE -> Color(0x2010C88C)
        MarketTagTone.NEGATIVE -> Error.copy(alpha = 0.16f)
        MarketTagTone.NEUTRAL -> VpnSurfaceStrong
    }
    val contentColor = when (tag.tone) {
        MarketTagTone.ACCENT -> VpnAccent
        MarketTagTone.POSITIVE -> Color(0xFF10C88C)
        MarketTagTone.NEGATIVE -> Error
        MarketTagTone.NEUTRAL -> TextSecondary
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
    ) {
        Text(
            text = tag.label,
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun MarketBoardDivider() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .height(1.dp)
            .background(VpnOutline.copy(alpha = 0.72f)),
    )
}

private fun MarketBoard.primaryMetricColor(quote: MarketQuote): Color {
    return when (this) {
        MarketBoard.HOT, MarketBoard.ALL, MarketBoard.FAVORITES, MarketBoard.GAINERS ->
            if (quote.changeRateValue >= 0f) VpnAccent else Error
        MarketBoard.VOLUME -> TextPrimary
        MarketBoard.NEW -> VpnAccent
    }
}

@Preview
@Composable
private fun MarketOverviewPagePreview() {
    CryptoVPNTheme {
        MarketOverviewContent(
            quotes = listOf(
                MarketQuote(
                    instrumentId = "crypto:bitcoin",
                    symbol = "BTC",
                    name = "Bitcoin",
                    market = "CRYPTO",
                    marketType = "CONTRACT",
                    sessionLabel = "24x7",
                    lastPrice = "\$71,697.00",
                    changeAmount = "+\$3,342.22",
                    changePercent = "+4.89%",
                    volume24h = "\$51.65B",
                    marketCap = "\$1.43T",
                    peRatio = "--",
                    dayRange = "\$67,805.00 - \$72,379.00",
                    categories = setOf(MarketCategory.HOT, MarketCategory.US_STOCKS),
                    tags = listOf(MarketTag("热门", MarketTagTone.ACCENT)),
                    changeRateValue = 4.89f,
                    turnover24hValue = 51647750364.0,
                    heatRank = 2,
                ),
            ),
            spotlights = listOf(
                MarketSpotlight(
                    instrumentId = "crypto:bitcoin",
                    symbol = "BTC",
                    eyebrow = "热度榜 #2",
                    title = "Bitcoin",
                    subtitle = "BTC · CRYPTO",
                    primaryValue = "+4.91%",
                    secondaryValue = "\$48.47B",
                ),
            ),
            onOpenQuote = {},
        )
    }
}
