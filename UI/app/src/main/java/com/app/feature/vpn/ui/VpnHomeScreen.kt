package com.app.feature.vpn.ui

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CompareArrows
import androidx.compose.material.icons.automirrored.outlined.HelpOutline
import androidx.compose.material.icons.automirrored.outlined.ShowChart
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Apps
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.CardGiftcard
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.CropFree
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.GridView
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.NotificationsNone
import androidx.compose.material.icons.outlined.PersonAddAlt1
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material.icons.outlined.PowerSettingsNew
import androidx.compose.material.icons.outlined.Redeem
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.SouthWest
import androidx.compose.material.icons.outlined.Stars
import androidx.compose.material.icons.outlined.SwapHoriz
import androidx.compose.material.icons.outlined.VerifiedUser
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Badge
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.widgets.TokenIcon
import com.app.core.theme.AppWhite
import com.app.core.theme.BluePrimary
import com.app.core.theme.BlueSecondary
import com.app.core.theme.BorderSubtle
import com.app.core.theme.GlowBlue
import com.app.core.theme.GlowCyan
import com.app.core.theme.GlowPurple
import com.app.core.theme.MintPositive
import com.app.core.theme.RedNegative
import com.app.core.theme.TextPrimary
import com.app.core.theme.TextSecondary
import com.app.core.theme.TextTertiary
import com.app.core.ui.AppScaffold
import com.app.core.utils.Formatters
import com.app.data.model.MarketTicker
import com.app.data.model.TokenPricePoint
import com.app.feature.market.viewmodel.MarketUiState
import com.app.feature.market.viewmodel.MarketViewModel
import com.app.feature.vpn.viewmodel.VpnUiState
import com.app.feature.vpn.viewmodel.VpnViewModel
import com.app.feature.wallet.viewmodel.WalletUiState
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.vpncore.model.VpnNode
import com.app.vpncore.model.VpnState
import java.text.DecimalFormat
import java.util.Locale
import kotlinx.coroutines.delay

private val OverviewHeroStart = Color(0xFF0D66FF)
private val OverviewHeroCenter = Color(0xFF1547E8)
private val OverviewHeroEnd = Color(0xFF5F2BFF)
private val OverviewCardShadow = Color(0x1A173D8A)
private val OverviewSoftBlue = Color(0xFFF3F8FF)
private val OverviewSoftBorder = Color(0x183868CC)
private val OverviewDeepNavy = Color(0xFF08163A)
private val OverviewAccentCyan = Color(0xFF23E3FF)
private val OverviewAccentBlue = Color(0xFF2C7CFF)
private val OverviewAccentPurple = Color(0xFF764BFF)
private val OverviewAccentGreen = Color(0xFF1ECE7A)
private val OverviewMuted = Color(0xFF8A9BC0)

private val MarketTabTitles = listOf("自选", "热门", "涨幅榜")
private const val OverviewDesignWidthPx = 943f

private data class OverviewLayoutMetrics(
    val unit: Float,
    val contentHorizontalPadding: Dp,
    val sectionGap: Dp,
    val cardRadius: Dp,
    val headerLogoSize: Dp,
    val headerButtonSize: Dp,
    val headerIconSize: Dp,
    val heroHeight: Dp,
    val heroPaddingHorizontal: Dp,
    val heroPaddingVertical: Dp,
    val heroChartWidth: Dp,
    val heroChartHeight: Dp,
    val heroActionHeight: Dp,
    val vpnHeight: Dp,
    val vpnPaddingHorizontal: Dp,
    val vpnPaddingVertical: Dp,
    val vpnMapHeight: Dp,
    val powerOrbSize: Dp,
    val powerInnerSize: Dp,
    val marketPaddingHorizontal: Dp,
    val marketCardHeight: Dp,
    val quickPaddingHorizontal: Dp,
    val quickItemHeight: Dp,
    val bannerHeight: Dp,
    val bannerArtworkWidth: Dp,
    val bannerArtworkHeight: Dp,
    val bottomBarBottomPadding: Dp,
)

private fun overviewLayoutMetrics(screenWidth: Dp, bottomInset: Dp): OverviewLayoutMetrics {
    val unit = (screenWidth.value / OverviewDesignWidthPx).coerceIn(0.34f, 0.56f)
    fun dp(px: Float): Dp = (px * unit).dp

    return OverviewLayoutMetrics(
        unit = unit,
        contentHorizontalPadding = dp(37f),
        sectionGap = dp(20f).coerceAtLeast(7.dp),
        cardRadius = dp(34f),
        headerLogoSize = dp(58f),
        headerButtonSize = dp(58f),
        headerIconSize = dp(34f),
        heroHeight = dp(374f),
        heroPaddingHorizontal = dp(38f),
        heroPaddingVertical = dp(30f),
        heroChartWidth = dp(390f),
        heroChartHeight = dp(168f),
        heroActionHeight = dp(68f).coerceAtLeast(28.dp),
        vpnHeight = dp(360f),
        vpnPaddingHorizontal = dp(32f),
        vpnPaddingVertical = dp(26f),
        vpnMapHeight = dp(180f),
        powerOrbSize = dp(188f),
        powerInnerSize = dp(118f),
        marketPaddingHorizontal = dp(28f),
        marketCardHeight = dp(282f),
        quickPaddingHorizontal = dp(30f),
        quickItemHeight = dp(78f).coerceAtLeast(32.dp),
        bannerHeight = dp(171f),
        bannerArtworkWidth = dp(300f),
        bannerArtworkHeight = dp(122f),
        bottomBarBottomPadding = bottomInset + dp(10f),
    )
}

private fun OverviewLayoutMetrics.dp(px: Float): Dp = (px * unit).dp

private fun OverviewLayoutMetrics.sp(
    px: Float,
    min: Float = 9f,
    max: Float = 28f,
): TextUnit = (px * unit).coerceIn(min, max).sp

private data class HeroAction(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
)

private data class FeatureShortcut(
    val label: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val badgeText: String? = null,
    val dot: Boolean = false,
)

private data class BottomBarAction(
    val label: String,
    val icon: ImageVector,
    val active: Boolean = false,
    val onClick: () -> Unit,
)

@Composable
fun VpnHomeScreen(
    vpnViewModel: VpnViewModel = viewModel(),
    walletViewModel: WalletViewModel = viewModel(),
    marketViewModel: MarketViewModel = viewModel(),
    onOpenNodes: () -> Unit = {},
    onOpenPlans: () -> Unit = {},
    onOpenSubscription: () -> Unit = {},
    onOpenOrders: () -> Unit = {},
    onOpenWalletHome: () -> Unit = {},
    onOpenMarket: () -> Unit = {},
    onOpenInvite: () -> Unit = {},
    onOpenProfile: () -> Unit = {},
    onOpenLedger: () -> Unit = {},
    onOpenSecurity: () -> Unit = {},
    onOpenAssets: () -> Unit = {},
    onOpenTicker: (String) -> Unit = {},
) {
    val vpnState by vpnViewModel.uiState.collectAsState()
    val walletState by walletViewModel.uiState.collectAsState()
    val marketState by marketViewModel.uiState.collectAsState()
    val bottomInset = WindowInsets.safeDrawing.asPaddingValues().calculateBottomPadding()

    val totalAssetsUsd = walletState.profile?.totalAssetsUsd
        ?: walletState.assets.sumOf { it.balance * it.priceUsd }
    val dailyPnlUsd = walletState.assets.sumOf { asset ->
        asset.balance * asset.priceUsd * (asset.change24h / 100.0)
    }
    val dailyPnlPct = if (totalAssetsUsd == 0.0) 0.0 else (dailyPnlUsd / totalAssetsUsd) * 100.0
    val btcTicker = marketState.overview.firstOrNull { it.symbol.equals("BTC", true) }
    val btcEquivalent = btcTicker?.priceUsd?.takeIf { it > 0.0 }?.let { totalAssetsUsd / it }
    val primaryTicker = remember(walletState.assets, marketState.overview) {
        val bestHeld = walletState.assets
            .map { asset -> asset.symbol to (asset.balance * asset.priceUsd) }
            .maxByOrNull { it.second }
            ?.first
        marketState.overview.firstOrNull { it.symbol.equals(bestHeld, true) }
            ?: marketState.overview.firstOrNull()
    }
    val heroSeries = rememberPriceSeries(marketViewModel, primaryTicker?.symbol)
    val heroActions = remember(onOpenAssets, onOpenWalletHome) {
        listOf(
            HeroAction("充值", Icons.Outlined.ArrowDownward, onOpenAssets),
            HeroAction("转账", Icons.Outlined.SwapHoriz, onOpenWalletHome),
            HeroAction("钱包", Icons.Outlined.AccountBalanceWallet, onOpenWalletHome),
        )
    }
    val shortcuts = remember(
        onOpenWalletHome,
        onOpenInvite,
        onOpenPlans,
        onOpenLedger,
        onOpenSubscription,
        onOpenOrders,
        onOpenProfile,
        onOpenSecurity,
    ) {
        listOf(
            FeatureShortcut("加密支付", Icons.Outlined.AccountBalanceWallet, onOpenWalletHome, badgeText = "NEW"),
            FeatureShortcut("邀请好友", Icons.Outlined.PersonAddAlt1, onOpenInvite, dot = true),
            FeatureShortcut("会员中心", Icons.Outlined.Stars, onOpenPlans),
            FeatureShortcut("收益中心", Icons.Outlined.SouthWest, onOpenLedger),
            FeatureShortcut("节点订阅", Icons.Outlined.Security, onOpenSubscription),
            FeatureShortcut("代理中心", Icons.Outlined.GridView, onOpenInvite),
            FeatureShortcut("帮助中心", Icons.AutoMirrored.Outlined.HelpOutline, onOpenProfile),
            FeatureShortcut("活动中心", Icons.Outlined.CardGiftcard, onOpenOrders),
            FeatureShortcut("安全检测", Icons.Outlined.VerifiedUser, onOpenSecurity),
            FeatureShortcut("更多功能", Icons.Outlined.Apps, onOpenProfile),
        )
    }
    val bottomBarActions = remember(onOpenMarket, onOpenWalletHome, onOpenInvite, onOpenProfile) {
        listOf(
            BottomBarAction("首页", Icons.Outlined.Home, active = true, onClick = {}),
            BottomBarAction("行情", Icons.AutoMirrored.Outlined.ShowChart, onClick = onOpenMarket),
            BottomBarAction("交易", Icons.AutoMirrored.Outlined.CompareArrows, onClick = onOpenWalletHome),
            BottomBarAction("发现", Icons.Outlined.Explore, onClick = onOpenInvite),
            BottomBarAction("我的", Icons.Outlined.PersonOutline, onClick = onOpenProfile),
        )
    }

    AppScaffold(
        title = "",
        showTopBar = false,
        useProductionMotion = false,
    ) { padding ->
        BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
            val metrics = remember(maxWidth, bottomInset) {
                overviewLayoutMetrics(maxWidth, bottomInset)
            }
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(
                    start = metrics.contentHorizontalPadding,
                    end = metrics.contentHorizontalPadding,
                    top = padding.calculateTopPadding(),
                    bottom = metrics.bottomBarBottomPadding + metrics.dp(260f),
                ),
                verticalArrangement = Arrangement.spacedBy(metrics.sectionGap),
            ) {
                item {
                    OverviewHeader(
                        metrics = metrics,
                        onSearch = onOpenMarket,
                        onNotifications = onOpenOrders,
                        onScan = onOpenSecurity,
                    )
                }
                item {
                    PortfolioHeroCard(
                        metrics = metrics,
                        totalAssetsUsd = totalAssetsUsd,
                        btcEquivalent = btcEquivalent,
                        dailyPnlUsd = dailyPnlUsd,
                        dailyPnlPct = dailyPnlPct,
                        points = heroSeries,
                        actions = heroActions,
                    )
                }
                item {
                    VpnConnectionCard(
                        metrics = metrics,
                        uiState = vpnState,
                        connectionDuration = rememberConnectionTimer(vpnState.vpnState),
                        onOpenNodes = onOpenNodes,
                        onToggleConnection = vpnViewModel::connectOrDisconnect,
                    )
                }
                item {
                    MarketStrip(
                        metrics = metrics,
                        marketState = marketState,
                        marketViewModel = marketViewModel,
                        onOpenMarket = onOpenMarket,
                        onOpenTicker = onOpenTicker,
                    )
                }
                item {
                    QuickCenterCard(metrics = metrics, shortcuts = shortcuts)
                }
                item {
                    ExploreBanner(metrics = metrics, onExplore = onOpenWalletHome)
                }
            }

            OverviewBottomBar(
                metrics = metrics,
                actions = bottomBarActions,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(horizontal = metrics.contentHorizontalPadding)
                    .padding(bottom = metrics.bottomBarBottomPadding),
            )
        }
    }
}

@Composable
private fun OverviewHeader(
    metrics: OverviewLayoutMetrics,
    onSearch: () -> Unit,
    onNotifications: () -> Unit,
    onScan: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(metrics.dp(20f)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BrandMark(metrics)
            Text(
                text = "CryptoVPN",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = metrics.sp(46f, min = 14f, max = 22f),
                ),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
            )
        }
        Row(horizontalArrangement = Arrangement.spacedBy(metrics.dp(18f))) {
            HeaderIconButton(metrics = metrics, icon = Icons.Outlined.Search, onClick = onSearch)
            HeaderIconButton(
                metrics = metrics,
                icon = Icons.Outlined.NotificationsNone,
                onClick = onNotifications,
                badgeText = "12",
            )
            HeaderIconButton(metrics = metrics, icon = Icons.Outlined.CropFree, onClick = onScan)
        }
    }
}

@Composable
private fun BrandMark(metrics: OverviewLayoutMetrics) {
    Box(
        modifier = Modifier
            .size(metrics.headerLogoSize)
            .clip(RoundedCornerShape(metrics.dp(16f)))
            .background(
                brush = Brush.linearGradient(
                    colors = listOf(OverviewAccentBlue, OverviewAccentPurple),
                ),
            ),
            contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize().padding(metrics.dp(10f))) {
            val shape = Path().apply {
                moveTo(size.width * 0.18f, size.height * 0.18f)
                lineTo(size.width * 0.62f, size.height * 0.02f)
                lineTo(size.width * 0.88f, size.height * 0.2f)
                lineTo(size.width * 0.48f, size.height * 0.36f)
                lineTo(size.width * 0.68f, size.height * 0.5f)
                lineTo(size.width * 0.36f, size.height * 0.64f)
                lineTo(size.width * 0.36f, size.height * 0.94f)
                lineTo(size.width * 0.08f, size.height * 0.74f)
                close()
            }
            drawPath(
                path = shape,
                brush = Brush.linearGradient(
                    colors = listOf(Color.White, Color(0xB3FFFFFF)),
                    start = Offset.Zero,
                    end = Offset(size.width, size.height),
                ),
            )
        }
    }
}

@Composable
private fun HeaderIconButton(
    metrics: OverviewLayoutMetrics,
    icon: ImageVector,
    onClick: () -> Unit,
    badgeText: String? = null,
) {
    Box(contentAlignment = Alignment.TopEnd) {
        Surface(
            modifier = Modifier
                .size(metrics.headerButtonSize)
                .clickable(onClick = onClick),
            shape = CircleShape,
            color = AppWhite.copy(alpha = 0.92f),
            shadowElevation = metrics.dp(14f),
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(metrics.headerIconSize),
                )
            }
        }
        if (badgeText != null) {
            Badge(
                modifier = Modifier.padding(top = metrics.dp(2f), end = metrics.dp(1f)),
                containerColor = Color(0xFFFF4B4B),
                contentColor = Color.White,
            ) {
                Text(text = badgeText, fontSize = metrics.sp(24f, min = 8f, max = 10f), fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun PortfolioHeroCard(
    metrics: OverviewLayoutMetrics,
    totalAssetsUsd: Double,
    btcEquivalent: Double?,
    dailyPnlUsd: Double,
    dailyPnlPct: Double,
    points: List<Float>,
    actions: List<HeroAction>,
) {
    val pnlPositive = dailyPnlUsd >= 0.0
    val pnlColor = if (pnlPositive) OverviewAccentCyan else Color(0xFFFF8585)

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(metrics.heroHeight),
        shape = RoundedCornerShape(metrics.cardRadius),
        shadowElevation = metrics.dp(32f),
        color = Color.Transparent,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(OverviewHeroStart, OverviewHeroCenter, OverviewHeroEnd),
                    ),
                )
                .drawWithCache {
                    val topGlow = Brush.radialGradient(
                        colors = listOf(GlowCyan.copy(alpha = 0.22f), Color.Transparent),
                        center = Offset(size.width * 0.78f, size.height * 0.22f),
                        radius = size.width * 0.56f,
                    )
                    val sideGlow = Brush.radialGradient(
                        colors = listOf(GlowPurple.copy(alpha = 0.26f), Color.Transparent),
                        center = Offset(size.width * 0.96f, size.height * 0.62f),
                        radius = size.width * 0.62f,
                    )
                    onDrawWithContent {
                        drawRect(brush = topGlow)
                        drawRect(brush = sideGlow)
                        drawContent()
                    }
                }
                .padding(horizontal = metrics.heroPaddingHorizontal, vertical = metrics.heroPaddingVertical),
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(metrics.dp(15f)),
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(metrics.dp(14f)),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                text = "总资产估值",
                                color = Color.White,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = metrics.sp(32f, min = 11f, max = 16f),
                                ),
                                fontWeight = FontWeight.SemiBold,
                            )
                            Icon(
                                imageVector = Icons.Outlined.Visibility,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.88f),
                                modifier = Modifier.size(metrics.dp(34f)),
                            )
                        }
                        Text(
                            text = Formatters.money(totalAssetsUsd),
                            color = Color.White,
                            style = MaterialTheme.typography.headlineLarge.copy(
                                fontSize = metrics.sp(58f, min = 20f, max = 26f),
                                lineHeight = metrics.sp(62f, min = 22f, max = 28f),
                            ),
                            fontWeight = FontWeight.ExtraBold,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                        )
                        Text(
                            text = btcEquivalent?.let { "≈ ${btcFormat(it)} BTC" } ?: "≈ -- BTC",
                            color = Color.White.copy(alpha = 0.8f),
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = metrics.sp(28f, min = 10f, max = 14f),
                            ),
                            maxLines = 1,
                        )
                        Column(verticalArrangement = Arrangement.spacedBy(metrics.dp(10f))) {
                            Text(
                                text = "今日收益",
                                color = Color.White.copy(alpha = 0.92f),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = metrics.sp(30f, min = 10f, max = 15f),
                                ),
                                fontWeight = FontWeight.SemiBold,
                            )
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(metrics.dp(14f)),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Text(
                                    text = signedMoney(dailyPnlUsd),
                                    color = pnlColor,
                                    style = MaterialTheme.typography.headlineSmall.copy(
                                        fontSize = metrics.sp(40f, min = 15f, max = 20f),
                                    ),
                                    fontWeight = FontWeight.Bold,
                                    maxLines = 1,
                                )
                                Surface(
                                    color = Color.White.copy(alpha = 0.14f),
                                    shape = RoundedCornerShape(999.dp),
                                ) {
                                    Text(
                                        text = Formatters.percent(dailyPnlPct),
                                        color = pnlColor,
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontSize = metrics.sp(24f, min = 9f, max = 12f),
                                        ),
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.padding(horizontal = metrics.dp(18f), vertical = metrics.dp(8f)),
                                    )
                                }
                            }
                        }
                    }
                    Column(
                        modifier = Modifier.width(metrics.heroChartWidth),
                        verticalArrangement = Arrangement.spacedBy(metrics.dp(16f)),
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Outlined.ShowChart,
                                contentDescription = null,
                                tint = Color.White.copy(alpha = 0.86f),
                                modifier = Modifier.size(metrics.dp(30f)),
                            )
                            Spacer(modifier = Modifier.width(metrics.dp(10f)))
                            Text(
                                text = "资产分析",
                                color = Color.White.copy(alpha = 0.92f),
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = metrics.sp(30f, min = 10f, max = 15f),
                                ),
                                fontWeight = FontWeight.SemiBold,
                            )
                        }
                        OverviewHeroSparkline(
                            points = points,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(metrics.heroChartHeight),
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(metrics.dp(26f))) {
                    actions.forEach { action ->
                        HeroActionButton(
                            metrics = metrics,
                            action = action,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HeroActionButton(
    metrics: OverviewLayoutMetrics,
    action: HeroAction,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .height(metrics.heroActionHeight)
            .clickable(onClick = action.onClick),
        shape = RoundedCornerShape(metrics.dp(18f)),
        color = Color.White.copy(alpha = 0.12f),
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(
                imageVector = action.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(metrics.dp(34f)),
            )
            Spacer(modifier = Modifier.width(metrics.dp(12f)))
            Text(
                text = action.label,
                color = Color.White,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = metrics.sp(32f, min = 11f, max = 16f),
                ),
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Composable
private fun OverviewHeroSparkline(
    points: List<Float>,
    modifier: Modifier = Modifier,
) {
    val safePoints = if (points.size >= 2) points else listOf(1f, 1.3f, 1.2f, 1.5f, 1.46f, 1.72f, 1.68f, 1.9f)
    Canvas(modifier = modifier) {
        val left = 8.dp.toPx()
        val right = size.width - 6.dp.toPx()
        val top = 18.dp.toPx()
        val bottom = size.height - 24.dp.toPx()
        val width = right - left
        val height = bottom - top
        val min = safePoints.minOrNull() ?: 0f
        val max = safePoints.maxOrNull() ?: 1f
        val range = (max - min).takeIf { it > 0f } ?: 1f
        val stepX = width / (safePoints.lastIndex).coerceAtLeast(1)

        repeat(3) { index ->
            val y = top + (height / 2f) * index
            drawLine(
                color = Color.White.copy(alpha = if (index == 2) 0.16f else 0.08f),
                start = Offset(left, y),
                end = Offset(right, y),
                strokeWidth = if (index == 2) 2f else 1f,
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f)),
            )
        }

        val line = Path()
        val fill = Path()
        safePoints.forEachIndexed { index, point ->
            val x = left + (stepX * index)
            val y = bottom - (((point - min) / range) * height)
            if (index == 0) {
                line.moveTo(x, y)
                fill.moveTo(x, bottom)
                fill.lineTo(x, y)
            } else {
                line.lineTo(x, y)
                fill.lineTo(x, y)
            }
        }
        fill.lineTo(right, bottom)
        fill.close()

        drawPath(
            path = fill,
            brush = Brush.verticalGradient(
                colors = listOf(
                    OverviewAccentCyan.copy(alpha = 0.32f),
                    Color.Transparent,
                ),
                startY = top,
                endY = bottom,
            ),
        )
        drawPath(
            path = line,
            color = OverviewAccentCyan.copy(alpha = 0.2f),
            style = Stroke(width = 10f, cap = StrokeCap.Round),
        )
        drawPath(
            path = line,
            brush = Brush.horizontalGradient(
                colors = listOf(OverviewAccentCyan, Color(0xFF66E9FF)),
            ),
            style = Stroke(width = 5f, cap = StrokeCap.Round),
        )

        safePoints.forEachIndexed { index, point ->
            val x = left + (stepX * index)
            val y = bottom - (((point - min) / range) * height)
            drawCircle(
                color = OverviewAccentCyan,
                radius = if (index == safePoints.lastIndex) 8f else 5f,
                center = Offset(x, y),
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.9f),
                radius = if (index == safePoints.lastIndex) 4f else 2.6f,
                center = Offset(x, y),
            )
        }
    }
}

@Composable
private fun VpnConnectionCard(
    metrics: OverviewLayoutMetrics,
    uiState: VpnUiState,
    connectionDuration: String,
    onOpenNodes: () -> Unit,
    onToggleConnection: () -> Unit,
) {
    val status = remember(uiState.vpnState) { vpnStatusMeta(uiState.vpnState) }
    val node = uiState.selectedNode ?: uiState.nodes.firstOrNull()

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(metrics.vpnHeight),
        shape = RoundedCornerShape(metrics.cardRadius),
        color = Color.White.copy(alpha = 0.95f),
        shadowElevation = metrics.dp(32f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(Color.White, OverviewSoftBlue),
                    ),
                )
                .padding(horizontal = metrics.vpnPaddingHorizontal, vertical = metrics.vpnPaddingVertical),
        ) {
            DottedWorldBackdrop(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(metrics.vpnMapHeight)
                    .align(Alignment.TopCenter)
                    .padding(top = metrics.dp(28f), start = metrics.dp(88f), end = metrics.dp(76f)),
            )
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(metrics.dp(16f)),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Surface(
                            shape = RoundedCornerShape(999.dp),
                            color = status.containerColor,
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = metrics.dp(14f), vertical = metrics.dp(6f)),
                                horizontalArrangement = Arrangement.spacedBy(metrics.dp(10f)),
                                verticalAlignment = Alignment.CenterVertically,
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(metrics.dp(16f))
                                        .clip(CircleShape)
                                        .background(status.tint),
                                )
                                Text(
                                    text = "VPN ${status.label}",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontSize = metrics.sp(32f, min = 11f, max = 15f),
                                    ),
                                    color = status.tint,
                                    fontWeight = FontWeight.Bold,
                                )
                            }
                        }
                    }
                    HeaderLiteAction(
                        metrics = metrics,
                        icon = Icons.Outlined.ChevronRight,
                        onClick = onOpenNodes,
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(metrics.dp(12f)),
                    ) {
                        Text(
                            text = "当前节点",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = metrics.sp(28f, min = 10f, max = 14f),
                            ),
                            color = TextSecondary,
                        )
                        NodeIdentity(metrics = metrics, node = node)
                        Surface(
                            modifier = Modifier.clickable(onClick = onOpenNodes),
                            shape = RoundedCornerShape(999.dp),
                            color = Color(0xFFF1F6FF),
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4F7CFF)),
                        ) {
                            Text(
                                text = "切换",
                                color = BluePrimary,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontSize = metrics.sp(26f, min = 10f, max = 13f),
                                ),
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(horizontal = metrics.dp(26f), vertical = metrics.dp(8f)),
                            )
                        }
                    }

                    PowerConnectionOrb(
                        metrics = metrics,
                        statusLabel = status.orbText,
                        actionLabel = status.actionLabel,
                        enabled = !status.loading,
                        onClick = onToggleConnection,
                    )

                    Column(
                        modifier = Modifier.weight(1f),
                        horizontalAlignment = Alignment.End,
                        verticalArrangement = Arrangement.spacedBy(metrics.dp(22f)),
                    ) {
                        MetricColumn(
                            metrics = metrics,
                            label = "连接时长",
                            value = connectionDuration,
                            valueColor = TextPrimary,
                        )
                        MetricColumn(
                            metrics = metrics,
                            label = "网络延迟",
                            value = node?.latencyMs?.let { "$it ms" } ?: "-- ms",
                            valueColor = OverviewAccentGreen,
                        )
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "⚡ 智能模式",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = metrics.sp(28f, min = 10f, max = 14f),
                        ),
                        color = BluePrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    Text(
                        text = "  |  全局代理",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = metrics.sp(28f, min = 10f, max = 14f),
                        ),
                        color = TextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun NodeIdentity(metrics: OverviewLayoutMetrics, node: VpnNode?) {
    if (node == null) {
        Text(
            text = "暂无节点",
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = metrics.sp(28f, min = 10f, max = 14f),
            ),
            color = TextSecondary,
        )
        return
    }

    Column(verticalArrangement = Arrangement.spacedBy(metrics.dp(8f))) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(metrics.dp(8f)),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = countryFlag(node.country),
                style = MaterialTheme.typography.titleLarge.copy(
                    fontSize = metrics.sp(32f, min = 12f, max = 16f),
                ),
            )
            Text(
                text = countryName(node.country),
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = metrics.sp(30f, min = 10f, max = 15f),
                ),
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
        Text(
            text = node.name,
            style = MaterialTheme.typography.bodyLarge.copy(
                fontSize = metrics.sp(26f, min = 10f, max = 13f),
            ),
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
    }
}

@Composable
private fun PowerConnectionOrb(
    metrics: OverviewLayoutMetrics,
    statusLabel: String,
    actionLabel: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    val transition = rememberInfiniteTransition(label = "vpn-power")
    val pulse = transition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse,
        ),
        label = "vpn-power-pulse",
    )

    Box(
        modifier = Modifier
            .size(metrics.powerOrbSize)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .blur(metrics.dp(18f))
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            OverviewAccentPurple.copy(alpha = 0.18f + pulse.value * 0.1f),
                            Color.Transparent,
                        ),
                    ),
                ),
        )
        Canvas(modifier = Modifier.fillMaxSize()) {
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(Color.White.copy(alpha = 0.98f), Color(0xFFEAF2FF)),
                ),
                radius = size.minDimension / 2f,
            )
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        Color(0xFFE9F5FF),
                        OverviewAccentCyan.copy(alpha = 0.8f),
                        OverviewAccentPurple.copy(alpha = 0.8f),
                        Color(0xFFE9F5FF),
                    ),
                ),
                radius = size.minDimension * (0.46f + (pulse.value * 0.02f)),
                style = Stroke(width = metrics.dp(14f).toPx()),
            )
            drawCircle(
                brush = Brush.sweepGradient(
                    colors = listOf(
                        OverviewAccentPurple.copy(alpha = 0.82f),
                        OverviewAccentBlue,
                        OverviewAccentCyan,
                        OverviewAccentPurple.copy(alpha = 0.82f),
                    ),
                ),
                radius = size.minDimension * 0.36f,
                style = Stroke(width = metrics.dp(20f).toPx()),
            )
        }
        Surface(
            modifier = Modifier.size(metrics.powerInnerSize),
            shape = CircleShape,
            color = Color.Transparent,
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(OverviewAccentPurple, OverviewAccentBlue),
                        ),
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(metrics.dp(8f)),
                ) {
                    Icon(
                        imageVector = Icons.Outlined.PowerSettingsNew,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(metrics.dp(42f)),
                    )
                    Text(
                        text = actionLabel,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontSize = metrics.sp(24f, min = 9f, max = 12f),
                            lineHeight = metrics.sp(28f, min = 10f, max = 13f),
                        ),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
        Text(
            text = statusLabel,
            color = Color.Transparent,
        )
    }
}

@Composable
private fun MetricColumn(
    metrics: OverviewLayoutMetrics,
    label: String,
    value: String,
    valueColor: Color,
) {
    Column(
        horizontalAlignment = Alignment.End,
        verticalArrangement = Arrangement.spacedBy(metrics.dp(8f)),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleMedium.copy(
                fontSize = metrics.sp(28f, min = 10f, max = 14f),
            ),
            color = TextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontSize = metrics.sp(34f, min = 13f, max = 17f),
            ),
            color = valueColor,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
        )
    }
}

@Composable
private fun HeaderLiteAction(
    metrics: OverviewLayoutMetrics,
    icon: ImageVector,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .size(metrics.dp(48f))
            .clickable(onClick = onClick),
        shape = CircleShape,
        color = Color(0xFFF4F8FF),
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextPrimary,
                modifier = Modifier.size(metrics.dp(28f)),
            )
        }
    }
}

@Composable
private fun DottedWorldBackdrop(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val clusters = listOf(
            listOf(0.10f to 0.40f, 0.16f to 0.32f, 0.23f to 0.38f, 0.29f to 0.30f, 0.34f to 0.36f),
            listOf(0.42f to 0.24f, 0.48f to 0.19f, 0.54f to 0.23f, 0.60f to 0.18f, 0.66f to 0.26f),
            listOf(0.68f to 0.42f, 0.74f to 0.36f, 0.80f to 0.43f, 0.87f to 0.34f, 0.92f to 0.42f),
            listOf(0.56f to 0.58f, 0.48f to 0.64f, 0.40f to 0.58f, 0.24f to 0.62f, 0.82f to 0.62f),
        )
        clusters.forEachIndexed { row, points ->
            points.forEachIndexed { index, (x, y) ->
                val radius = 3f + ((row + index) % 3) * 1.2f
                drawCircle(
                    brush = Brush.radialGradient(
                        colors = listOf(
                            if ((row + index) % 2 == 0) GlowBlue.copy(alpha = 0.34f) else GlowPurple.copy(alpha = 0.26f),
                            Color.Transparent,
                        ),
                    ),
                    radius = radius * 4.4f,
                    center = Offset(size.width * x, size.height * y),
                )
                drawCircle(
                    color = Color(0xFFB7CAFF).copy(alpha = 0.52f),
                    radius = radius,
                    center = Offset(size.width * x, size.height * y),
                )
            }
        }
    }
}

@Composable
private fun MarketStrip(
    metrics: OverviewLayoutMetrics,
    marketState: MarketUiState,
    marketViewModel: MarketViewModel,
    onOpenMarket: () -> Unit,
    onOpenTicker: (String) -> Unit,
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(1) }
    val cards = remember(selectedTab, marketState) {
        val base = when (selectedTab) {
            0 -> marketState.watchlist
            1 -> marketState.overview.sortedByDescending { it.marketCapUsd }
            else -> marketState.hotRisers.sortedByDescending { it.change24h }
        }
        val fallback = marketState.overview.filterNot { candidate ->
            base.any { it.symbol.equals(candidate.symbol, true) }
        }
        (base + fallback).distinctBy { it.symbol }.take(4)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(metrics.cardRadius),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = metrics.dp(32f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = metrics.marketPaddingHorizontal, vertical = metrics.dp(28f)),
            verticalArrangement = Arrangement.spacedBy(metrics.dp(22f)),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(metrics.dp(34f)),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "行情",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontSize = metrics.sp(34f, min = 13f, max = 18f),
                        ),
                        color = TextPrimary,
                        fontWeight = FontWeight.Bold,
                    )
                    MarketTabTitles.forEachIndexed { index, title ->
                        Text(
                            text = title,
                            color = if (selectedTab == index) TextPrimary else OverviewMuted,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = metrics.sp(28f, min = 10f, max = 14f),
                            ),
                            fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Medium,
                            modifier = Modifier.clickable { selectedTab = index },
                        )
                    }
                }
                Row(
                    modifier = Modifier.clickable(onClick = onOpenMarket),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "更多",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = metrics.sp(28f, min = 10f, max = 14f),
                        ),
                        color = OverviewMuted,
                    )
                    Icon(
                        imageVector = Icons.Outlined.ChevronRight,
                        contentDescription = null,
                        tint = OverviewMuted,
                        modifier = Modifier.size(metrics.dp(28f)),
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(metrics.dp(24f))) {
                cards.forEach { ticker ->
                    MarketTickerCard(
                        metrics = metrics,
                        ticker = ticker,
                        marketViewModel = marketViewModel,
                        onClick = { onOpenTicker(ticker.symbol) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

@Composable
private fun MarketTickerCard(
    metrics: OverviewLayoutMetrics,
    ticker: MarketTicker,
    marketViewModel: MarketViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val points = rememberPriceSeries(marketViewModel, ticker.symbol)
    val positive = ticker.change24h >= 0.0

    Surface(
        modifier = modifier
            .height(metrics.marketCardHeight)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(metrics.dp(22f)),
        color = Color(0xFFFBFDFF),
        shadowElevation = metrics.dp(12f),
        border = androidx.compose.foundation.BorderStroke(1.dp, OverviewSoftBorder),
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = metrics.dp(12f), vertical = metrics.dp(18f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(metrics.dp(10f)),
            ) {
                TokenIcon(symbol = ticker.symbol, size = metrics.dp(48f))
                Text(
                    text = "${ticker.symbol}/USDT",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = metrics.sp(24f, min = 9f, max = 12f),
                    ),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                )
                Text(
                    text = plainMoney(ticker.priceUsd),
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontSize = metrics.sp(30f, min = 11f, max = 15f),
                    ),
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(
                    text = Formatters.percent(ticker.change24h),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = metrics.sp(26f, min = 10f, max = 13f),
                    ),
                    color = if (positive) OverviewAccentGreen else RedNegative,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
            MiniSparkline(
                points = points,
                positive = positive,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(metrics.dp(48f)),
            )
        }
    }
}

@Composable
private fun QuickCenterCard(
    metrics: OverviewLayoutMetrics,
    shortcuts: List<FeatureShortcut>,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(metrics.cardRadius),
        color = Color.White.copy(alpha = 0.97f),
        shadowElevation = metrics.dp(32f),
    ) {
        Column(
            modifier = Modifier.padding(horizontal = metrics.quickPaddingHorizontal, vertical = metrics.dp(24f)),
            verticalArrangement = Arrangement.spacedBy(metrics.dp(12f)),
        ) {
            shortcuts.chunked(5).forEach { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(metrics.dp(10f))) {
                    rowItems.forEach { item ->
                        ShortcutItem(
                            metrics = metrics,
                            item = item,
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ShortcutItem(
    metrics: OverviewLayoutMetrics,
    item: FeatureShortcut,
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .height(metrics.quickItemHeight)
            .clip(RoundedCornerShape(metrics.dp(18f)))
            .clickable(onClick = item.onClick)
            .padding(top = metrics.dp(4f)),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(metrics.dp(8f)),
        ) {
            Box(
                modifier = Modifier
                    .width(metrics.dp(72f))
                    .height(metrics.dp(44f)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = null,
                    tint = TextPrimary,
                    modifier = Modifier.size(metrics.dp(34f)),
                )
                if (item.badgeText != null) {
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd),
                        shape = RoundedCornerShape(999.dp),
                        color = Color(0xFFFF5A47),
                    ) {
                        Text(
                            text = item.badgeText,
                            color = Color.White,
                            fontSize = metrics.sp(20f, min = 7f, max = 9f),
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = metrics.dp(8f), vertical = metrics.dp(2f)),
                        )
                    }
                }
                if (item.dot) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .size(metrics.dp(14f))
                            .clip(CircleShape)
                            .background(Color(0xFFFF4747)),
                    )
                }
            }
            Text(
                text = item.label,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = metrics.sp(26f, min = 10f, max = 13f),
                    lineHeight = metrics.sp(30f, min = 11f, max = 14f),
                ),
                color = TextPrimary,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun ExploreBanner(
    metrics: OverviewLayoutMetrics,
    onExplore: () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(metrics.cardRadius),
        color = Color.Transparent,
        shadowElevation = metrics.dp(34f),
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(metrics.bannerHeight)
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(OverviewDeepNavy, Color(0xFF071847), Color(0xFF141A71)),
                    ),
                )
                .padding(horizontal = metrics.dp(40f), vertical = metrics.dp(24f)),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .blur(metrics.dp(70f))
                    .background(
                        brush = Brush.radialGradient(
                            colors = listOf(GlowBlue.copy(alpha = 0.36f), Color.Transparent),
                            center = Offset(860f, 120f),
                            radius = 420f,
                        ),
                    ),
            )
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(metrics.dp(10f)),
                ) {
                    Text(
                        text = "开启你的加密之旅",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontSize = metrics.sp(34f, min = 13f, max = 18f),
                            lineHeight = metrics.sp(40f, min = 15f, max = 20f),
                        ),
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                    )
                    Text(
                        text = "安全 · 自由 · 财富",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontSize = metrics.sp(28f, min = 10f, max = 14f),
                        ),
                        color = Color.White.copy(alpha = 0.88f),
                        fontWeight = FontWeight.Medium,
                        maxLines = 1,
                    )
                    Surface(
                        modifier = Modifier
                            .padding(top = metrics.dp(8f))
                            .clickable(onClick = onExplore),
                        shape = RoundedCornerShape(999.dp),
                        color = Color.White.copy(alpha = 0.12f),
                    ) {
                        Text(
                            text = "立即探索",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontSize = metrics.sp(24f, min = 9f, max = 12f),
                            ),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = metrics.dp(26f), vertical = metrics.dp(8f)),
                        )
                    }
                }
                CryptoJourneyArtwork(
                    modifier = Modifier
                        .width(metrics.bannerArtworkWidth)
                        .height(metrics.bannerArtworkHeight),
                )
            }
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = metrics.dp(4f)),
                horizontalArrangement = Arrangement.spacedBy(metrics.dp(8f)),
            ) {
                repeat(4) { index ->
                    Box(
                        modifier = Modifier
                            .size(if (index == 1) metrics.dp(12f) else metrics.dp(10f))
                            .clip(CircleShape)
                            .background(
                                if (index == 1) Color.White else Color.White.copy(alpha = 0.36f),
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun CryptoJourneyArtwork(
    modifier: Modifier = Modifier,
) {
    Canvas(modifier = modifier) {
        val center = Offset(size.width * 0.58f, size.height * 0.54f)
        repeat(3) { index ->
            drawOval(
                color = Color.White.copy(alpha = 0.14f - index * 0.03f),
                topLeft = Offset(size.width * 0.14f, size.height * (0.18f + index * 0.1f)),
                size = Size(size.width * (0.8f - index * 0.08f), size.height * (0.5f - index * 0.06f)),
                style = Stroke(width = 2f),
            )
        }
        repeat(12) { index ->
            val orbitT = index / 12f
            val x = center.x + kotlin.math.cos(orbitT * Math.PI * 2.0).toFloat() * size.width * (0.34f + (index % 3) * 0.05f)
            val y = center.y + kotlin.math.sin(orbitT * Math.PI * 2.0).toFloat() * size.height * (0.18f + (index % 3) * 0.04f)
            val accent = when (index % 3) {
                0 -> OverviewAccentCyan
                1 -> Color(0xFFFFA94D)
                else -> Color(0xFFFFC54D)
            }
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(accent.copy(alpha = 0.9f), Color.Transparent),
                ),
                radius = 14f,
                center = Offset(x, y),
            )
            drawCircle(
                color = accent,
                radius = 5.5f,
                center = Offset(x, y),
            )
        }
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color(0xFF3159FF), Color(0xFF5BD5FF)),
            ),
            topLeft = Offset(size.width * 0.44f, size.height * 0.22f),
            size = Size(size.width * 0.24f, size.height * 0.38f),
            cornerRadius = CornerRadius(24f, 24f),
        )
        drawRoundRect(
            brush = Brush.linearGradient(
                colors = listOf(Color.White.copy(alpha = 0.96f), Color(0xFFE9F6FF)),
            ),
            topLeft = Offset(size.width * 0.495f, size.height * 0.31f),
            size = Size(size.width * 0.13f, size.height * 0.18f),
            cornerRadius = CornerRadius(18f, 18f),
        )
    }
}

@Composable
private fun OverviewBottomBar(
    metrics: OverviewLayoutMetrics,
    actions: List<BottomBarAction>,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(metrics.cardRadius),
        color = Color.White.copy(alpha = 0.96f),
        shadowElevation = metrics.dp(28f),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = metrics.dp(20f), vertical = metrics.dp(14f)),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            actions.forEach { action ->
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clip(RoundedCornerShape(metrics.dp(20f)))
                        .clickable(onClick = action.onClick)
                        .padding(vertical = metrics.dp(6f)),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(metrics.dp(8f)),
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = null,
                        tint = if (action.active) BluePrimary else TextTertiary,
                        modifier = Modifier.size(metrics.dp(36f)),
                    )
                    Text(
                        text = action.label,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = metrics.sp(28f, min = 10f, max = 14f),
                        ),
                        color = if (action.active) BluePrimary else TextTertiary,
                        fontWeight = if (action.active) FontWeight.Bold else FontWeight.Medium,
                    )
                }
            }
        }
    }
}

@Composable
private fun MiniSparkline(
    points: List<Float>,
    positive: Boolean,
    modifier: Modifier = Modifier,
) {
    val safePoints = if (points.size >= 2) points else listOf(0.4f, 0.36f, 0.5f, 0.44f, 0.58f, 0.52f)
    val strokeColor = if (positive) OverviewAccentGreen else RedNegative

    Canvas(modifier = modifier) {
        val left = 2.dp.toPx()
        val right = size.width - 2.dp.toPx()
        val top = 4.dp.toPx()
        val bottom = size.height - 4.dp.toPx()
        val width = right - left
        val height = bottom - top
        val min = safePoints.minOrNull() ?: 0f
        val max = safePoints.maxOrNull() ?: 1f
        val range = (max - min).takeIf { it > 0f } ?: 1f
        val stepX = width / safePoints.lastIndex.coerceAtLeast(1)
        val path = Path()
        val fill = Path()

        safePoints.forEachIndexed { index, point ->
            val x = left + (stepX * index)
            val y = bottom - (((point - min) / range) * height)
            if (index == 0) {
                path.moveTo(x, y)
                fill.moveTo(x, bottom)
                fill.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fill.lineTo(x, y)
            }
        }
        fill.lineTo(right, bottom)
        fill.close()

        drawPath(
            path = fill,
            color = strokeColor.copy(alpha = 0.10f),
            style = Fill,
        )
        drawPath(
            path = path,
            color = strokeColor,
            style = Stroke(width = 4f, cap = StrokeCap.Round),
        )
    }
}

@Composable
private fun rememberPriceSeries(
    marketViewModel: MarketViewModel,
    symbol: String?,
): List<Float> {
    val series by produceState(initialValue = emptyList<TokenPricePoint>(), symbol) {
        if (symbol.isNullOrBlank()) {
            value = emptyList()
        } else {
            marketViewModel.loadPriceSeries(symbol) { points -> value = points }
        }
    }
    return series.map { it.price }
}

@Composable
private fun rememberConnectionTimer(
    vpnState: VpnState,
): String {
    val connectedAt = (vpnState as? VpnState.Connected)?.connectedAt
    val text by produceState(initialValue = "00:00:00", connectedAt) {
        if (connectedAt == null) {
            value = "00:00:00"
            return@produceState
        }
        while (true) {
            value = formatElapsed(System.currentTimeMillis() - connectedAt)
            delay(1000)
        }
    }
    return text
}

private data class VpnStatusPresentation(
    val label: String,
    val orbText: String,
    val actionLabel: String,
    val tint: Color,
    val containerColor: Color,
    val loading: Boolean = false,
)

private fun vpnStatusMeta(state: VpnState): VpnStatusPresentation = when (state) {
    is VpnState.Connected -> VpnStatusPresentation(
        label = "已连接",
        orbText = "已连接",
        actionLabel = "点击断开",
        tint = OverviewAccentGreen,
        containerColor = Color(0xFFEAFBF3),
    )
    is VpnState.Connecting -> VpnStatusPresentation(
        label = "连接中",
        orbText = "连接中",
        actionLabel = "正在连接",
        tint = BluePrimary,
        containerColor = Color(0xFFEAF1FF),
        loading = true,
    )
    is VpnState.Disconnecting -> VpnStatusPresentation(
        label = "断开中",
        orbText = "断开中",
        actionLabel = "正在断开",
        tint = TextSecondary,
        containerColor = Color(0xFFF3F6FC),
        loading = true,
    )
    is VpnState.Error -> VpnStatusPresentation(
        label = "异常",
        orbText = "连接异常",
        actionLabel = "重试连接",
        tint = RedNegative,
        containerColor = Color(0xFFFFEFF1),
    )
    VpnState.Disconnected -> VpnStatusPresentation(
        label = "未连接",
        orbText = "未连接",
        actionLabel = "点击连接",
        tint = BluePrimary,
        containerColor = Color(0xFFEFF4FF),
    )
}

private fun formatElapsed(millis: Long): String {
    val totalSeconds = (millis / 1000L).coerceAtLeast(0L)
    val hours = totalSeconds / 3600
    val minutes = (totalSeconds % 3600) / 60
    val seconds = totalSeconds % 60
    return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, seconds)
}

private fun signedMoney(value: Double): String = if (value >= 0.0) {
    "+ ${Formatters.money(value)}"
} else {
    "- ${Formatters.money(kotlin.math.abs(value))}"
}

private fun plainMoney(value: Double): String = DecimalFormat("#,##0.00").format(value)

private fun btcFormat(value: Double): String = String.format(Locale.getDefault(), "%.4f", value)

private fun countryFlag(code: String): String = when (code.uppercase(Locale.ROOT)) {
    "US" -> "🇺🇸"
    "JP" -> "🇯🇵"
    "SG" -> "🇸🇬"
    "DE" -> "🇩🇪"
    "HK" -> "🇭🇰"
    else -> "🌐"
}

private fun countryName(code: String): String = when (code.uppercase(Locale.ROOT)) {
    "US" -> "美国"
    "JP" -> "日本"
    "SG" -> "新加坡"
    "DE" -> "德国"
    "HK" -> "中国香港"
    else -> code.uppercase(Locale.ROOT)
}
