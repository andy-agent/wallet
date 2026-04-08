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
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.SouthWest
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
                price = walletReferencePrice(symbol),
                priceChange = walletReferenceChange(symbol),
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
            "USD24" -> "USD24"
            else -> symbol.uppercase()
        }
    }
}

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

    WalletPageBackdrop {
        when (val currentState = state) {
            is AssetDetailState.Loaded -> AssetDetailLoadedContent(
                state = currentState,
                onNavigateBack = onNavigateBack,
                onNavigateToSend = { onNavigateToSend(symbol.uppercase()) },
                onNavigateToReceive = { onNavigateToReceive(symbol.uppercase()) },
                onTransactionClick = onTransactionClick,
            )

            AssetDetailState.Idle, AssetDetailState.Loading -> Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center,
            ) {
                CircularProgressIndicator(color = WalletAccent)
            }

            is AssetDetailState.Error -> WalletDetailErrorView(message = currentState.message)
        }
    }
}

@Composable
private fun AssetDetailLoadedContent(
    state: AssetDetailState.Loaded,
    onNavigateBack: () -> Unit,
    onNavigateToSend: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onTransactionClick: (String) -> Unit,
) {
    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = WalletPagePadding, vertical = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                WalletSecondaryButton(
                    label = "收款",
                    onClick = onNavigateToReceive,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.SouthWest,
                )
                WalletPrimaryButton(
                    label = "转账",
                    onClick = onNavigateToSend,
                    modifier = Modifier.weight(1f),
                    icon = Icons.Default.NorthEast,
                )
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentPadding = PaddingValues(
                start = WalletPagePadding,
                end = WalletPagePadding,
                top = 12.dp,
                bottom = 18.dp,
            ),
            verticalArrangement = Arrangement.spacedBy(18.dp),
        ) {
            item {
                WalletTopBar(
                    title = state.symbol,
                    onBack = onNavigateBack,
                    trailingIcon = Icons.Default.OpenInBrowser,
                    trailingDescription = "open explorer",
                    onTrailingClick = {},
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top,
                ) {
                    Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                        WalletTokenBadge(symbol = state.symbol)
                        Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = state.name,
                                style = MaterialTheme.typography.headlineSmall,
                                color = WalletTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = walletNetworkLabel(state.symbol),
                                style = MaterialTheme.typography.bodyMedium,
                                color = WalletTextSecondary,
                            )
                        }
                    }
                    WalletTag(text = "资产详情", accent = walletAssetAccent(state.symbol))
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom,
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(6.dp),
                    ) {
                        Text(
                            text = state.balance.trimEnd('0').trimEnd('.').ifBlank { state.balance },
                            fontSize = 40.sp,
                            fontWeight = FontWeight.Bold,
                            color = WalletTextPrimary,
                        )
                        Text(
                            text = state.value,
                            style = MaterialTheme.typography.titleMedium,
                            color = WalletTextSecondary,
                        )
                    }
                    WalletPrimaryButton(
                        label = "交易",
                        onClick = onNavigateToSend,
                        modifier = Modifier.width(132.dp),
                    )
                }
            }

            item {
                WalletGlassCard(
                    accent = Color(0xFFE9B56D),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 14.dp),
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = null,
                            tint = Color(0xFFE9B56D),
                        )
                        Text(
                            text = "请勿随意转账或交易以免造成损失，错误网络资产不会自动退回。",
                            style = MaterialTheme.typography.bodyMedium,
                            color = WalletTextPrimary,
                        )
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    WalletGlassCard(
                        modifier = Modifier.weight(1f),
                        accent = walletAssetAccent(state.symbol),
                    ) {
                        Text(
                            text = "链上行情",
                            style = MaterialTheme.typography.titleMedium,
                            color = WalletTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = state.priceChange,
                            style = MaterialTheme.typography.titleLarge,
                            color = if (state.priceChange.startsWith("-")) WalletDanger else WalletAccent,
                            fontWeight = FontWeight.Bold,
                        )
                        Text(
                            text = state.price,
                            style = MaterialTheme.typography.bodyMedium,
                            color = WalletTextSecondary,
                        )
                    }
                    WalletGlassCard(
                        modifier = Modifier.weight(1f),
                        accent = Color(0xFF9AC0FF),
                    ) {
                        Text(
                            text = "产品信息",
                            style = MaterialTheme.typography.titleMedium,
                            color = WalletTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        WalletInfoRow(label = "网络", value = walletNetworkLabel(state.symbol))
                        WalletInfoRow(label = "模式", value = "Wallet Live")
                        WalletInfoRow(label = "状态", value = "Bridge Ready")
                    }
                }
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = "交易历史",
                        style = MaterialTheme.typography.titleLarge,
                        color = WalletTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = null,
                        tint = WalletTextSecondary,
                    )
                }
            }

            if (state.transactions.isEmpty()) {
                item {
                    WalletGlassCard(accent = WalletDanger) {
                        Text(
                            text = "暂无交易记录",
                            style = MaterialTheme.typography.titleMedium,
                            color = WalletTextPrimary,
                            fontWeight = FontWeight.SemiBold,
                        )
                        Text(
                            text = "等待 bridge 写入真实订单后，这里会直接显示最新流水。",
                            style = MaterialTheme.typography.bodySmall,
                            color = WalletTextSecondary,
                        )
                    }
                }
            } else {
                item {
                    WalletGlassCard(contentPadding = PaddingValues(vertical = 8.dp)) {
                        state.transactions.forEachIndexed { index, transaction ->
                            TransactionRow(
                                transaction = transaction,
                                onClick = { onTransactionClick(transaction.id) },
                            )
                            if (index != state.transactions.lastIndex) {
                                WalletDivider(modifier = Modifier.padding(horizontal = 18.dp))
                            }
                        }
                    }
                }
            }

            item {
                WalletGlassCard(
                    accent = Color(0xFF748186),
                    contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
                ) {
                    Text(
                        text = "安全检测",
                        style = MaterialTheme.typography.titleMedium,
                        color = WalletTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                    )
                    Text(
                        text = "该入口保持弱层级，仅作为链上地址和近期风控状态的附加检查。",
                        style = MaterialTheme.typography.bodySmall,
                        color = WalletTextSecondary,
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    transaction: TransactionRecord,
    onClick: () -> Unit,
) {
    val dateFormat = SimpleDateFormat("MM/dd HH:mm:ss", Locale.getDefault())
    val accent = when (transaction.type) {
        TransactionType.RECEIVE -> WalletAccent
        TransactionType.SEND -> WalletDanger
        TransactionType.SWAP -> Color(0xFF8D8BFF)
    }
    val title = when (transaction.type) {
        TransactionType.RECEIVE -> "收款"
        TransactionType.SEND -> "转账"
        TransactionType.SWAP -> "兑换"
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 18.dp, vertical = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        WalletTokenBadge(symbol = transaction.symbol, modifier = Modifier.size(44.dp))
        Spacer(modifier = Modifier.width(14.dp))
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    color = WalletTextPrimary,
                    fontWeight = FontWeight.SemiBold,
                )
                WalletTag(text = transaction.status, accent = accent)
            }
            Text(
                text = transaction.fromTo.ifBlank { "链上转入" },
                style = MaterialTheme.typography.bodySmall,
                color = WalletTextSecondary,
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(4.dp),
        ) {
            Text(
                text = "${transaction.amount} ${transaction.symbol.uppercase()}",
                style = MaterialTheme.typography.titleSmall,
                color = accent,
                fontWeight = FontWeight.SemiBold,
            )
            Text(
                text = dateFormat.format(transaction.timestamp),
                style = MaterialTheme.typography.bodySmall,
                color = WalletTextSecondary,
            )
        }
    }
}

@Composable
private fun WalletDetailErrorView(message: String) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(WalletPagePadding),
        contentAlignment = Alignment.Center,
    ) {
        WalletGlassCard(accent = WalletDanger) {
            Text(
                text = "资产详情不可用",
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

@Preview(showBackground = true)
@Composable
private fun AssetDetailPagePreview() {
    CryptoVPNTheme {
        AssetDetailPage(symbol = "USD24")
    }
}
