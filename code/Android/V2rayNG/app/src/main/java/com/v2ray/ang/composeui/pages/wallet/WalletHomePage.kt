package com.v2ray.ang.composeui.pages.wallet

import android.app.Application
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.wallet.WalletBridgeRepository
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

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
            else -> symbol
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Wallet") },
                actions = {
                    IconButton(onClick = { viewModel.refreshData() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh wallet",
                        )
                    }
                    IconButton(onClick = onNavigateToProfile) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Open profile",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        WalletPageBackdrop(modifier = Modifier.padding(paddingValues)) {
            when (val currentState = state) {
                is WalletHomeState.Loaded -> WalletHomeContent(
                    state = currentState,
                    isBalanceVisible = isBalanceVisible,
                    onToggleVisibility = { viewModel.toggleBalanceVisibility() },
                    onNavigateToReceive = onNavigateToReceive,
                    onNavigateToSend = onNavigateToSend,
                    onNavigateToAssetDetail = onNavigateToAssetDetail,
                    onNavigateToProfile = onNavigateToProfile,
                )

                WalletHomeState.Loading, WalletHomeState.Idle -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
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
    onToggleVisibility: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onNavigateToSend: () -> Unit,
    onNavigateToAssetDetail: (String) -> Unit,
    onNavigateToProfile: () -> Unit,
) {
    val featuredAsset = state.assets.firstOrNull()
    val totalValueText = if (isBalanceVisible) state.totalValue else "****"
    val assetCount = state.assets.size.toString()
    val networkCount = state.assets.map { walletNetworkLabel(it.symbol) }.distinct().size.toString()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            WalletGlassCard(accent = featuredAsset?.let { walletAssetAccent(it.symbol) } ?: MaterialTheme.colorScheme.primary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        WalletTag(text = "ASSET OVERVIEW")
                        Text(
                            text = "Bitget 风格资产总览",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                    }
                    WalletTag(
                        text = walletShortAddress(state.walletAddress),
                        accent = MaterialTheme.colorScheme.secondary,
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    Text(
                        text = totalValueText,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    IconButton(onClick = onToggleVisibility) {
                        Icon(
                            imageVector = if (isBalanceVisible) {
                                Icons.Default.Visibility
                            } else {
                                Icons.Default.VisibilityOff
                            },
                            contentDescription = "Toggle balance visibility",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }

                Text(
                    text = "账户地址 ${walletShortAddress(state.walletAddress)} · Bridge 已保持兼容",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                WalletMetricStrip(
                    metrics = listOf(
                        WalletOverviewMetric("资产数", assetCount),
                        WalletOverviewMetric("链路", networkCount),
                        WalletOverviewMetric("模式", "Wallet Live"),
                    ),
                )

                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    WalletPrimaryButton(
                        label = "接收",
                        onClick = onNavigateToReceive,
                        modifier = Modifier.weight(1f),
                    )
                    WalletSecondaryButton(
                        label = "发送",
                        onClick = onNavigateToSend,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }

        item {
            WalletSectionHeading(
                title = "快捷操作",
                subtitle = "收发、查看资产明细、跳转账户中心都保持原有路由接线。",
            )
        }

        item {
            WalletActionRow(
                actions = listOf(
                    WalletQuickAction(
                        label = "Receive",
                        hint = "打开收款二维码",
                        icon = Icons.Default.SouthWest,
                        accent = MaterialTheme.colorScheme.primary,
                        onClick = onNavigateToReceive,
                    ),
                    WalletQuickAction(
                        label = "Send",
                        hint = "进入发送起点",
                        icon = Icons.Default.NorthEast,
                        accent = MaterialTheme.colorScheme.secondary,
                        onClick = onNavigateToSend,
                    ),
                    WalletQuickAction(
                        label = "Detail",
                        hint = "查看主资产详情",
                        icon = Icons.Default.ArrowOutward,
                        accent = featuredAsset?.let { walletAssetAccent(it.symbol) }
                            ?: MaterialTheme.colorScheme.tertiary,
                        onClick = { featuredAsset?.let { onNavigateToAssetDetail(it.symbol) } },
                    ),
                    WalletQuickAction(
                        label = "Profile",
                        hint = "进入账户概览",
                        icon = Icons.Default.AccountBalanceWallet,
                        accent = MaterialTheme.colorScheme.tertiary,
                        onClick = onNavigateToProfile,
                    ),
                ),
            )
        }

        item {
            WalletSectionHeading(
                title = "账户概览",
                subtitle = "以 Bitget 风格卡片组织资产标签、主链状态与默认地址。",
            )
        }

        item {
            WalletGlassCard(accent = MaterialTheme.colorScheme.secondary) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Text(
                            text = "默认接收地址",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                        )
                        Text(
                            text = state.walletAddress,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = "Address preview",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                WalletMetricStrip(
                    metrics = listOf(
                        WalletOverviewMetric("主资产", featuredAsset?.symbol ?: "--"),
                        WalletOverviewMetric("网络", featuredAsset?.let { walletNetworkLabel(it.symbol) } ?: "Mainnet"),
                        WalletOverviewMetric("状态", "Bridge Ready"),
                    ),
                )
            }
        }

        item {
            WalletSectionHeading(
                title = "资产列表",
                subtitle = "按资产余额排序，保留现有占位值与订单缓存桥接能力。",
            )
        }

        items(state.assets) { asset ->
            AssetRow(
                asset = asset,
                isBalanceVisible = isBalanceVisible,
                onClick = { onNavigateToAssetDetail(asset.symbol) },
            )
        }
    }
}

@Composable
private fun AssetRow(
    asset: AssetInfo,
    isBalanceVisible: Boolean,
    onClick: () -> Unit,
) {
    WalletGlassCard(
        modifier = Modifier.clickable(onClick = onClick),
        accent = walletAssetAccent(asset.symbol),
        contentPadding = PaddingValues(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WalletTokenBadge(symbol = asset.symbol)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = asset.symbol,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    WalletTag(
                        text = walletNetworkLabel(asset.symbol),
                        accent = walletAssetAccent(asset.symbol),
                    )
                }
                Text(
                    text = asset.name,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = if (isBalanceVisible) {
                        "${asset.balance} ${asset.symbol}"
                    } else {
                        "****"
                    },
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = if (isBalanceVisible) asset.value else "****",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun WalletErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        WalletGlassCard(accent = MaterialTheme.colorScheme.error) {
            Text(
                text = "Wallet 暂不可用",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun WalletHomePagePreview() {
    CryptoVPNTheme {
        WalletHomePage()
    }
}
