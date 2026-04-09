package com.v2ray.ang.composeui.pages.wallet

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import com.v2ray.ang.composeui.components.tags.StatusTag
import com.v2ray.ang.composeui.components.tags.StatusType
import com.v2ray.ang.composeui.theme.ControlPlaneIntent
import com.v2ray.ang.composeui.theme.ControlPlaneLayer
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.abs

data class AssetInfo(
    val symbol: String,
    val name: String,
    val balance: String,
    val value: String,
    val iconUrl: String? = null,
)

sealed class WalletHomeState {
    data object Idle : WalletHomeState()
    data object Loading : WalletHomeState()

    data class Loaded(
        val walletAddress: String,
        val totalValue: String,
        val assets: List<AssetInfo>,
    ) : WalletHomeState()

    data class Error(val message: String) : WalletHomeState()
}

class WalletHomeViewModel(application: Application) : AndroidViewModel(application) {
    private val walletBridgeRepository = WalletBridgeRepository(application)
    private val _state = MutableStateFlow<WalletHomeState>(WalletHomeState.Idle)
    val state: StateFlow<WalletHomeState> = _state

    private val _isBalanceVisible = MutableStateFlow(true)
    val isBalanceVisible: StateFlow<Boolean> = _isBalanceVisible

    init {
        loadWalletData()
    }

    private fun loadWalletData() {
        viewModelScope.launch {
            _state.value = WalletHomeState.Loading
            val userId = walletBridgeRepository.getCurrentUserId()
            if (userId.isNullOrBlank()) {
                _state.value = WalletHomeState.Error("当前未登录")
                return@launch
            }
            val orders = walletBridgeRepository.getCachedOrders(userId)
            val paidOrders = orders.filter {
                it.status == "COMPLETED" || it.status == "PAID" || it.status == "FULFILLED"
            }
            val assets = mutableListOf<AssetInfo>()
            val assetGroups = paidOrders.groupBy { it.assetCode.uppercase() }
            assetGroups.forEach { (symbol, assetOrders) ->
                val amount = assetOrders.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
                assets += AssetInfo(
                    symbol = symbol,
                    name = defaultAssetName(symbol),
                    balance = String.format("%.4f", amount),
                    value = "$${String.format("%.2f", amount)}",
                )
            }
            if (assets.isEmpty()) {
                assets += AssetInfo(
                    symbol = "USDT",
                    name = defaultAssetName("USDT"),
                    balance = "0.0000",
                    value = "$0.00",
                )
            }
            val total = assets.sumOf { it.balance.toDoubleOrNull() ?: 0.0 }
            _state.value = WalletHomeState.Loaded(
                walletAddress = walletBridgeRepository.currentWalletAddressFallback(userId),
                totalValue = "$${String.format("%.2f", total)}",
                assets = assets.sortedByDescending { it.balance.toDoubleOrNull() ?: 0.0 },
            )
        }
    }

    fun toggleBalanceVisibility() {
        _isBalanceVisible.value = !_isBalanceVisible.value
    }

    fun refreshData() {
        loadWalletData()
    }

    private fun defaultAssetName(symbol: String): String {
        return when (symbol.uppercase()) {
            "USDT" -> "Tether USD"
            "SOL" -> "Solana"
            "ETH" -> "Ethereum"
            "TRX", "TRON" -> "TRON"
            "BNB" -> "BNB Chain"
            "MATIC" -> "Polygon"
            "USD24" -> "USD24"
            else -> symbol
        }
    }
}

@Composable
fun WalletHomePage(
    viewModel: WalletHomeViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateToReceive: () -> Unit = {},
    onNavigateToSend: () -> Unit = {},
    onNavigateToAssetDetail: (String) -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()
    val isBalanceVisible by viewModel.isBalanceVisible.collectAsState()
    var showWalletSwitcher by rememberSaveable { mutableStateOf(false) }

    Scaffold(containerColor = Color.Transparent) { paddingValues ->
        WalletPageBackdrop(modifier = Modifier.padding(paddingValues)) {
            when (val currentState = state) {
                is WalletHomeState.Loaded -> WalletHomeContent(
                    state = currentState,
                    isBalanceVisible = isBalanceVisible,
                    showWalletSwitcher = showWalletSwitcher,
                    onDismissWalletSwitcher = { showWalletSwitcher = false },
                    onOpenWalletSwitcher = { showWalletSwitcher = true },
                    onToggleVisibility = viewModel::toggleBalanceVisibility,
                    onRefresh = viewModel::refreshData,
                    onNavigateToReceive = onNavigateToReceive,
                    onNavigateToSend = onNavigateToSend,
                    onNavigateToAssetDetail = onNavigateToAssetDetail,
                    onNavigateToProfile = onNavigateToProfile,
                )

                WalletHomeState.Loading, WalletHomeState.Idle -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator(color = WalletAccent)
                }

                is WalletHomeState.Error -> WalletErrorView(message = currentState.message)
            }
        }
    }
}

@Composable
private fun WalletHomeContent(
    state: WalletHomeState.Loaded,
    isBalanceVisible: Boolean,
    showWalletSwitcher: Boolean,
    onDismissWalletSwitcher: () -> Unit,
    onOpenWalletSwitcher: () -> Unit,
    onToggleVisibility: () -> Unit,
    onRefresh: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onNavigateToSend: () -> Unit,
    onNavigateToAssetDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val featuredAsset = state.assets.firstOrNull()
    val totalValueText = if (isBalanceVisible) state.totalValue else "****"
    val totalValueNumber = state.totalValue.removePrefix("$").toDoubleOrNull() ?: 0.0
    val changeRate = featuredAsset?.let { parsePercent(walletReferenceChange(it.symbol)) } ?: 0f
    val estimatedPnl = totalValueNumber * changeRate.toDouble() / 100.0
    val pnlColor = if (changeRate < 0f) WalletDanger else WalletAccent
    val pnlValueText = formatSignedUsd(estimatedPnl)
    val pnlPercentText = formatSignedPercent(changeRate)

    Box(modifier = Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(
                start = WalletPagePadding,
                end = WalletPagePadding,
                top = 16.dp,
                bottom = 32.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                WalletSearchChrome(
                    avatarLabel = state.walletAddress.firstOrNull()?.uppercaseChar()?.toString() ?: "W",
                    onAvatarClick = onNavigateToProfile,
                    onSearchClick = {},
                    onTrailingClick = onRefresh,
                    trailingIcon = Icons.Default.Refresh,
                )
            }

            item {
                WalletGlassCard(
                    layer = ControlPlaneLayer.Level3,
                    accent = WalletAccent,
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.Top,
                    ) {
                        WalletConsoleHeader(
                            eyebrow = "ASSET CONTROL PLANE",
                            title = "资产总账",
                            detail = "${state.assets.size} assets",
                            modifier = Modifier.weight(1f),
                        )
                        StatusTag(
                            text = if (state.assets.any { (it.balance.toDoubleOrNull() ?: 0.0) > 0.0 }) {
                                "已同步"
                            } else {
                                "待入账"
                            },
                            type = if (state.assets.any { (it.balance.toDoubleOrNull() ?: 0.0) > 0.0 }) {
                                StatusType.OK
                            } else {
                                StatusType.UNKNOWN
                            },
                        )
                    }
                    WalletIdentityRow(
                        walletAddress = state.walletAddress,
                        onClick = onOpenWalletSwitcher,
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Text(
                            text = totalValueText,
                            fontSize = 42.sp,
                            lineHeight = 42.sp,
                            fontWeight = FontWeight.Bold,
                            color = WalletTextPrimary,
                        )
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .background(WalletSurfaceStrong, shape = RoundedCornerShape(14.dp))
                                .clickable(onClick = onToggleVisibility),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(
                                imageVector = if (isBalanceVisible) {
                                    Icons.Default.Visibility
                                } else {
                                    Icons.Default.VisibilityOff
                                },
                                contentDescription = "toggle balance",
                                tint = WalletTextSecondary,
                            )
                        }
                    }
                    WalletMetricStrip(
                        metrics = listOf(
                            WalletOverviewMetric("当日盈亏", "$pnlValueText · $pnlPercentText"),
                            WalletOverviewMetric("主资产", featuredAsset?.symbol ?: "USDT"),
                            WalletOverviewMetric("账本状态", if (state.assets.size > 1) "多资产" else "单资产"),
                        ),
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        WalletIntentBadge(text = "INFRA CUSTODY", intent = ControlPlaneIntent.Infra)
                        WalletIntentBadge(text = "SETTLEMENT READY", intent = ControlPlaneIntent.Settlement)
                        WalletIntentBadge(text = "FINANCE VIEW", intent = ControlPlaneIntent.Finance)
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WalletHomeAction(
                        label = "转账",
                        icon = Icons.Default.AccountBalanceWallet,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToSend,
                    )
                    WalletHomeAction(
                        label = "收款",
                        icon = Icons.Default.SouthWest,
                        modifier = Modifier.weight(1f),
                        onClick = onNavigateToReceive,
                    )
                    WalletHomeAction(
                        label = "交易历史",
                        icon = Icons.Default.History,
                        modifier = Modifier.weight(1f),
                        onClick = { featuredAsset?.let { onNavigateToAssetDetail(it.symbol) } },
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WalletFeatureCard(
                        title = "银行卡",
                        value = "$0.00",
                        modifier = Modifier.weight(1f),
                        accent = Color(0xFFD8B595),
                    )
                    WalletFeatureCard(
                        title = "理财",
                        value = "4% 年化收益率",
                        modifier = Modifier.weight(1f),
                        accent = Color(0xFFA9C2A2),
                    )
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WalletConsoleHeader(
                        eyebrow = "LEDGER MATRIX",
                        title = "代币",
                        detail = state.totalValue,
                        modifier = Modifier.weight(1f),
                    )
                    WalletToolbarIconButton(
                        icon = Icons.Default.FilterList,
                        contentDescription = "filter",
                        onClick = {},
                        tint = WalletTextSecondary,
                    )
                }
            }

            item {
                WalletGlassCard(
                    layer = ControlPlaneLayer.Level1,
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    state.assets.forEachIndexed { index, asset ->
                        WalletHomeAssetRow(
                            asset = asset,
                            isBalanceVisible = isBalanceVisible,
                            onClick = { onNavigateToAssetDetail(asset.symbol) },
                        )
                        if (index != state.assets.lastIndex) {
                            WalletDivider(modifier = Modifier.padding(horizontal = 18.dp))
                        }
                    }
                    WalletDivider(modifier = Modifier.padding(horizontal = 18.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable(onClick = {})
                            .padding(horizontal = 18.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        Text(
                            text = "代币管理",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WalletTextSecondary,
                        )
                        WalletIntentBadge(
                            text = "${state.assets.size} tracked",
                            intent = ControlPlaneIntent.Neutral,
                            compact = true,
                        )
                    }
                }
            }
        }

        if (showWalletSwitcher) {
            WalletSwitcherOverlay(
                currentAddress = state.walletAddress,
                totalValue = state.totalValue,
                featuredAsset = featuredAsset?.symbol ?: "USDT",
                onDismiss = onDismissWalletSwitcher,
            )
        }
    }
}

@Composable
private fun WalletIdentityRow(
    walletAddress: String,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = walletShortAddress(walletAddress),
                style = MaterialTheme.typography.titleMedium,
                color = WalletTextSecondary,
                maxLines = 1,
            )
            Text(
                text = "当前钱包",
                style = MaterialTheme.typography.bodySmall,
                color = WalletTextTertiary,
            )
        }
        Icon(
            imageVector = Icons.Default.KeyboardArrowDown,
            contentDescription = "open wallet switcher",
            tint = WalletTextSecondary,
        )
        Spacer(modifier = Modifier.width(12.dp))
        WalletToolbarIconButton(
            icon = Icons.Default.ContentCopy,
            contentDescription = "copy wallet address",
            onClick = {},
            tint = WalletTextSecondary,
        )
    }
}

@Composable
private fun WalletHomeAction(
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    WalletGlassCard(
        modifier = modifier.clickable(onClick = onClick),
        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 14.dp),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Box(
                modifier = Modifier
                    .size(34.dp)
                    .background(
                        WalletAccent.copy(alpha = 0.12f),
                        shape = androidx.compose.foundation.shape.CircleShape,
                    ),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = WalletAccent,
                    modifier = Modifier.size(16.dp),
                )
            }
            Text(
                text = label,
                style = MaterialTheme.typography.titleSmall,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
            )
        }
    }
}

@Composable
private fun WalletFeatureCard(
    title: String,
    value: String,
    accent: Color,
    modifier: Modifier = Modifier,
) {
    WalletGlassCard(
        modifier = modifier,
        accent = accent,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 18.dp),
    ) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .background(accent.copy(alpha = 0.12f), shape = androidx.compose.foundation.shape.CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(accent, shape = androidx.compose.foundation.shape.RoundedCornerShape(4.dp)),
            )
        }
        Text(
            text = title,
            style = MaterialTheme.typography.bodyMedium,
            color = WalletTextSecondary,
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            color = WalletTextPrimary,
            fontWeight = FontWeight.SemiBold,
        )
    }
}

@Composable
private fun WalletHomeAssetRow(
    asset: AssetInfo,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WalletTokenBadge(symbol = asset.symbol, modifier = Modifier.size(44.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = asset.symbol,
                style = MaterialTheme.typography.titleMedium,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = walletReferencePrice(asset.symbol),
                    style = MaterialTheme.typography.bodyMedium,
                    color = WalletTextSecondary,
                )
                Text(
                    text = walletReferenceChange(asset.symbol),
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (walletReferenceChange(asset.symbol).startsWith("-")) {
                        WalletDanger
                    } else {
                        WalletAccent
                    },
                    fontWeight = FontWeight.Medium,
                )
            }
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = if (isBalanceVisible) {
                    asset.balance.trimEnd('0').trimEnd('.').ifBlank { asset.balance }
                } else {
                    "****"
                },
                style = MaterialTheme.typography.titleMedium,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = if (isBalanceVisible) asset.value else "****",
                style = MaterialTheme.typography.bodyMedium,
                color = WalletTextSecondary,
            )
        }
    }
}

@Composable
private fun WalletSwitcherOverlay(
    currentAddress: String,
    totalValue: String,
    featuredAsset: String,
    onDismiss: () -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(WalletTextPrimary.copy(alpha = 0.14f))
                .clickable(onClick = onDismiss),
        )
        WalletBottomSheetCard(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .navigationBarsPadding(),
        ) {
            Text(
                text = "选择钱包",
                style = MaterialTheme.typography.headlineSmall,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            WalletInfoRow(label = "总资产", value = totalValue)
            WalletWalletCard(
                title = "默认钱包",
                address = walletShortAddress(currentAddress),
                featuredAsset = featuredAsset,
                selected = true,
            )
            WalletWalletCard(
                title = "硬件钱包",
                address = walletShortAddress("${currentAddress.take(8)}A1F38F"),
                featuredAsset = "ETH",
                selected = false,
            )
            WalletWalletCard(
                title = "交易钱包",
                address = walletShortAddress("${currentAddress.take(8)}B7CE0D"),
                featuredAsset = "USDT",
                selected = false,
            )
            WalletPrimaryButton(
                label = "添加钱包",
                onClick = onDismiss,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Default.Add,
            )
        }
    }
}

@Composable
private fun WalletWalletCard(
    title: String,
    address: String,
    featuredAsset: String,
    selected: Boolean,
) {
    WalletGlassCard(
        accent = walletAssetAccent(featuredAsset),
        selected = selected,
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WalletTokenBadge(symbol = featuredAsset, modifier = Modifier.size(42.dp))
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = WalletTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = address,
                    style = MaterialTheme.typography.bodySmall,
                    color = WalletTextSecondary,
                )
            }
            if (selected) {
                WalletTag(text = "当前", accent = WalletAccent)
            }
        }
    }
}

@Composable
private fun WalletErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WalletPagePadding),
        contentAlignment = Alignment.Center,
    ) {
        WalletGlassCard(accent = WalletDanger) {
            Text(
                text = "Wallet 暂不可用",
                style = MaterialTheme.typography.headlineSmall,
                color = WalletTextPrimary,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = WalletDanger,
            )
        }
    }
}

private fun parsePercent(text: String): Float {
    return text.replace("%", "").replace("+", "").toFloatOrNull()?.let {
        if (text.startsWith("-")) -abs(it) else it
    } ?: 0f
}

private fun formatSignedUsd(amount: Double): String {
    val prefix = if (amount > 0) "+$" else if (amount < 0) "-$" else "$"
    return "$prefix${String.format("%.2f", abs(amount))}"
}

private fun formatSignedPercent(amount: Float): String {
    val prefix = if (amount > 0) "+" else ""
    return "$prefix${String.format("%.2f", amount)}%"
}

@Preview(showBackground = true)
@Composable
private fun WalletHomePagePreview() {
    CryptoVPNTheme {
        WalletHomePage()
    }
}
