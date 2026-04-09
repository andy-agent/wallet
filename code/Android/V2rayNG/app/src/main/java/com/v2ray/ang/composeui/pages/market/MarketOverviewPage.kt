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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.HorizontalDivider
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val MarketPageBackground = Color(0xFFFFFFFF)
private val MarketTopGlow = Color(0xFFF4F8FC)
private val MarketSearchSurface = Color(0xFFF6F8FB)
private val MarketChipSurface = Color(0xFFF5F8FB)
private val MarketChipSurfaceStrong = Color(0xFFEFF8FB)
private val MarketLine = Color(0xFFE8EEF5)
private val MarketLineStrong = Color(0xFFD7E2EE)
private val MarketTextPrimary = Color(0xFF0C1B2A)
private val MarketTextSecondary = Color(0xFF7D8C9D)
private val MarketTextMuted = Color(0xFFB0BAC7)
private val MarketAccent = Color(0xFF1CB7D0)
private val MarketAccentSoft = Color(0xFFECFAFD)
private val MarketPositive = Color(0xFF19A8C8)
private val MarketNegative = Color(0xFFD85D86)

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
    val headlineSpotlight = spotlights.firstOrNull()
    val signalSpotlights = remember(spotlights) { if (spotlights.size > 1) spotlights.drop(1) else emptyList() }

    MarketOverviewBackground {
        Scaffold(
            containerColor = Color.Transparent,
            contentColor = MarketTextPrimary,
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
            ) {
                item {
                    MarketCompactHeader(
                        query = query,
                        onQueryChange = { query = it },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(18.dp))
                }
                item {
                    MarketPrimaryTabs(
                        labels = marketPrimarySections.map { it.label },
                        selectedIndex = selectedPrimaryIndex,
                        onSelect = { selectedPrimaryIndex = it },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                }
                if (headlineSpotlight != null) {
                    item {
                        MarketAnnouncementBanner(spotlight = headlineSpotlight)
                    }
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                    }
                }
                item {
                    MarketBoardTabs(
                        labels = boardOptions.map { it.label },
                        selectedIndex = selectedBoardIndex,
                        onSelect = { selectedBoardIndex = it },
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                }
                item {
                    MarketFilterRow(
                        selectedPrimary = selectedPrimary,
                        selectedBoard = selectedBoard,
                        quoteCount = visibleQuotes.size,
                    )
                }
                if (signalSpotlights.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    item {
                        MarketSignalStrip(
                            spotlights = signalSpotlights,
                            quotes = quotes,
                            onOpenQuote = onOpenQuote,
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(10.dp))
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
private fun MarketOverviewBackground(
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MarketPageBackground),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MarketTopGlow, MarketPageBackground),
                    ),
                ),
        )
        content()
    }
}

@Composable
private fun MarketCompactHeader(
    query: String,
    onQueryChange: (String) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Surface(
            modifier = Modifier.size(42.dp),
            shape = CircleShape,
            color = Color(0xFFF6B6DA),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Text(
                    text = "M",
                    style = MaterialTheme.typography.titleMedium,
                    color = MarketTextPrimary,
                    fontWeight = FontWeight.Black,
                )
            }
        }
        Surface(
            modifier = Modifier.weight(1f),
            shape = RoundedCornerShape(24.dp),
            color = MarketSearchSurface,
            border = BorderStroke(1.dp, MarketLine),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = MarketTextMuted,
                )
                BasicTextField(
                    value = query,
                    onValueChange = onQueryChange,
                    modifier = Modifier.weight(1f),
                    singleLine = true,
                    textStyle = MaterialTheme.typography.bodyLarge.merge(
                        TextStyle(
                            color = MarketTextPrimary,
                            fontWeight = FontWeight.Medium,
                        ),
                    ),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier.fillMaxWidth(),
                            contentAlignment = Alignment.CenterStart,
                        ) {
                            if (query.isBlank()) {
                                Text(
                                    text = "全局搜索",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MarketTextMuted,
                                )
                            }
                            innerTextField()
                        }
                    },
                )
                Box(
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Color.White)
                        .clickable { }
                        .padding(8.dp),
                ) {
                    Icon(
                        imageVector = Icons.Default.Tune,
                        contentDescription = null,
                        tint = MarketTextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketPrimaryTabs(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(30.dp),
    ) {
        labels.forEachIndexed { index, label ->
            val selected = index == selectedIndex
            Text(
                text = label,
                modifier = Modifier.clickable { onSelect(index) },
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
                    color = if (selected) MarketTextPrimary else MarketTextMuted,
                    letterSpacing = (-0.4).sp,
                ),
            )
        }
    }
}

@Composable
private fun MarketAnnouncementBanner(
    spotlight: MarketSpotlight,
) {
    Surface(
        shape = RoundedCornerShape(18.dp),
        color = MarketAccentSoft,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 14.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFB8F0FA),
                ) {
                    Text(
                        text = spotlight.eyebrow,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        style = MaterialTheme.typography.labelSmall,
                        color = MarketTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                    )
                }
                Text(
                    text = "${spotlight.title} ${spotlight.primaryValue}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MarketTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = spotlight.subtitle,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MarketTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = null,
                tint = MarketTextSecondary,
            )
        }
    }
}

@Composable
private fun MarketBoardTabs(
    labels: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(24.dp),
        ) {
            labels.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                Column(
                    modifier = Modifier.clickable { onSelect(index) },
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(7.dp),
                ) {
                    Text(
                        text = label,
                        style = MaterialTheme.typography.titleLarge.copy(
                            color = if (selected) MarketTextPrimary else MarketTextSecondary,
                            fontWeight = if (selected) FontWeight.Black else FontWeight.SemiBold,
                            letterSpacing = (-0.2).sp,
                        ),
                    )
                    Box(
                        modifier = Modifier
                            .width(36.dp)
                            .height(3.dp)
                            .clip(RoundedCornerShape(999.dp))
                            .background(if (selected) MarketTextPrimary else Color.Transparent),
                    )
                }
            }
        }
        HorizontalDivider(color = MarketLine, thickness = 0.8.dp)
    }
}

@Composable
private fun MarketFilterRow(
    selectedPrimary: MarketPrimarySection,
    selectedBoard: MarketBoard,
    quoteCount: Int,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MarketFilterChip(
                text = when (selectedPrimary) {
                    MarketPrimarySection.TOKEN -> "全市场"
                    MarketPrimarySection.CONTRACT -> "USDT"
                    MarketPrimarySection.STOCK -> "美股"
                },
            )
            MarketFilterChip(text = "24h")
        }
        Text(
            text = "${selectedBoard.label} · ${padCount(quoteCount)}",
            style = MaterialTheme.typography.labelMedium,
            color = MarketTextSecondary,
        )
    }
}

@Composable
private fun MarketFilterChip(
    text: String,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = MarketChipSurface,
        border = BorderStroke(1.dp, MarketLine),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
            style = MaterialTheme.typography.bodyMedium,
            color = MarketTextPrimary,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun MarketSignalStrip(
    spotlights: List<MarketSpotlight>,
    quotes: List<MarketQuote>,
    onOpenQuote: (MarketQuote) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        spotlights.forEach { spotlight ->
            val relatedQuote = quotes.firstOrNull {
                it.instrumentId == spotlight.instrumentId || it.symbol == spotlight.symbol
            }
            val metricColor = when {
                spotlight.primaryValue.trim().startsWith("-") -> MarketNegative
                spotlight.primaryValue.trim().startsWith("+") -> MarketPositive
                else -> MarketTextPrimary
            }
            Surface(
                modifier = Modifier.clickable(enabled = relatedQuote != null) {
                    relatedQuote?.let(onOpenQuote)
                },
                shape = RoundedCornerShape(14.dp),
                color = MarketChipSurfaceStrong,
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = spotlight.title,
                        style = MaterialTheme.typography.labelMedium,
                        color = MarketTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = spotlight.primaryValue,
                        style = MaterialTheme.typography.labelMedium,
                        color = metricColor,
                        fontWeight = FontWeight.Bold,
                    )
                }
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
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        MarketQuoteHeader(board = board)
        quotes.forEachIndexed { index, quote ->
            MarketQuoteRow(
                quote = quote,
                board = board,
                onClick = { onOpenQuote(quote) },
            )
            if (index != quotes.lastIndex) {
                HorizontalDivider(color = MarketLine, thickness = 0.8.dp)
            }
        }
    }
}

@Composable
private fun MarketQuoteHeader(
    board: MarketBoard,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MarketHeaderCell(
                modifier = Modifier.weight(1.25f),
                title = "名称",
            )
            MarketHeaderCell(
                modifier = Modifier.weight(0.9f),
                title = "最新价格",
                alignEnd = true,
            )
            MarketHeaderCell(
                modifier = Modifier.weight(0.85f),
                title = board.columnLabel,
                alignEnd = true,
            )
        }
        HorizontalDivider(color = MarketLineStrong, thickness = 0.8.dp)
    }
}

@Composable
private fun MarketHeaderCell(
    modifier: Modifier,
    title: String,
    alignEnd: Boolean = false,
) {
    Box(modifier = modifier) {
        Text(
            text = title,
            modifier = Modifier.align(if (alignEnd) Alignment.CenterEnd else Alignment.CenterStart),
            style = MaterialTheme.typography.bodyMedium,
            color = MarketTextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun MarketQuoteRow(
    quote: MarketQuote,
    board: MarketBoard,
    onClick: () -> Unit,
) {
    val tag = remember(quote) {
        when {
            quote.isFavorite -> MarketTag("收藏", MarketTagTone.ACCENT)
            quote.tags.isNotEmpty() -> quote.tags.first()
            quote.heatRank != null -> MarketTag("热度 ${quote.heatRank}", MarketTagTone.NEUTRAL)
            else -> null
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            modifier = Modifier.weight(1.25f),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MarketSymbolAvatar(symbol = quote.symbol)
            Column(
                verticalArrangement = Arrangement.spacedBy(5.dp),
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = quote.symbol,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MarketTextPrimary,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.4).sp,
                        ),
                        maxLines = 1,
                    )
                    if (tag != null) {
                        MarketInlineMarker(tag = tag)
                    }
                }
                Text(
                    text = quote.marketMeta(),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MarketTextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
            }
        }
        Column(
            modifier = Modifier.weight(0.9f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = quote.lastPrice,
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = MarketTextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.3).sp,
                ),
                maxLines = 1,
            )
            Text(
                text = quote.changeAmount,
                style = MaterialTheme.typography.bodyMedium,
                color = MarketTextSecondary,
                maxLines = 1,
            )
        }
        Column(
            modifier = Modifier.weight(0.85f),
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(5.dp),
        ) {
            Text(
                text = board.primaryMetric(quote),
                style = MaterialTheme.typography.headlineSmall.copy(
                    color = board.primaryMetricColor(quote),
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.3).sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = board.secondaryMetric(quote),
                style = MaterialTheme.typography.bodyMedium,
                color = MarketTextSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MarketSymbolAvatar(
    symbol: String,
) {
    val palette = remember(symbol) {
        marketAvatarPalette[(symbol.hashCode().absoluteValue()) % marketAvatarPalette.size]
    }
    Surface(
        modifier = Modifier.size(46.dp),
        shape = CircleShape,
        color = palette.first,
    ) {
        Box(contentAlignment = Alignment.Center) {
            Text(
                text = symbol.take(2).uppercase(),
                style = MaterialTheme.typography.labelLarge,
                color = palette.second,
                fontWeight = FontWeight.Black,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun MarketInlineMarker(tag: MarketTag) {
    val containerColor = when (tag.tone) {
        MarketTagTone.ACCENT -> MarketAccentSoft
        MarketTagTone.POSITIVE -> MarketPositive.copy(alpha = 0.12f)
        MarketTagTone.NEGATIVE -> MarketNegative.copy(alpha = 0.12f)
        MarketTagTone.NEUTRAL -> MarketChipSurface
    }
    val contentColor = when (tag.tone) {
        MarketTagTone.ACCENT -> MarketAccent
        MarketTagTone.POSITIVE -> MarketPositive
        MarketTagTone.NEGATIVE -> MarketNegative
        MarketTagTone.NEUTRAL -> MarketTextSecondary
    }
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = containerColor,
    ) {
        Text(
            text = tag.label,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelSmall,
            color = contentColor,
            fontWeight = FontWeight.Medium,
            maxLines = 1,
        )
    }
}

private fun MarketBoard.primaryMetricColor(quote: MarketQuote): Color {
    return when (this) {
        MarketBoard.HOT,
        MarketBoard.ALL,
        MarketBoard.FAVORITES,
        MarketBoard.GAINERS,
        -> if (quote.changeRateValue >= 0f) MarketPositive else MarketNegative

        MarketBoard.VOLUME,
        MarketBoard.NEW,
        -> MarketTextPrimary
    }
}

private fun MarketQuote.marketMeta(): String {
    val parts = buildList {
        if (volume24h.isNotBlank()) {
            add(volume24h)
        }
        when {
            marketCap.isNotBlank() && marketCap != "--" -> add(marketCap)
            sessionLabel.isNotBlank() -> add(sessionLabel)
            market.isNotBlank() -> add(market)
        }
    }
    return parts.joinToString("  |  ").ifBlank { name }
}

private fun Int.absoluteValue(): Int = if (this == Int.MIN_VALUE) 0 else kotlin.math.abs(this)

private fun padCount(value: Int): String = value.coerceAtLeast(0).toString().padStart(2, '0')

private val marketAvatarPalette = listOf(
    Color(0xFFE5F4FF) to Color(0xFF1175C5),
    Color(0xFFFDEAD8) to Color(0xFFC26A16),
    Color(0xFFE7F8EE) to Color(0xFF238A58),
    Color(0xFFF7E9FF) to Color(0xFF9A45CF),
    Color(0xFFFFE7EF) to Color(0xFFC24974),
    Color(0xFFEAF1FF) to Color(0xFF355FC9),
)

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
                    instrumentId = "contract:sol",
                    symbol = "SOLUSDT",
                    name = "Solana",
                    market = "CONTRACT",
                    marketType = "CONTRACT",
                    sessionLabel = "USDT 永续",
                    lastPrice = "\$141.32",
                    changeAmount = "-\$1.76",
                    changePercent = "-1.23%",
                    volume24h = "\$10.62M",
                    marketCap = "\$70.11B",
                    peRatio = "--",
                    dayRange = "\$138.00 - \$146.30",
                    categories = setOf(MarketCategory.HOT),
                    tags = listOf(MarketTag("75x", MarketTagTone.NEUTRAL)),
                    changeRateValue = -1.23f,
                    turnover24hValue = 10620000.0,
                    heatRank = 5,
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
                    heatRank = 3,
                ),
            ),
            spotlights = listOf(
                MarketSpotlight(
                    instrumentId = "contract:sol",
                    symbol = "SOLUSDT",
                    eyebrow = "新交易对",
                    title = "SOLUSDT",
                    subtitle = "今日上线 1-50 倍合约",
                    primaryValue = "+4.91%",
                    secondaryValue = "\$48.47B",
                ),
                MarketSpotlight(
                    instrumentId = "crypto:bitcoin",
                    symbol = "BTC",
                    eyebrow = "热度榜 #2",
                    title = "BTC",
                    subtitle = "BTC · CRYPTO",
                    primaryValue = "+4.91%",
                    secondaryValue = "\$48.47B",
                ),
                MarketSpotlight(
                    instrumentId = "stock:nvda",
                    symbol = "NVDA",
                    eyebrow = "AI 风向",
                    title = "NVDA",
                    subtitle = "NVDA · STOCK",
                    primaryValue = "+2.03%",
                    secondaryValue = "\$12.21B",
                ),
            ),
            onOpenQuote = {},
        )
    }
}
