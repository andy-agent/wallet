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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowOutward
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.v2ray.ang.composeui.theme.CryptoVPNTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

enum class TransactionType {
    RECEIVE,
    SEND,
    SWAP,
}

data class TransactionRecord(
    val id: String,
    val type: TransactionType,
    val amount: String,
    val symbol: String,
    val fromTo: String,
    val timestamp: Date,
    val status: String,
)

sealed class AssetDetailState {
    data object Idle : AssetDetailState()
    data object Loading : AssetDetailState()

    data class Loaded(
        val symbol: String,
        val name: String,
        val balance: String,
        val value: String,
        val price: String,
        val priceChange: String,
        val transactions: List<TransactionRecord>,
    ) : AssetDetailState()

    data class Error(val message: String) : AssetDetailState()
}

class AssetDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val walletBridgeRepository = WalletBridgeRepository(application)
    private val _state = MutableStateFlow<AssetDetailState>(AssetDetailState.Idle)
    val state: StateFlow<AssetDetailState> = _state

    fun loadAssetDetail(symbol: String) {
        viewModelScope.launch {
            _state.value = AssetDetailState.Loading
            val userId = walletBridgeRepository.getCurrentUserId()
            if (userId.isNullOrBlank()) {
                _state.value = AssetDetailState.Error("当前未登录")
                return@launch
            }
            val orders = walletBridgeRepository.getCachedOrders(userId)
                .filter { it.assetCode.equals(symbol, true) }
            val transactions = orders.map {
                TransactionRecord(
                    id = it.orderNo,
                    type = if (
                        it.status == "COMPLETED" || it.status == "PAID" || it.status == "FULFILLED"
                    ) {
                        TransactionType.RECEIVE
                    } else {
                        TransactionType.SEND
                    },
                    amount = it.amount,
                    symbol = it.assetCode,
                    fromTo = it.planName,
                    timestamp = Date(it.createdAt),
                    status = it.status,
                )
            }.sortedByDescending { it.timestamp.time }
            val balance = orders.sumOf { it.amount.toDoubleOrNull() ?: 0.0 }
            _state.value = AssetDetailState.Loaded(
                symbol = symbol.uppercase(),
                name = assetName(symbol),
                balance = String.format("%.4f", balance),
                value = "$${String.format("%.2f", balance)}",
                price = if (balance > 0.0) "$${String.format("%.2f", balance / 2)}" else "--",
                priceChange = if (balance > 0.0) "+0.00%" else "--",
                transactions = transactions,
            )
        }
    }

    private fun assetName(symbol: String): String {
        return when (symbol.uppercase()) {
            "USDT" -> "Tether USD"
            "SOL" -> "Solana"
            "ETH" -> "Ethereum"
            "TRX", "TRON" -> "TRON"
            "BNB" -> "BNB Chain"
            "MATIC" -> "Polygon"
            else -> symbol.uppercase()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailPage(
    viewModel: AssetDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    symbol: String = "ETH",
    onNavigateBack: () -> Unit = {},
    onNavigateToSend: (String) -> Unit = {},
    onNavigateToReceive: (String) -> Unit = {},
    onTransactionClick: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadAssetDetail(symbol)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(symbol.uppercase()) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = {}) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Open explorer",
                        )
                    }
                },
            )
        },
    ) { paddingValues ->
        WalletPageBackdrop(modifier = Modifier.padding(paddingValues)) {
            when (val currentState = state) {
                is AssetDetailState.Loaded -> AssetDetailContent(
                    state = currentState,
                    onNavigateToSend = { onNavigateToSend(symbol.uppercase()) },
                    onNavigateToReceive = { onNavigateToReceive(symbol.uppercase()) },
                    onTransactionClick = onTransactionClick,
                )

                AssetDetailState.Idle, AssetDetailState.Loading -> Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center,
                ) {
                    CircularProgressIndicator()
                }

                is AssetDetailState.Error -> WalletDetailErrorView(message = currentState.message)
            }
        }
    }
}

@Composable
private fun AssetDetailContent(
    state: AssetDetailState.Loaded,
    onNavigateToSend: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onTransactionClick: (String) -> Unit,
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 20.dp, end = 20.dp, top = 18.dp, bottom = 28.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            WalletGlassCard(accent = walletAssetAccent(state.symbol)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        WalletTokenBadge(symbol = state.symbol)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            WalletTag(text = walletNetworkLabel(state.symbol), accent = walletAssetAccent(state.symbol))
                            Text(
                                text = state.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                            )
                        }
                    }
                    WalletTag(text = "ASSET", accent = MaterialTheme.colorScheme.secondary)
                }

                Text(
                    text = "${state.balance} ${state.symbol}",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Text(
                    text = state.value,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                WalletMetricStrip(
                    metrics = listOf(
                        WalletOverviewMetric("参考价", state.price),
                        WalletOverviewMetric("波动", state.priceChange),
                        WalletOverviewMetric("流水", state.transactions.size.toString()),
                    ),
                )

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
                            label = "History",
                            hint = "查看最近订单",
                            icon = Icons.Default.History,
                            accent = MaterialTheme.colorScheme.tertiary,
                            onClick = {
                                state.transactions.firstOrNull()?.id?.let(onTransactionClick)
                            },
                        ),
                        WalletQuickAction(
                            label = "Explorer",
                            hint = "占位保留外部链路",
                            icon = Icons.Default.ArrowOutward,
                            accent = walletAssetAccent(state.symbol),
                            onClick = {},
                        ),
                    ),
                )
            }
        }

        item {
            WalletSectionHeading(
                title = "资产说明",
                subtitle = "链路、钱包占位和交易详情继续依赖现有 bridge 与本地缓存。",
            )
        }

        item {
            WalletGlassCard(accent = MaterialTheme.colorScheme.secondary) {
                WalletMetricStrip(
                    metrics = listOf(
                        WalletOverviewMetric("网络", walletNetworkLabel(state.symbol)),
                        WalletOverviewMetric("状态", "Bridge Ready"),
                        WalletOverviewMetric("类型", "Spot"),
                    ),
                )
                Text(
                    text = "该页面仅重构视觉与层级，不改变钱包详情路由和交易点击行为。",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        item {
            WalletSectionHeading(
                title = "交易记录",
                subtitle = "最近流水从现有订单缓存映射而来，点击仍跳转原有订单详情。",
            )
        }

        if (state.transactions.isEmpty()) {
            item {
                WalletGlassCard(accent = MaterialTheme.colorScheme.error) {
                    Text(
                        text = "暂无交易记录",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Text(
                        text = "等待 bridge 写入真实订单后，这里会直接显示最新流水。",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
            }
        } else {
            items(state.transactions) { transaction ->
                TransactionRow(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) },
                )
            }
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: TransactionRecord,
    onClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val accent = when (transaction.type) {
        TransactionType.RECEIVE -> MaterialTheme.colorScheme.primary
        TransactionType.SEND -> MaterialTheme.colorScheme.error
        TransactionType.SWAP -> MaterialTheme.colorScheme.secondary
    }
    val title = when (transaction.type) {
        TransactionType.RECEIVE -> "接收"
        TransactionType.SEND -> "发送"
        TransactionType.SWAP -> "兑换"
    }

    WalletGlassCard(
        modifier = Modifier.clickable(onClick = onClick),
        accent = accent,
        contentPadding = PaddingValues(18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            WalletTokenBadge(symbol = transaction.symbol)
            Spacer(modifier = Modifier.width(14.dp))
            Column(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold,
                    )
                    WalletTag(text = transaction.status, accent = accent)
                }
                Text(
                    text = transaction.fromTo,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Text(
                    text = "${transaction.amount} ${transaction.symbol}",
                    style = MaterialTheme.typography.titleSmall,
                    color = accent,
                    fontWeight = FontWeight.SemiBold,
                )
                Text(
                    text = dateFormat.format(transaction.timestamp),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }
    }
}

@Composable
private fun WalletDetailErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        contentAlignment = Alignment.Center,
    ) {
        WalletGlassCard(accent = Color(0xFFFF6B7A)) {
            Text(
                text = "资产详情不可用",
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
private fun AssetDetailPagePreview() {
    CryptoVPNTheme {
        AssetDetailPage()
    }
}
