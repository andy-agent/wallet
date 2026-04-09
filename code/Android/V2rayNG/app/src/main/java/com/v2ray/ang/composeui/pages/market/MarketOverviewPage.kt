package com.v2ray.ang.composeui.pages.market

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.pages.vpn.VpnEmptyPanel
import com.v2ray.ang.composeui.pages.vpn.VpnLoadingPanel
import com.v2ray.ang.composeui.pages.vpn.VpnPageBottomPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageHorizontalPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageTopPadding
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import com.v2ray.ang.composeui.theme.Error
import com.v2ray.ang.composeui.theme.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val MarketPageBackground = Color(0xFFF4F7FB)
private val MarketPageGlow = Color(0xFFE1ECFF)
private val MarketPanel = Color(0xFFFFFFFF)
private val MarketPanelMuted = Color(0xFFF0F4FA)
private val MarketPanelSoft = Color(0xFFE7EEF8)
private val MarketOutlineSoft = Color(0xFFD7E1EE)
private val MarketOutlineStrong = Color(0xFFBDD0EE)
private val MarketInk = Color(0xFF0D1828)
private val MarketInkSoft = Color(0xFF536275)
private val MarketInkMuted = Color(0xFF8E9CAF)
private val MarketAccent = Color(0xFF246BFD)
private val MarketAccentSoft = Color(0xFFE9F0FF)
private val MarketHeroDeep = Color(0xFF101E34)
private val MarketHeroDeepSecondary = Color(0xFF1A2D49)
private val MarketSelectionFill = MarketAccentSoft
private val MarketSelectionBorder = Color(0xFFB2C8FF)
private val MarketPositive = Success

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
            MarketOverviewBackground {
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
            MarketOverviewBackground {
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
    val leadQuote = visibleQuotes.firstOrNull()
    val positiveCount = visibleQuotes.count { it.changeRateValue >= 0f }
    val favoriteCount = visibleQuotes.count { it.isFavorite }

    MarketOverviewBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MarketInk,
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
                verticalArrangement = Arrangement.spacedBy(18.dp),
            ) {
                item {
                    MarketHeroDeck(
                        query = query,
                        onQueryChange = { query = it },
                        primaryLabels = marketPrimarySections.map { it.label },
                        selectedPrimaryIndex = selectedPrimaryIndex,
                        onSelectPrimary = { selectedPrimaryIndex = it },
                        boardLabels = boardOptions.map { it.label },
                        selectedBoardIndex = selectedBoardIndex,
                        onSelectBoard = { selectedBoardIndex = it },
                        selectedPrimary = selectedPrimary,
                        selectedBoard = selectedBoard,
                        leadQuote = leadQuote,
                        visibleCount = visibleQuotes.size,
                        positiveCount = positiveCount,
                        favoriteCount = favoriteCount,
                        spotlightCount = spotlights.size,
                    )
                }
                if (spotlights.isNotEmpty()) {
                    item {
                        MarketSpotlightStrip(
                            spotlights = spotlights,
                            quotes = quotes,
                            onOpenQuote = onOpenQuote,
                        )
                    }
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
                            selectedPrimary = selectedPrimary,
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
private fun MarketOverviewBackground(
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFFFCFDFF), MarketPageBackground),
                ),
            ),
    ) {
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(MarketPageGlow.copy(alpha = 0.72f), Color.Transparent),
                        radius = 1240f,
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color(0xFFEAF2FF),
                            Color(0xFFF7FAFF),
                            Color(0xFFE8F6F1),
                        ),
                    ),
                ),
        )
        content()
    }
}

@Composable
private fun MarketHeroDeck(
    query: String,
    onQueryChange: (String) -> Unit,
    primaryLabels: List<String>,
    selectedPrimaryIndex: Int,
    onSelectPrimary: (Int) -> Unit,
    boardLabels: List<String>,
    selectedBoardIndex: Int,
    onSelectBoard: (Int) -> Unit,
    selectedPrimary: MarketPrimarySection,
    selectedBoard: MarketBoard,
    leadQuote: MarketQuote?,
    visibleCount: Int,
    positiveCount: Int,
    favoriteCount: Int,
    spotlightCount: Int,
) {
    Surface(
        shape = RoundedCornerShape(32.dp),
        color = MarketPanel,
        shadowElevation = 22.dp,
        border = BorderStroke(1.dp, MarketOutlineSoft),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 22.dp),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = "MARKET / LIVE GRID",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MarketAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.6.sp,
                        ),
                    )
                    Text(
                        text = "探索 ${selectedPrimary.label} 市场",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = MarketInk,
                            fontWeight = FontWeight.Black,
                            lineHeight = 36.sp,
                        ),
                    )
                    Text(
                        text = "把搜索、一级分类、榜单切换和实时焦点压进同一视区，先看信号，再进详情。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MarketInkSoft,
                    )
                }
                MarketCounterCapsule(
                    text = "${selectedBoard.label} · ${padCount(visibleCount)}",
                    emphasized = true,
                )
            }
            MarketSearchField(
                value = query,
                onValueChange = onQueryChange,
            )
            MarketTopTabs(
                labels = primaryLabels,
                selectedIndex = selectedPrimaryIndex,
                onSelect = onSelectPrimary,
            )
            MarketBoardTabs(
                labels = boardLabels,
                selectedIndex = selectedBoardIndex,
                onSelect = onSelectBoard,
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MarketHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "可见标的",
                    value = padCount(visibleCount),
                )
                MarketHeroStat(
                    modifier = Modifier.weight(1f),
                    label = "上涨席位",
                    value = padCount(positiveCount),
                )
                MarketHeroStat(
                    modifier = Modifier.weight(1f),
                    label = if (selectedPrimary == MarketPrimarySection.STOCK) "收藏标的" else "监控信号",
                    value = padCount(if (selectedPrimary == MarketPrimarySection.STOCK) favoriteCount else spotlightCount),
                )
            }
            MarketLeadQuoteCard(
                quote = leadQuote,
                board = selectedBoard,
            )
        }
    }
}

@Composable
private fun MarketSearchField(
    value: String,
    onValueChange: (String) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MarketPanelMuted,
        border = BorderStroke(1.dp, MarketOutlineSoft),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                tint = MarketInkMuted,
            )
            BasicTextField(
                value = value,
                onValueChange = onValueChange,
                modifier = Modifier.weight(1f),
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyLarge.merge(
                    TextStyle(
                        color = MarketInk,
                        fontWeight = FontWeight.Medium,
                    ),
                ),
                decorationBox = { innerTextField ->
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.CenterStart,
                    ) {
                        if (value.isBlank()) {
                            Text(
                                text = "搜索代币/合约/股票/DApp",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MarketInkMuted,
                            )
                        }
                        innerTextField()
                    }
                },
            )
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(MarketPanel)
                    .clickable { }
                    .padding(10.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = null,
                    tint = MarketAccent,
                )
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
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MarketPanelMuted,
        border = BorderStroke(1.dp, MarketOutlineSoft),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(
                            brush = if (selected) {
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MarketAccentSoft,
                                        Color(0xFFF7FAFF),
                                    ),
                                )
                            } else {
                                Brush.horizontalGradient(
                                    colors = listOf(Color.Transparent, Color.Transparent),
                                )
                            },
                        )
                        .clickable { onSelect(index) }
                        .padding(vertical = 12.dp),
                ) {
                    Text(
                        text = label,
                        modifier = Modifier.align(Alignment.Center),
                        style = MaterialTheme.typography.titleSmall,
                        color = if (selected) MarketInk else MarketInkSoft,
                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    )
                }
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
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Surface(
                modifier = Modifier.clickable { onSelect(index) },
                shape = RoundedCornerShape(18.dp),
                color = if (selected) MarketHeroDeep else MarketPanel,
                border = BorderStroke(
                    width = 1.dp,
                    color = if (selected) Color.Transparent else MarketOutlineSoft,
                ),
            ) {
                Text(
                    text = label,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
                    style = MaterialTheme.typography.labelLarge,
                    color = if (selected) Color.White else MarketInkSoft,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                )
            }
        }
    }
}

@Composable
private fun MarketHeroStat(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(22.dp),
        color = MarketPanelMuted,
        border = BorderStroke(1.dp, MarketOutlineSoft),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MarketInkMuted,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Black,
                    color = MarketInk,
                ),
            )
        }
    }
}

@Composable
private fun MarketLeadQuoteCard(
    quote: MarketQuote?,
    board: MarketBoard,
) {
    Surface(
        shape = RoundedCornerShape(28.dp),
        color = MarketHeroDeep,
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(
                            MarketHeroDeep,
                            MarketHeroDeepSecondary,
                        ),
                    ),
                ),
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 18.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "FOCUS QUOTE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = MarketAccent.copy(alpha = 0.92f),
                                fontWeight = FontWeight.Bold,
                                letterSpacing = 1.4.sp,
                            ),
                        )
                        Text(
                            text = quote?.symbol ?: "暂无焦点标的",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                            ),
                        )
                    }
                    if (quote != null) {
                        MarketCounterCapsule(
                            text = quote.market,
                            emphasized = false,
                            onDark = true,
                        )
                    }
                }
                if (quote == null) {
                    Text(
                        text = "切换分类或榜单后，焦点标的会在这里展示。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.74f),
                    )
                } else {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            text = quote.lastPrice,
                            style = MaterialTheme.typography.displaySmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Black,
                            ),
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            MarketHeroMetric(
                                modifier = Modifier.weight(1f),
                                label = "24H 变化",
                                value = "${quote.changePercent} ${quote.changeAmount}",
                                valueColor = if (quote.changeRateValue >= 0f) MarketPositive else Error,
                            )
                            MarketHeroMetric(
                                modifier = Modifier.weight(1f),
                                label = board.columnLabel,
                                value = board.primaryMetric(quote),
                                valueColor = when (board) {
                                    MarketBoard.NEW, MarketBoard.VOLUME -> Color.White
                                    else -> board.primaryMetricColor(quote)
                                },
                            )
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MarketCounterCapsule(text = quote.sessionLabel, emphasized = false)
                            quote.tags.take(2).forEach { tag ->
                                MarketCounterCapsule(
                                    text = tag.label,
                                    emphasized = false,
                                    onDark = true,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketHeroMetric(
    modifier: Modifier = Modifier,
    label: String,
    value: String,
    valueColor: Color,
) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        color = Color.White.copy(alpha = 0.08f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.08f)),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = Color.White.copy(alpha = 0.62f),
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleSmall,
                color = valueColor,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MarketSpotlightStrip(
    spotlights: List<MarketSpotlight>,
    quotes: List<MarketQuote>,
    onOpenQuote: (MarketQuote) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = MarketPanel,
        border = BorderStroke(1.dp, MarketOutlineSoft),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(
                        text = "SIGNAL RADAR",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MarketAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.4.sp,
                        ),
                    )
                    Text(
                        text = "热点追踪",
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = MarketInk,
                            fontWeight = FontWeight.Black,
                        ),
                    )
                }
                MarketCounterCapsule(
                    text = padCount(spotlights.size),
                    emphasized = true,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                spotlights.forEach { spotlight ->
                    val relatedQuote = quotes.firstOrNull {
                        it.instrumentId == spotlight.instrumentId || it.symbol == spotlight.symbol
                    }
                    val metricColor = when {
                        spotlight.primaryValue.trim().startsWith("-") -> Error
                        spotlight.primaryValue.trim().startsWith("+") -> MarketPositive
                        else -> MarketInk
                    }
                    Surface(
                        modifier = Modifier
                            .clickable(enabled = relatedQuote != null) {
                                relatedQuote?.let(onOpenQuote)
                            },
                        shape = RoundedCornerShape(24.dp),
                        color = MarketPanelMuted,
                        border = BorderStroke(1.dp, MarketOutlineSoft),
                    ) {
                        Column(
                            modifier = Modifier
                                .widthIn(min = 196.dp, max = 220.dp)
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(4.dp)
                                    .background(
                                        brush = Brush.horizontalGradient(
                                            colors = listOf(MarketAccent, Color.Transparent),
                                        ),
                                    ),
                            )
                            Text(
                                text = spotlight.eyebrow,
                                style = MaterialTheme.typography.labelSmall,
                                color = MarketInkMuted,
                            )
                            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = spotlight.title,
                                    style = MaterialTheme.typography.titleMedium,
                                    color = MarketInk,
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                                Text(
                                    text = spotlight.subtitle,
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MarketInkSoft,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                )
                            }
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.Bottom,
                                horizontalArrangement = Arrangement.SpaceBetween,
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                    Text(
                                        text = spotlight.primaryValue,
                                        style = MaterialTheme.typography.titleLarge,
                                        color = metricColor,
                                        fontWeight = FontWeight.Black,
                                    )
                                    Text(
                                        text = spotlight.secondaryValue,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = MarketInkMuted,
                                    )
                                }
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                    contentDescription = null,
                                    tint = MarketInkMuted,
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MarketQuoteBoard(
    quotes: List<MarketQuote>,
    selectedPrimary: MarketPrimarySection,
    board: MarketBoard,
    onOpenQuote: (MarketQuote) -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(30.dp),
        color = MarketPanel,
        border = BorderStroke(1.dp, MarketOutlineSoft),
        shadowElevation = 12.dp,
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = "${selectedPrimary.label} / ${board.label}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = MarketAccent,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.2.sp,
                        ),
                    )
                    Text(
                        text = "实时榜单",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MarketInk,
                            fontWeight = FontWeight.Black,
                        ),
                    )
                    Text(
                        text = "列表仍然保持原有搜索、分类、榜单和详情入口，只把视觉层级重排为更强的信号面板。",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MarketInkSoft,
                    )
                }
                MarketCounterCapsule(
                    text = padCount(quotes.size),
                    emphasized = true,
                )
            }
            Surface(
                shape = RoundedCornerShape(22.dp),
                color = MarketPanelMuted,
                border = BorderStroke(1.dp, MarketOutlineSoft),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    MarketColumnHeader(
                        modifier = Modifier.weight(1.15f),
                        title = "标的",
                    )
                    MarketColumnHeader(
                        modifier = Modifier.weight(0.85f),
                        title = "最新价",
                        alignEnd = true,
                    )
                    MarketColumnHeader(
                        modifier = Modifier.weight(0.9f),
                        title = board.columnLabel,
                        alignEnd = true,
                    )
                }
            }
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                quotes.forEachIndexed { index, quote ->
                    MarketQuoteRow(
                        quote = quote,
                        board = board,
                        rank = index + 1,
                        onClick = { onOpenQuote(quote) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketColumnHeader(
    modifier: Modifier,
    title: String,
    alignEnd: Boolean = false,
) {
    Box(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.align(if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart),
            style = MaterialTheme.typography.labelMedium,
            color = MarketInkMuted,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MarketQuoteRow(
    quote: MarketQuote,
    board: MarketBoard,
    rank: Int,
    onClick: () -> Unit,
) {
    val trendColor = if (quote.changeRateValue >= 0f) MarketPositive else Error
    val markers = remember(quote) {
        buildList {
            if (quote.isFavorite) {
                add(MarketTag(label = "收藏", tone = MarketTagTone.ACCENT))
            }
            addAll(quote.tags.take(2))
        }
    }

    Surface(
        shape = RoundedCornerShape(24.dp),
        color = if (rank == 1) MarketAccentSoft.copy(alpha = 0.58f) else MarketPanel,
        border = BorderStroke(
            width = 1.dp,
            color = if (rank == 1) MarketSelectionBorder else MarketOutlineSoft,
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 16.dp, vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1.15f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MarketRankBadge(rank = rank)
                    Text(
                        text = quote.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = MarketInk,
                        fontWeight = FontWeight.Black,
                    )
                    MarketCounterCapsule(
                        text = quote.market,
                        emphasized = false,
                    )
                }
                Text(
                    text = "${quote.name} · ${quote.sessionLabel}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MarketInkSoft,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                if (markers.isNotEmpty()) {
                    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                        markers.forEach { tag ->
                            MarketInlineMarker(tag = tag)
                        }
                    }
                }
            }
            Column(
                modifier = Modifier.weight(0.85f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = quote.lastPrice,
                    style = MaterialTheme.typography.titleSmall,
                    color = MarketInk,
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                )
                Text(
                    text = quote.changeAmount,
                    style = MaterialTheme.typography.labelMedium,
                    color = trendColor,
                    maxLines = 1,
                )
            }
            Column(
                modifier = Modifier.weight(0.9f),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = board.primaryMetric(quote),
                    style = MaterialTheme.typography.titleSmall,
                    color = board.primaryMetricColor(quote),
                    fontWeight = FontWeight.Black,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = board.secondaryMetric(quote),
                    style = MaterialTheme.typography.labelSmall,
                    color = MarketInkMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
    }
}

@Composable
private fun MarketInlineMarker(tag: MarketTag) {
    val containerColor = when (tag.tone) {
        MarketTagTone.ACCENT -> MarketSelectionFill
        MarketTagTone.POSITIVE -> MarketPositive.copy(alpha = 0.14f)
        MarketTagTone.NEGATIVE -> Error.copy(alpha = 0.14f)
        MarketTagTone.NEUTRAL -> MarketPanelSoft
    }
    val contentColor = when (tag.tone) {
        MarketTagTone.ACCENT -> MarketAccent
        MarketTagTone.POSITIVE -> MarketPositive
        MarketTagTone.NEGATIVE -> Error
        MarketTagTone.NEUTRAL -> MarketInkSoft
    }
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = containerColor,
    ) {
        Text(
            text = tag.label,
            modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            maxLines = 1,
        )
    }
}

@Composable
private fun MarketRankBadge(rank: Int) {
    val containerColor = when (rank) {
        1 -> MarketAccent
        2 -> Color(0xFF1E3659)
        3 -> Color(0xFF445775)
        else -> MarketPanelSoft
    }
    val contentColor = if (rank <= 3) Color.White else MarketInkSoft
    Surface(
        shape = CircleShape,
        color = containerColor,
    ) {
        Text(
            text = rank.toString(),
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Bold,
        )
    }
}

@Composable
private fun MarketCounterCapsule(
    text: String,
    emphasized: Boolean,
    onDark: Boolean = false,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = when {
            emphasized -> MarketAccentSoft
            onDark -> Color.White.copy(alpha = 0.08f)
            else -> MarketPanelMuted
        },
        border = BorderStroke(
            width = 1.dp,
            color = when {
                emphasized -> MarketOutlineStrong
                onDark -> Color.White.copy(alpha = 0.14f)
                else -> MarketOutlineSoft
            },
        ),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelMedium,
            color = when {
                emphasized -> MarketAccent
                onDark -> Color.White.copy(alpha = 0.82f)
                else -> MarketInkSoft
            },
            fontWeight = FontWeight.SemiBold,
        )
    }
}

private fun MarketBoard.primaryMetricColor(quote: MarketQuote): Color {
    return when (this) {
        MarketBoard.HOT,
        MarketBoard.ALL,
        MarketBoard.FAVORITES,
        MarketBoard.GAINERS,
        -> if (quote.changeRateValue >= 0f) MarketPositive else Error

        MarketBoard.VOLUME -> MarketInk
        MarketBoard.NEW -> MarketInk
    }
}

private fun padCount(value: Int): String = value.coerceAtLeast(0).toString().padStart(2, '0')

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
                MarketQuote(
                    instrumentId = "stock:nvda",
                    symbol = "NVDA",
                    name = "NVIDIA",
                    market = "STOCK",
                    marketType = "STOCK",
                    sessionLabel = "US",
                    lastPrice = "\$934.13",
                    changeAmount = "+\$18.62",
                    changePercent = "+2.03%",
                    volume24h = "\$12.21B",
                    marketCap = "\$2.29T",
                    peRatio = "71.3",
                    dayRange = "\$919.00 - \$944.20",
                    categories = setOf(MarketCategory.US_STOCKS),
                    tags = listOf(MarketTag("AI", MarketTagTone.NEUTRAL)),
                    isFavorite = true,
                    changeRateValue = 2.03f,
                    turnover24hValue = 12210000000.0,
                    heatRank = 5,
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
                MarketSpotlight(
                    instrumentId = "stock:nvda",
                    symbol = "NVDA",
                    eyebrow = "AI 风向",
                    title = "NVIDIA",
                    subtitle = "NVDA · STOCK",
                    primaryValue = "+2.03%",
                    secondaryValue = "\$12.21B",
                ),
            ),
            onOpenQuote = {},
        )
    }
}
