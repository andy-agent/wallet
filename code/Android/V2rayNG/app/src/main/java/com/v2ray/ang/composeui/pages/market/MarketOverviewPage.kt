package com.v2ray.ang.composeui.pages.market

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.components.tags.StatusTag
import com.v2ray.ang.composeui.components.tags.StatusType
import com.v2ray.ang.composeui.pages.vpn.VpnPageBottomPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageHorizontalPadding
import com.v2ray.ang.composeui.pages.vpn.VpnPageTopPadding
import com.v2ray.ang.composeui.theme.AuditState
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.ControlPlaneTokens
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

private val MarketLayer0 = ControlPlaneTokens.layer(ControlPlaneLayer.Level0)
private val MarketLayer1 = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
private val MarketLayer2 = ControlPlaneTokens.layer(ControlPlaneLayer.Level2)
private val MarketLayer3 = ControlPlaneTokens.layer(ControlPlaneLayer.Level3)
private val MarketInfra = ControlPlaneTokens.Infra
private val MarketSettlement = ControlPlaneTokens.Settlement
private val MarketFinance = ControlPlaneTokens.Finance
private val MarketNeutral = ControlPlaneTokens.Neutral
private val MarketWarning = ControlPlaneTokens.Warning
private val MarketCritical = ControlPlaneTokens.Critical

private val MarketPageBackground = MarketLayer0.container
private val MarketTopWash = MarketInfra.accent.copy(alpha = 0.06f)
private val MarketInfraGlow = MarketInfra.accent.copy(alpha = 0.05f)
private val MarketSettlementGlow = MarketSettlement.accent.copy(alpha = 0.05f)
private val MarketFinanceGlow = MarketFinance.accent.copy(alpha = 0.04f)
private val MarketLine = MarketLayer1.outline
private val MarketLineStrong = MarketLayer2.outline
private val MarketTextPrimary = ControlPlaneTokens.Ink
private val MarketTextSecondary = ControlPlaneTokens.InkSecondary
private val MarketTextMuted = ControlPlaneTokens.InkTertiary
private val MarketAccent = MarketInfra.accent
private val MarketAccentSoft = MarketInfra.container
private val MarketPositive = MarketSettlement.accent
private val MarketNegative = MarketCritical.accent
private val MarketSkeleton = MarketLayer2.container
private val MarketSkeletonStrong = MarketLayer3.container

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
        MarketOverviewUiState.Loading -> MarketOverviewLoadingContent()

        is MarketOverviewUiState.Error -> MarketOverviewErrorContent(
            message = currentState.message,
            onRetry = viewModel::refresh,
        )

        is MarketOverviewUiState.Loaded -> MarketOverviewContent(
            quotes = currentState.quotes,
            spotlights = currentState.spotlights,
            onOpenQuote = onOpenQuote,
        )
    }
}

@Composable
private fun MarketOverviewLoadingContent() {
    val selectedPrimary = MarketPrimarySection.CONTRACT
    val boardOptions = remember { marketBoardsFor(selectedPrimary) }
    val selectedBoardIndex = boardOptions.indexOf(MarketBoard.ALL).takeIf { it >= 0 } ?: 0
    val selectedBoard = boardOptions[selectedBoardIndex]

    MarketOverviewShell(
        query = "",
        onQueryChange = {},
        selectedPrimaryIndex = marketPrimarySections.indexOf(selectedPrimary).takeIf { it >= 0 } ?: 1,
        onSelectPrimary = {},
        boardOptions = boardOptions,
        selectedBoardIndex = selectedBoardIndex,
        onSelectBoard = {},
        selectedPrimary = selectedPrimary,
        selectedBoard = selectedBoard,
        countLabel = "--",
        visibleQuotes = emptyList(),
        headlineSpotlight = MarketSpotlight(
            instrumentId = "loading:banner",
            symbol = "SYNC",
            eyebrow = "市场同步中",
            title = "合约行情即将显示",
            subtitle = "页面骨架已提前加载，可直接评估样式方向",
            primaryValue = "--",
            secondaryValue = "--",
        ),
        signalSpotlights = listOf(
            MarketSpotlight("loading:signal-1", "HOT", "同步", "热门", "", "--", "--"),
            MarketSpotlight("loading:signal-2", "NEW", "同步", "新上线", "", "--", "--"),
            MarketSpotlight("loading:signal-3", "ALL", "同步", "全部", "", "--", "--"),
        ),
        signalQuotes = emptyList(),
        onOpenQuote = {},
    ) {
        MarketQuoteLoadingBoard(board = selectedBoard)
    }
}

@Composable
private fun MarketOverviewErrorContent(
    message: String,
    onRetry: () -> Unit,
) {
    val selectedPrimary = MarketPrimarySection.CONTRACT
    val boardOptions = remember { marketBoardsFor(selectedPrimary) }
    val selectedBoardIndex = boardOptions.indexOf(MarketBoard.ALL).takeIf { it >= 0 } ?: 0
    val selectedBoard = boardOptions[selectedBoardIndex]

    MarketOverviewShell(
        query = "",
        onQueryChange = {},
        selectedPrimaryIndex = marketPrimarySections.indexOf(selectedPrimary).takeIf { it >= 0 } ?: 1,
        onSelectPrimary = {},
        boardOptions = boardOptions,
        selectedBoardIndex = selectedBoardIndex,
        onSelectBoard = {},
        selectedPrimary = selectedPrimary,
        selectedBoard = selectedBoard,
        countLabel = "00",
        visibleQuotes = emptyList(),
        headlineSpotlight = MarketSpotlight(
            instrumentId = "error:banner",
            symbol = "WARN",
            eyebrow = "行情加载失败",
            title = "未拿到实时数据",
            subtitle = "已保留市场页结构，便于先评审布局方向",
            primaryValue = "--",
            secondaryValue = "--",
        ),
        signalSpotlights = emptyList(),
        signalQuotes = emptyList(),
        onOpenQuote = {},
    ) {
        MarketQuoteEmptyBoard(
            board = selectedBoard,
            title = "暂无实时行情",
            subtitle = message,
            actionText = "重新加载",
            onAction = onRetry,
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

    MarketOverviewShell(
        query = query,
        onQueryChange = { query = it },
        selectedPrimaryIndex = selectedPrimaryIndex,
        onSelectPrimary = { selectedPrimaryIndex = it },
        boardOptions = boardOptions,
        selectedBoardIndex = selectedBoardIndex,
        onSelectBoard = { selectedBoardIndex = it },
        selectedPrimary = selectedPrimary,
        selectedBoard = selectedBoard,
        countLabel = padCount(visibleQuotes.size),
        visibleQuotes = visibleQuotes,
        headlineSpotlight = headlineSpotlight,
        signalSpotlights = signalSpotlights,
        signalQuotes = quotes,
        onOpenQuote = onOpenQuote,
    ) {
        if (visibleQuotes.isEmpty()) {
            MarketQuoteEmptyBoard(
                board = selectedBoard,
                title = "未找到相关行情",
                subtitle = "尝试更换搜索词或切换榜单。",
            )
        } else {
            MarketQuoteBoard(
                quotes = visibleQuotes,
                board = selectedBoard,
                onOpenQuote = onOpenQuote,
            )
        }
    }
}

@Composable
private fun MarketOverviewShell(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedPrimaryIndex: Int,
    onSelectPrimary: (Int) -> Unit,
    boardOptions: List<MarketBoard>,
    selectedBoardIndex: Int,
    onSelectBoard: (Int) -> Unit,
    selectedPrimary: MarketPrimarySection,
    selectedBoard: MarketBoard,
    countLabel: String,
    visibleQuotes: List<MarketQuote>,
    headlineSpotlight: MarketSpotlight?,
    signalSpotlights: List<MarketSpotlight>,
    signalQuotes: List<MarketQuote>,
    onOpenQuote: (MarketQuote) -> Unit,
    body: @Composable () -> Unit,
) {
    val activeQuotes = remember(visibleQuotes, signalQuotes) {
        if (visibleQuotes.isNotEmpty()) visibleQuotes else signalQuotes
    }
    val feedSnapshot = remember(activeQuotes) { activeQuotes.toFeedSnapshot() }

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
                        onQueryChange = onQueryChange,
                        selectedPrimary = selectedPrimary,
                        selectedBoard = selectedBoard,
                        countLabel = countLabel,
                        feedSnapshot = feedSnapshot,
                    )
                }
                item {
                    Spacer(modifier = Modifier.height(20.dp))
                }
                if (headlineSpotlight != null) {
                    item {
                        MarketAnnouncementBanner(
                            spotlight = headlineSpotlight,
                            selectedPrimary = selectedPrimary,
                            selectedBoard = selectedBoard,
                            countLabel = countLabel,
                            visibleQuotes = activeQuotes,
                            feedSnapshot = feedSnapshot,
                        )
                    }
                    item {
                        Spacer(modifier = Modifier.height(16.dp))
                    }
                }
                item {
                    MarketConsoleSurface(
                        layer = ControlPlaneLayer.Level1,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp, vertical = 16.dp),
                            verticalArrangement = Arrangement.spacedBy(14.dp),
                        ) {
                            MarketSectionHeader(
                                eyebrow = "观察切片",
                                title = "监控范围",
                                detail = "${selectedPrimary.label} / ${selectedBoard.label}",
                            )
                            MarketPrimaryTabs(
                                labels = marketPrimarySections.map { it.label },
                                selectedIndex = selectedPrimaryIndex,
                                onSelect = onSelectPrimary,
                            )
                            HorizontalDivider(color = MarketLine, thickness = 1.dp)
                            MarketBoardTabs(
                                labels = boardOptions.map { it.label },
                                selectedIndex = selectedBoardIndex,
                                onSelect = onSelectBoard,
                            )
                            MarketFilterRow(
                                selectedPrimary = selectedPrimary,
                                selectedBoard = selectedBoard,
                                countLabel = countLabel,
                                feedSnapshot = feedSnapshot,
                            )
                        }
                    }
                }
                if (signalSpotlights.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(14.dp))
                    }
                    item {
                        MarketSignalStrip(
                            spotlights = signalSpotlights,
                            quotes = signalQuotes,
                            onOpenQuote = onOpenQuote,
                        )
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(14.dp))
                }
                item {
                    body()
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
                .height(220.dp)
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(MarketTopWash, MarketPageBackground),
                    ),
                ),
        )
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = 188.dp, y = (-34).dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(MarketInfraGlow, Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .offset(x = (-28).dp, y = 182.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(MarketSettlementGlow, Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 232.dp, y = 316.dp)
                .background(
                    brush = Brush.radialGradient(
                        colors = listOf(MarketFinanceGlow, Color.Transparent),
                    ),
                    shape = CircleShape,
                ),
        )
        content()
    }
}

@Composable
private fun MarketCompactHeader(
    query: String,
    onQueryChange: (String) -> Unit,
    selectedPrimary: MarketPrimarySection,
    selectedBoard: MarketBoard,
    countLabel: String,
    feedSnapshot: MarketFeedSnapshot,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.Top,
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = "MARKET CONTROL PLANE",
                    style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.2.sp),
                    color = MarketInfra.accent,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "市场监控总览",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        color = MarketTextPrimary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.6).sp,
                    ),
                )
                Text(
                    text = "${selectedPrimary.label} / ${selectedBoard.label} 控制台",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MarketTextSecondary,
                )
            }
            StatusTag(
                text = feedSnapshot.statusLabel(),
                type = feedSnapshot.overallState.toStatusType(),
            )
        }

        MarketConsoleSurface(
            layer = ControlPlaneLayer.Level3,
            accentWash = MarketInfra.accent.copy(alpha = 0.05f),
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
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
                                        text = "搜索标的 / 标签 / 监控焦点",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MarketTextMuted,
                                    )
                                }
                                innerTextField()
                            }
                        },
                    )
                    Surface(
                        modifier = Modifier.clickable { },
                        shape = CircleShape,
                        color = MarketLayer1.container,
                        border = BorderStroke(1.dp, MarketLine),
                    ) {
                        Box(
                            modifier = Modifier.padding(9.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = Icons.Default.Tune,
                                contentDescription = null,
                                tint = MarketTextSecondary,
                            )
                        }
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MarketIntentBadge(
                        text = "$countLabel feeds",
                        intent = ControlPlaneIntent.Infra,
                    )
                    MarketIntentBadge(
                        text = "${feedSnapshot.okCount} stable",
                        intent = ControlPlaneIntent.Settlement,
                    )
                    MarketIntentBadge(
                        text = selectedBoard.columnLabel,
                        intent = ControlPlaneIntent.Finance,
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
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        labels.forEachIndexed { index, label ->
            MarketSelectionChip(
                label = label,
                selected = index == selectedIndex,
                intent = ControlPlaneIntent.Infra,
                onClick = { onSelect(index) },
            )
        }
    }
}

@Composable
private fun MarketAnnouncementBanner(
    spotlight: MarketSpotlight,
    selectedPrimary: MarketPrimarySection,
    selectedBoard: MarketBoard,
    countLabel: String,
    visibleQuotes: List<MarketQuote>,
    feedSnapshot: MarketFeedSnapshot,
) {
    val focusPrice = spotlight.primaryValue.takeUnless { it == "--" }
        ?: visibleQuotes.firstOrNull()?.lastPrice
        ?: "--"
    val focusSupport = spotlight.secondaryValue.takeUnless { it == "--" }
        ?: visibleQuotes.firstOrNull()?.volume24h
        ?: "--"

    MarketConsoleSurface(
        layer = ControlPlaneLayer.Level2,
        accentWash = MarketInfra.accent.copy(alpha = 0.04f),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 18.dp, vertical = 18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Top,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = spotlight.eyebrow,
                        style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                        color = MarketInfra.accent,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = spotlight.title,
                        style = MaterialTheme.typography.headlineSmall.copy(
                            color = MarketTextPrimary,
                            fontWeight = FontWeight.Black,
                            letterSpacing = (-0.4).sp,
                        ),
                    )
                    Text(
                        text = spotlight.subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MarketTextSecondary,
                    )
                }
                StatusTag(
                    text = feedSnapshot.summaryLabel(),
                    type = feedSnapshot.overallState.toStatusType(),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                MarketSummaryMetric(
                    modifier = Modifier.weight(1f),
                    title = "Scope",
                    value = countLabel,
                    supporting = "${selectedPrimary.label} / ${selectedBoard.label}",
                    intent = ControlPlaneIntent.Infra,
                )
                MarketSummaryMetric(
                    modifier = Modifier.weight(1f),
                    title = "Settlement",
                    value = "${feedSnapshot.okCount}",
                    supporting = "${feedSnapshot.warnCount} watch / ${feedSnapshot.criticalCount} critical",
                    intent = ControlPlaneIntent.Settlement,
                )
                MarketSummaryMetric(
                    modifier = Modifier.weight(1f),
                    title = "Pricing",
                    value = focusPrice,
                    supporting = focusSupport,
                    intent = ControlPlaneIntent.Finance,
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
            MarketSelectionChip(
                label = label,
                selected = index == selectedIndex,
                intent = ControlPlaneIntent.Finance,
                onClick = { onSelect(index) },
            )
        }
    }
}

@Composable
private fun MarketFilterRow(
    selectedPrimary: MarketPrimarySection,
    selectedBoard: MarketBoard,
    countLabel: String,
    feedSnapshot: MarketFeedSnapshot,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MarketFilterChip(
                text = when (selectedPrimary) {
                    MarketPrimarySection.TOKEN -> "全市场"
                    MarketPrimarySection.CONTRACT -> "USDT"
                    MarketPrimarySection.STOCK -> "美股"
                },
                intent = ControlPlaneIntent.Infra,
            )
            MarketFilterChip(
                text = selectedBoard.label,
                intent = ControlPlaneIntent.Finance,
            )
            MarketFilterChip(
                text = "24H",
                intent = ControlPlaneIntent.Neutral,
            )
        }
        StatusTag(
            text = "${countLabel.ifBlank { "--" }} instruments",
            type = feedSnapshot.overallState.toStatusType(),
        )
    }
}

@Composable
private fun MarketFilterChip(
    text: String,
    intent: ControlPlaneIntent,
) {
    MarketIntentBadge(
        text = text,
        intent = intent,
    )
}

@Composable
private fun MarketSignalStrip(
    spotlights: List<MarketSpotlight>,
    quotes: List<MarketQuote>,
    onOpenQuote: (MarketQuote) -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MarketSectionHeader(
            eyebrow = "Evidence",
            title = "信号窗口",
            detail = "${spotlights.size} active",
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            spotlights.forEach { spotlight ->
                val relatedQuote = quotes.firstOrNull {
                    it.instrumentId == spotlight.instrumentId || it.symbol == spotlight.symbol
                }
                val spotlightState = spotlight.toAuditState()

                MarketConsoleSurface(
                    modifier = Modifier
                        .width(208.dp)
                        .clickable(enabled = relatedQuote != null) {
                            relatedQuote?.let(onOpenQuote)
                        },
                    layer = ControlPlaneLayer.Level2,
                    accentWash = ControlPlaneTokens.audit(spotlightState).emphasis,
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 14.dp, vertical = 14.dp)
                            .animateContentSize(
                                animationSpec = tween(
                                    durationMillis = ControlPlaneTokens.Motion.emphasis.durationMillis,
                                    easing = ControlPlaneTokens.Motion.emphasis.easing,
                                ),
                            ),
                        verticalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Top,
                        ) {
                            Text(
                                text = spotlight.eyebrow,
                                style = MaterialTheme.typography.labelMedium,
                                color = MarketTextSecondary,
                                fontWeight = FontWeight.SemiBold,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis,
                            )
                            StatusTag(
                                text = spotlightState.shortLabel(),
                                type = spotlightState.toStatusType(),
                            )
                        }
                        Text(
                            text = spotlight.title,
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = MarketTextPrimary,
                                fontWeight = FontWeight.Black,
                                letterSpacing = (-0.3).sp,
                            ),
                        )
                        Text(
                            text = spotlight.subtitle,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MarketTextSecondary,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            MarketMetricChip(
                                label = "Signal",
                                value = spotlight.primaryValue,
                                intent = ControlPlaneIntent.Finance,
                            )
                            MarketMetricChip(
                                label = "Flow",
                                value = spotlight.secondaryValue,
                                intent = ControlPlaneIntent.Infra,
                            )
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
    board: MarketBoard,
    onOpenQuote: (MarketQuote) -> Unit,
) {
    MarketConsoleSurface(
        layer = ControlPlaneLayer.Level1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
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
}

@Composable
private fun MarketQuoteLoadingBoard(
    board: MarketBoard,
    rowCount: Int = 6,
) {
    MarketConsoleSurface(
        layer = ControlPlaneLayer.Level1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            MarketQuoteHeader(board = board)
            repeat(rowCount) { index ->
                MarketQuoteLoadingRow()
                if (index != rowCount - 1) {
                    HorizontalDivider(color = MarketLine, thickness = 0.8.dp)
                }
            }
        }
    }
}

@Composable
private fun MarketQuoteEmptyBoard(
    board: MarketBoard,
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: () -> Unit = {},
) {
    MarketConsoleSurface(
        layer = ControlPlaneLayer.Level1,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            MarketQuoteHeader(board = board)
            Spacer(modifier = Modifier.height(14.dp))
            MarketInlineStateCard(
                title = title,
                subtitle = subtitle,
                actionText = actionText,
                onAction = onAction,
            )
        }
    }
}

@Composable
private fun MarketQuoteHeader(
    board: MarketBoard,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        MarketSectionHeader(
            eyebrow = "Instrument Matrix",
            title = board.label,
            detail = board.columnLabel,
        )
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
private fun MarketQuoteLoadingRow() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MarketSkeletonStrong),
                )
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    MarketPlaceholderLine(
                        modifier = Modifier
                            .width(104.dp)
                            .height(20.dp),
                    )
                    MarketPlaceholderLine(
                        modifier = Modifier
                            .width(148.dp)
                            .height(14.dp),
                    )
                }
            }
            Column(
                modifier = Modifier
                    .width(112.dp)
                    .padding(start = 12.dp),
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                MarketPlaceholderLine(
                    modifier = Modifier
                        .width(92.dp)
                        .height(20.dp),
                )
                MarketPlaceholderLine(
                    modifier = Modifier
                        .width(74.dp)
                        .height(14.dp),
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            MarketPlaceholderLine(
                modifier = Modifier
                    .width(88.dp)
                    .height(28.dp),
                radius = 14.dp,
            )
            Spacer(modifier = Modifier.weight(1f))
            MarketPlaceholderLine(
                modifier = Modifier
                    .width(92.dp)
                    .height(40.dp),
                radius = 16.dp,
            )
            MarketPlaceholderLine(
                modifier = Modifier
                    .width(92.dp)
                    .height(40.dp),
                radius = 16.dp,
            )
        }
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
    val auditState = remember(quote) { quote.toAuditState() }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            MarketSymbolAvatar(symbol = quote.symbol)
            Column(
                modifier = Modifier.weight(1f),
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
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                Text(
                    text = quote.lastPrice,
                    style = MaterialTheme.typography.titleLarge.copy(
                        color = MarketTextPrimary,
                        fontWeight = FontWeight.Black,
                        letterSpacing = (-0.4).sp,
                    ),
                    maxLines = 1,
                )
                Text(
                    text = quote.changeAmount,
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (quote.changeRateValue < 0f) MarketNegative else MarketTextSecondary,
                    maxLines = 1,
                )
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            StatusTag(
                text = auditState.shortLabel(),
                type = auditState.toStatusType(),
            )
            Spacer(modifier = Modifier.weight(1f))
            MarketMetricChip(
                label = board.columnLabel,
                value = board.primaryMetric(quote),
                intent = board.primaryMetricIntent(),
                emphasize = board.primaryMetricColor(quote),
            )
            MarketMetricChip(
                label = "Context",
                value = board.secondaryMetric(quote),
                intent = ControlPlaneIntent.Infra,
            )
        }
    }
}

@Composable
private fun MarketInlineStateCard(
    title: String,
    subtitle: String,
    actionText: String? = null,
    onAction: () -> Unit = {},
) {
    val state = if (actionText == null) AuditState.Unknown else AuditState.Warn
    val palette = ControlPlaneTokens.audit(state)

    Surface(
        shape = RoundedCornerShape(20.dp),
        color = palette.container,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MarketTextPrimary,
                    fontWeight = FontWeight.Bold,
                )
                StatusTag(
                    text = state.shortLabel(),
                    type = state.toStatusType(),
                )
            }
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodyMedium,
                color = MarketTextSecondary,
            )
            if (actionText != null) {
                Text(
                    text = actionText,
                    modifier = Modifier.clickable(onClick = onAction),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MarketInfra.accent,
                    fontWeight = FontWeight.SemiBold,
                )
            }
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
        shape = RoundedCornerShape(16.dp),
        color = palette.first,
        border = BorderStroke(1.dp, palette.first.copy(alpha = 0.7f)),
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
private fun MarketPlaceholderLine(
    modifier: Modifier,
    radius: Dp = 999.dp,
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(radius))
            .background(MarketSkeleton),
    )
}

@Composable
private fun MarketInlineMarker(tag: MarketTag) {
    val intent = when (tag.tone) {
        MarketTagTone.ACCENT -> ControlPlaneIntent.Finance
        MarketTagTone.POSITIVE -> ControlPlaneIntent.Settlement
        MarketTagTone.NEGATIVE -> ControlPlaneIntent.Neutral
        MarketTagTone.NEUTRAL -> ControlPlaneIntent.Infra
    }
    MarketIntentBadge(
        text = tag.label,
        intent = intent,
        compact = true,
    )
}

@Composable
private fun MarketConsoleSurface(
    modifier: Modifier = Modifier,
    layer: ControlPlaneLayer,
    accentWash: Color? = null,
    content: @Composable BoxScope.() -> Unit,
) {
    val palette = ControlPlaneTokens.layer(layer)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(24.dp),
        color = palette.container,
        contentColor = MarketTextPrimary,
        border = BorderStroke(1.dp, palette.outline),
        shadowElevation = palette.shadowElevation,
        tonalElevation = palette.tonalElevation,
    ) {
        Box(
            modifier = Modifier.fillMaxWidth(),
        ) {
            if (accentWash != null) {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(accentWash, Color.Transparent),
                            ),
                        ),
                )
            }
            content()
        }
    }
}

@Composable
private fun MarketSectionHeader(
    eyebrow: String,
    title: String,
    detail: String,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = eyebrow,
                style = MaterialTheme.typography.labelMedium.copy(letterSpacing = 1.sp),
                color = MarketTextSecondary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge.copy(
                    color = MarketTextPrimary,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                ),
            )
        }
        Text(
            text = detail,
            style = MaterialTheme.typography.labelLarge,
            color = MarketTextMuted,
            fontWeight = FontWeight.Medium,
        )
    }
}

@Composable
private fun MarketSummaryMetric(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    supporting: String,
    intent: ControlPlaneIntent,
) {
    val palette = ControlPlaneTokens.intent(intent)

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(18.dp),
        color = palette.container,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium,
                color = palette.accent,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium.copy(
                    color = MarketTextPrimary,
                    fontWeight = FontWeight.Black,
                    letterSpacing = (-0.2).sp,
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            Text(
                text = supporting,
                style = MaterialTheme.typography.bodySmall,
                color = MarketTextSecondary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun MarketSelectionChip(
    label: String,
    selected: Boolean,
    intent: ControlPlaneIntent,
    onClick: () -> Unit,
) {
    val intentPalette = ControlPlaneTokens.intent(intent)
    val layerPalette = ControlPlaneTokens.layer(ControlPlaneLayer.Level1)
    val containerColor by animateColorAsState(
        targetValue = if (selected) intentPalette.container else layerPalette.container,
        animationSpec = tween(
            durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
            easing = ControlPlaneTokens.Motion.stateChange.easing,
        ),
        label = "marketSelectionContainer",
    )
    val borderColor by animateColorAsState(
        targetValue = if (selected) intentPalette.border else layerPalette.outline,
        animationSpec = tween(
            durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
            easing = ControlPlaneTokens.Motion.stateChange.easing,
        ),
        label = "marketSelectionBorder",
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) MarketTextPrimary else MarketTextSecondary,
        animationSpec = tween(
            durationMillis = ControlPlaneTokens.Motion.stateChange.durationMillis,
            easing = ControlPlaneTokens.Motion.stateChange.easing,
        ),
        label = "marketSelectionText",
    )

    Surface(
        modifier = Modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = containerColor,
        border = BorderStroke(1.dp, borderColor),
        shadowElevation = if (selected) 4.dp else 0.dp,
    ) {
        Text(
            text = label,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp),
            style = MaterialTheme.typography.titleSmall,
            color = textColor,
            fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
        )
    }
}

@Composable
private fun MarketIntentBadge(
    text: String,
    intent: ControlPlaneIntent,
    compact: Boolean = false,
) {
    val palette = ControlPlaneTokens.intent(intent)

    Surface(
        shape = RoundedCornerShape(if (compact) 10.dp else 14.dp),
        color = palette.container,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(
                horizontal = if (compact) 8.dp else 10.dp,
                vertical = if (compact) 4.dp else 6.dp,
            ),
            style = if (compact) MaterialTheme.typography.labelSmall else MaterialTheme.typography.labelLarge,
            color = if (intent == ControlPlaneIntent.Neutral) MarketTextSecondary else palette.accent,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
        )
    }
}

@Composable
private fun MarketMetricChip(
    label: String,
    value: String,
    intent: ControlPlaneIntent,
    emphasize: Color? = null,
) {
    val palette = ControlPlaneTokens.intent(intent)

    Surface(
        shape = RoundedCornerShape(16.dp),
        color = palette.container,
        border = BorderStroke(1.dp, palette.border),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(2.dp),
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = MarketTextMuted,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
            Text(
                text = value,
                style = MaterialTheme.typography.labelLarge,
                color = emphasize ?: if (intent == ControlPlaneIntent.Neutral) MarketTextPrimary else palette.accent,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

private data class MarketFeedSnapshot(
    val overallState: AuditState,
    val okCount: Int,
    val warnCount: Int,
    val criticalCount: Int,
    val unknownCount: Int,
)

private fun List<MarketQuote>.toFeedSnapshot(): MarketFeedSnapshot {
    if (isEmpty()) {
        return MarketFeedSnapshot(
            overallState = AuditState.Unknown,
            okCount = 0,
            warnCount = 0,
            criticalCount = 0,
            unknownCount = 0,
        )
    }

    var okCount = 0
    var warnCount = 0
    var criticalCount = 0
    var unknownCount = 0

    forEach { quote ->
        when (quote.toAuditState()) {
            AuditState.Ok -> okCount += 1
            AuditState.Warn -> warnCount += 1
            AuditState.Critical -> criticalCount += 1
            AuditState.Unknown -> unknownCount += 1
        }
    }

    val overallState = when {
        criticalCount > 0 -> AuditState.Critical
        warnCount > 0 -> AuditState.Warn
        okCount > 0 -> AuditState.Ok
        else -> AuditState.Unknown
    }

    return MarketFeedSnapshot(
        overallState = overallState,
        okCount = okCount,
        warnCount = warnCount,
        criticalCount = criticalCount,
        unknownCount = unknownCount,
    )
}

private fun MarketFeedSnapshot.statusLabel(): String = when (overallState) {
    AuditState.Ok -> "报价稳定"
    AuditState.Warn -> "需要复核"
    AuditState.Critical -> "异常波动"
    AuditState.Unknown -> "等待同步"
}

private fun MarketFeedSnapshot.summaryLabel(): String = when {
    criticalCount > 0 -> "$criticalCount critical"
    warnCount > 0 -> "$warnCount watch"
    okCount > 0 -> "$okCount healthy"
    else -> "syncing"
}

private fun MarketQuote.toAuditState(): AuditState {
    return when {
        lastPrice == "--" || changePercent == "--" -> AuditState.Unknown
        changeRateValue <= -5f -> AuditState.Critical
        changeRateValue < 0f -> AuditState.Warn
        else -> AuditState.Ok
    }
}

private fun MarketSpotlight.toAuditState(): AuditState {
    return when {
        primaryValue == "--" -> AuditState.Unknown
        primaryValue.trim().startsWith("-") -> AuditState.Warn
        else -> AuditState.Ok
    }
}

private fun AuditState.toStatusType(): StatusType = when (this) {
    AuditState.Ok -> StatusType.OK
    AuditState.Warn -> StatusType.WARN
    AuditState.Critical -> StatusType.CRITICAL
    AuditState.Unknown -> StatusType.UNKNOWN
}

private fun AuditState.shortLabel(): String = when (this) {
    AuditState.Ok -> "稳定"
    AuditState.Warn -> "观察"
    AuditState.Critical -> "异常"
    AuditState.Unknown -> "待校验"
}

private fun MarketBoard.primaryMetricIntent(): ControlPlaneIntent = when (this) {
    MarketBoard.NEW -> ControlPlaneIntent.Finance
    MarketBoard.VOLUME -> ControlPlaneIntent.Infra
    MarketBoard.HOT,
    MarketBoard.ALL,
    MarketBoard.FAVORITES,
    MarketBoard.GAINERS,
    -> ControlPlaneIntent.Settlement
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
    MarketInfra.container to MarketInfra.accent,
    MarketSettlement.container to MarketSettlement.accent,
    MarketFinance.container to MarketFinance.accent,
    MarketLayer2.container to MarketTextPrimary,
    MarketLayer3.container to MarketTextSecondary,
    MarketCritical.container to MarketCritical.accent,
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
