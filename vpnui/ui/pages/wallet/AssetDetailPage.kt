package com.cryptovpn.ui.pages.wallet

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

/**
 * 交易记录类型
 */
enum class TransactionType {
    RECEIVE,    // 接收
    SEND,       // 发送
    SWAP        // 兑换
}

/**
 * 交易记录
 */
data class TransactionRecord(
    val id: String,
    val type: TransactionType,
    val amount: String,
    val symbol: String,
    val fromTo: String,
    val timestamp: Date,
    val status: String
)

/**
 * 资产详情页状态
 */
sealed class AssetDetailState {
    object Idle : AssetDetailState()
    object Loading : AssetDetailState()
    data class Loaded(
        val symbol: String,
        val name: String,
        val balance: String,
        val value: String,
        val price: String,
        val priceChange: String,
        val transactions: List<TransactionRecord>
    ) : AssetDetailState()
    data class Error(val message: String) : AssetDetailState()
}

/**
 * 资产详情页ViewModel
 */
@HiltViewModel
class AssetDetailViewModel @Inject constructor() : ViewModel() {
    private val _state = MutableStateFlow<AssetDetailState>(AssetDetailState.Idle)
    val state: StateFlow<AssetDetailState> = _state

    fun loadAssetDetail(symbol: String) {
        val dateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        
        val transactions = listOf(
            TransactionRecord(
                id = "tx1",
                type = TransactionType.RECEIVE,
                amount = "+0.5",
                symbol = symbol,
                fromTo = "0x742d...5f0bEb",
                timestamp = dateFormat.parse("2024-01-15 10:30:00") ?: Date(),
                status = "已完成"
            ),
            TransactionRecord(
                id = "tx2",
                type = TransactionType.SEND,
                amount = "-0.25",
                symbol = symbol,
                fromTo = "0x1234...5678",
                timestamp = dateFormat.parse("2024-01-14 15:20:00") ?: Date(),
                status = "已完成"
            ),
            TransactionRecord(
                id = "tx3",
                type = TransactionType.RECEIVE,
                amount = "+1.0",
                symbol = symbol,
                fromTo = "0xabcd...efgh",
                timestamp = dateFormat.parse("2024-01-10 09:15:00") ?: Date(),
                status = "已完成"
            )
        )

        _state.value = AssetDetailState.Loaded(
            symbol = symbol,
            name = when (symbol) {
                "ETH" -> "Ethereum"
                "USDT" -> "Tether USD"
                "BNB" -> "BNB"
                else -> symbol
            },
            balance = "1.25",
            value = "$2,850.00",
            price = "$2,280.00",
            priceChange = "+5.23%",
            transactions = transactions
        )
    }
}

/**
 * 资产详情页
 * 显示资产详情和交易记录
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailPage(
    viewModel: AssetDetailViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    symbol: String = "ETH",
    onNavigateBack: () -> Unit = {},
    onNavigateToSend: (String) -> Unit = {},
    onNavigateToReceive: (String) -> Unit = {},
    onTransactionClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(symbol) {
        viewModel.loadAssetDetail(symbol)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(symbol) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* 打开浏览器查看 */ }) {
                        Icon(
                            imageVector = Icons.Default.OpenInBrowser,
                            contentDescription = "Explorer"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (state) {
                is AssetDetailState.Loaded -> {
                    val loadedState = state as AssetDetailState.Loaded
                    AssetDetailContent(
                        state = loadedState,
                        onNavigateToSend = { onNavigateToSend(symbol) },
                        onNavigateToReceive = { onNavigateToReceive(symbol) },
                        onTransactionClick = onTransactionClick
                    )
                }
                is AssetDetailState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is AssetDetailState.Error -> {
                    ErrorView(message = (state as AssetDetailState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun AssetDetailContent(
    state: AssetDetailState.Loaded,
    onNavigateToSend: () -> Unit,
    onNavigateToReceive: () -> Unit,
    onTransactionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 资产概览
        AssetOverviewCard(
            state = state,
            onNavigateToSend = onNavigateToSend,
            onNavigateToReceive = onNavigateToReceive
        )

        // 交易记录标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "交易记录",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            TextButton(onClick = { /* 查看全部 */ }) {
                Text("查看全部")
            }
        }

        // 交易列表
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(state.transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction.id) }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun AssetOverviewCard(
    state: AssetDetailState.Loaded,
    onNavigateToSend: () -> Unit,
    onNavigateToReceive: () -> Unit
) {
    val priceChangeColor = if (state.priceChange.startsWith("+")) {
        Color(0xFF22C55E)
    } else {
        Color(0xFFEF4444)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 余额
            Text(
                text = "${state.balance} ${state.symbol}",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 价值
            Text(
                text = state.value,
                fontSize = 18.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(16.dp))

            // 价格
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = state.price,
                    fontSize = 16.sp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.width(8.dp))
                Surface(
                    color = priceChangeColor.copy(alpha = 0.1f),
                    shape = MaterialTheme.shapes.small
                ) {
                    Text(
                        text = state.priceChange,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium,
                        color = priceChangeColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 操作按钮
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                AssetActionButton(
                    icon = Icons.Default.ArrowDownward,
                    label = "收款",
                    onClick = onNavigateToReceive
                )
                AssetActionButton(
                    icon = Icons.Default.ArrowUpward,
                    label = "发送",
                    onClick = onNavigateToSend
                )
                AssetActionButton(
                    icon = Icons.Default.SwapHoriz,
                    label = "兑换",
                    onClick = { /* 兑换 */ }
                )
            }
        }
    }
}

@Composable
private fun AssetActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable(onClick = onClick)
    ) {
        Surface(
            shape = MaterialTheme.shapes.medium,
            color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
            modifier = Modifier.size(52.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = label,
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
private fun TransactionItem(
    transaction: TransactionRecord,
    onClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    
    val (icon, iconColor) = when (transaction.type) {
        TransactionType.RECEIVE -> Pair(Icons.Default.ArrowDownward, Color(0xFF22C55E))
        TransactionType.SEND -> Pair(Icons.Default.ArrowUpward, Color(0xFFEF4444))
        TransactionType.SWAP -> Pair(Icons.Default.SwapHoriz, Color(0xFF1D4ED8))
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 类型图标
            Surface(
                shape = MaterialTheme.shapes.small,
                color = iconColor.copy(alpha = 0.1f),
                modifier = Modifier.size(44.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = transaction.type.name,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 交易信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = when (transaction.type) {
                        TransactionType.RECEIVE -> "接收"
                        TransactionType.SEND -> "发送"
                        TransactionType.SWAP -> "兑换"
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = transaction.fromTo,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // 金额和时间
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "${transaction.amount} ${transaction.symbol}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = iconColor
                )
                Text(
                    text = dateFormat.format(transaction.timestamp),
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ErrorView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Error,
            contentDescription = null,
            modifier = Modifier.size(64.dp),
            tint = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "加载失败",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.error
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = message,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}

@Preview(showBackground = true)
@Composable
fun AssetDetailPagePreview() {
    MaterialTheme {
        AssetDetailPage()
    }
}
