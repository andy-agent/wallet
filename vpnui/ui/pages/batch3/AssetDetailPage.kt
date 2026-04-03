package com.cryptovpn.ui.pages

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.cryptovpn.ui.theme.CryptoVPNTheme
import java.text.SimpleDateFormat
import java.util.*

// ==================== Enums & Data Models ====================

enum class TransactionType {
    SEND,
    RECEIVE,
    SWAP
}

enum class TransactionStatus {
    PENDING,
    CONFIRMED,
    FAILED
}

data class TransactionItem(
    val id: String,
    val type: TransactionType,
    val amount: String,
    val symbol: String,
    val usdValue: String,
    val address: String,
    val timestamp: Long,
    val status: TransactionStatus,
    val fee: String? = null
)

data class AssetDetailState(
    val symbol: String = "SOL",
    val name: String = "Solana",
    val balance: String = "45.2345",
    val usdValue: String = "4,523.45",
    val priceChange24h: String = "+5.2%",
    val isPositiveChange: Boolean = true,
    val walletAddress: String = "HN7cABqLq46Es1jh92dQQisAq662SmxELLLsHHe4YWrH",
    val transactions: List<TransactionItem> = emptyList()
)

// ==================== ViewModel ====================

class AssetDetailViewModel {
    var state by mutableStateOf(AssetDetailState())
        private set

    init {
        loadTransactions()
    }

    private fun loadTransactions() {
        val now = System.currentTimeMillis()
        val sampleTransactions = listOf(
            TransactionItem(
                id = "tx1",
                type = TransactionType.RECEIVE,
                amount = "10.5",
                symbol = "SOL",
                usdValue = "1,050.00",
                address = "HN7c...YWrH",
                timestamp = now - 3600000,
                status = TransactionStatus.CONFIRMED,
                fee = "0.000005"
            ),
            TransactionItem(
                id = "tx2",
                type = TransactionType.SEND,
                amount = "5.0",
                symbol = "SOL",
                usdValue = "500.00",
                address = "8xDi...3kLmN",
                timestamp = now - 86400000,
                status = TransactionStatus.CONFIRMED,
                fee = "0.000005"
            ),
            TransactionItem(
                id = "tx3",
                type = TransactionType.SWAP,
                amount = "100.0",
                symbol = "USDC",
                usdValue = "100.00",
                address = "Swap",
                timestamp = now - 172800000,
                status = TransactionStatus.CONFIRMED,
                fee = "0.00001"
            ),
            TransactionItem(
                id = "tx4",
                type = TransactionType.SEND,
                amount = "2.5",
                symbol = "SOL",
                usdValue = "250.00",
                address = "5xAb...9pQrS",
                timestamp = now - 259200000,
                status = TransactionStatus.PENDING,
                fee = "0.000005"
            )
        )
        state = state.copy(transactions = sampleTransactions)
    }

    fun onCopyAddress() {
        // Copy to clipboard
    }

    fun onSendClick() {
        // Navigate to send
    }

    fun onReceiveClick() {
        // Navigate to receive
    }

    fun onTransactionClick(transactionId: String) {
        // Navigate to transaction detail
    }
}

// ==================== Page Composable ====================

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailPage(
    viewModel: AssetDetailViewModel = remember { AssetDetailViewModel() },
    onBackClick: () -> Unit = {},
    onSendClick: () -> Unit = {},
    onReceiveClick: () -> Unit = {},
    onTransactionClick: (String) -> Unit = {}
) {
    val state = viewModel.state

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "${state.name} (${state.symbol})",
                        color = Color.White,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF0B1020)
                )
            )
        },
        containerColor = Color(0xFF0B1020)
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Token Info Header
            item {
                TokenInfoHeader(state = state)
            }

            // Address Card
            item {
                AddressCard(
                    address = state.walletAddress,
                    onCopyClick = { viewModel.onCopyAddress() }
                )
            }

            // Action Buttons
            item {
                AssetActionButtons(
                    onSendClick = {
                        viewModel.onSendClick()
                        onSendClick()
                    },
                    onReceiveClick = {
                        viewModel.onReceiveClick()
                        onReceiveClick()
                    }
                )
            }

            // Transactions Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "交易记录",
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    TextButton(onClick = { /* View all */ }) {
                        Text(
                            text = "查看全部",
                            color = Color(0xFF1D4ED8),
                            fontSize = 12.sp
                        )
                    }
                }
            }

            // Transactions List
            items(state.transactions) { transaction ->
                TransactionCard(
                    transaction = transaction,
                    onClick = {
                        viewModel.onTransactionClick(transaction.id)
                        onTransactionClick(transaction.id)
                    }
                )
            }
        }
    }
}

@Composable
private fun TokenInfoHeader(state: AssetDetailState) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Token Icon
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(Color(0xFF9945FF)),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = state.symbol.take(2),
                color = Color.White,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Balance
        Text(
            text = "${state.balance} ${state.symbol}",
            color = Color.White,
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(8.dp))

        // USD Value and Change
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "$${state.usdValue}",
                color = Color(0xFF9CA3AF),
                fontSize = 16.sp
            )
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(4.dp))
                    .background(
                        if (state.isPositiveChange) Color(0xFF22C55E).copy(alpha = 0.2f)
                        else Color(0xFFEF4444).copy(alpha = 0.2f)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = state.priceChange24h,
                    color = if (state.isPositiveChange) Color(0xFF22C55E) else Color(0xFFEF4444),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun AddressCard(
    address: String,
    onCopyClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "钱包地址",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = address,
                    color = Color.White,
                    fontSize = 13.sp,
                    fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                )
            }
            IconButton(onClick = onCopyClick) {
                Icon(
                    imageVector = Icons.Default.ContentCopy,
                    contentDescription = "Copy",
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun AssetActionButtons(
    onSendClick: () -> Unit,
    onReceiveClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onSendClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF1D4ED8)
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowUpward,
                contentDescription = "Send",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("发送", fontSize = 14.sp)
        }

        Button(
            onClick = onReceiveClick,
            modifier = Modifier
                .weight(1f)
                .height(48.dp),
            shape = RoundedCornerShape(12.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF22C55E)
            )
        ) {
            Icon(
                imageVector = Icons.Default.ArrowDownward,
                contentDescription = "Receive",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("收款", fontSize = 14.sp)
        }
    }
}

@Composable
private fun TransactionCard(
    transaction: TransactionItem,
    onClick: () -> Unit
) {
    val (icon, iconColor, bgColor, typeLabel) = when (transaction.type) {
        TransactionType.SEND -> Quadruple(
            Icons.Default.ArrowUpward,
            Color(0xFFEF4444),
            Color(0xFFEF4444).copy(alpha = 0.2f),
            "发送"
        )
        TransactionType.RECEIVE -> Quadruple(
            Icons.Default.ArrowDownward,
            Color(0xFF22C55E),
            Color(0xFF22C55E).copy(alpha = 0.2f),
            "接收"
        )
        TransactionType.SWAP -> Quadruple(
            Icons.Default.SwapHoriz,
            Color(0xFF1D4ED8),
            Color(0xFF1D4ED8).copy(alpha = 0.2f),
            "兑换"
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1F2937)
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Type Icon
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(bgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = typeLabel,
                        tint = iconColor,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = typeLabel,
                            color = Color.White,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        TransactionStatusBadge(status = transaction.status)
                    }
                    Text(
                        text = transaction.address,
                        color = Color(0xFF9CA3AF),
                        fontSize = 12.sp
                    )
                    Text(
                        text = formatTime(transaction.timestamp),
                        color = Color(0xFF6B7280),
                        fontSize = 11.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${if (transaction.type == TransactionType.SEND) "-" else "+"}${transaction.amount} ${transaction.symbol}",
                    color = when (transaction.type) {
                        TransactionType.SEND -> Color(0xFFEF4444)
                        TransactionType.RECEIVE -> Color(0xFF22C55E)
                        TransactionType.SWAP -> Color(0xFF1D4ED8)
                    },
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    text = "$${transaction.usdValue}",
                    color = Color(0xFF9CA3AF),
                    fontSize = 12.sp
                )
            }
        }
    }
}

@Composable
private fun TransactionStatusBadge(status: TransactionStatus) {
    val (bgColor, textColor, label) = when (status) {
        TransactionStatus.PENDING -> Triple(
            Color(0xFFF59E0B).copy(alpha = 0.2f),
            Color(0xFFF59E0B),
            "确认中"
        )
        TransactionStatus.CONFIRMED -> Triple(
            Color(0xFF22C55E).copy(alpha = 0.2f),
            Color(0xFF22C55E),
            "已完成"
        )
        TransactionStatus.FAILED -> Triple(
            Color(0xFFEF4444).copy(alpha = 0.2f),
            Color(0xFFEF4444),
            "失败"
        )
    }

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(bgColor)
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

// Helper data class for 4 values
private data class Quadruple<A, B, C, D>(
    val first: A,
    val second: B,
    val third: C,
    val fourth: D
)

// ==================== Helper Functions ====================

private fun formatTime(timestamp: Long): String {
    val sdf = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

// ==================== Preview ====================

@Preview(device = "id:pixel_5")
@Composable
private fun AssetDetailPagePreview() {
    CryptoVPNTheme {
        AssetDetailPage()
    }
}

@Preview
@Composable
private fun TransactionCardPreview() {
    CryptoVPNTheme {
        TransactionCard(
            transaction = TransactionItem(
                id = "tx1",
                type = TransactionType.RECEIVE,
                amount = "10.5",
                symbol = "SOL",
                usdValue = "1,050.00",
                address = "HN7c...YWrH",
                timestamp = System.currentTimeMillis(),
                status = TransactionStatus.CONFIRMED,
                fee = "0.000005"
            ),
            onClick = {}
        )
    }
}