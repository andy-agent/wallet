package com.cryptovpn.ui.pages.growth

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.cryptovpn.ui.theme.CryptoVPNTheme
import com.cryptovpn.ui.components.CommonTopAppBar
import com.cryptovpn.ui.components.LoadingIndicator
import java.math.BigDecimal
import java.text.SimpleDateFormat
import java.util.*

/**
 * 佣金账本页状态
 */
sealed class CommissionLedgerState {
    data object Loading : CommissionLedgerState()
    data class Loaded(
        val currentMonthEarnings: BigDecimal,
        val totalEarnings: BigDecimal,
        val selectedFilter: CommissionFilter,
        val transactions: List<CommissionTransaction>
    ) : CommissionLedgerState()
    data class Empty(
        val message: String = "暂无佣金记录"
    ) : CommissionLedgerState()
    data class Error(
        val message: String,
        val canRetry: Boolean = true
    ) : CommissionLedgerState()
}

/**
 * 佣金筛选类型
 */
enum class CommissionFilter {
    ALL, LEVEL1, LEVEL2, FROZEN
}

/**
 * 佣金交易记录
 */
data class CommissionTransaction(
    val id: String,
    val level: CommissionLevel,
    val amount: BigDecimal,
    val source: String,
    val status: TransactionStatus,
    val timestamp: Long,
    val description: String? = null
)

/**
 * 佣金层级
 */
enum class CommissionLevel {
    LEVEL1, LEVEL2
}

/**
 * 交易状态
 */
enum class TransactionStatus {
    PENDING, CONFIRMED, FROZEN, WITHDRAWN
}

/**
 * 佣金账本页
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommissionLedgerPage(
    onNavigateBack: () -> Unit = {},
    viewModel: CommissionLedgerViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var showDetailDialog by remember { mutableStateOf<CommissionTransaction?>(null) }

    Scaffold(
        topBar = {
            CommonTopAppBar(
                title = "佣金账本",
                onNavigateBack = onNavigateBack
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0B1020))
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is CommissionLedgerState.Loading -> {
                    LoadingIndicator()
                }
                is CommissionLedgerState.Loaded -> {
                    LedgerContent(
                        state = currentState,
                        onFilterSelected = { viewModel.setFilter(it) },
                        onTransactionClick = { showDetailDialog = it }
                    )
                }
                is CommissionLedgerState.Empty -> {
                    EmptyView(message = currentState.message)
                }
                is CommissionLedgerState.Error -> {
                    ErrorRetryView(
                        message = currentState.message,
                        canRetry = currentState.canRetry,
                        onRetry = { viewModel.retry() }
                    )
                }
            }
        }
    }

    // 详情对话框
    showDetailDialog?.let { transaction ->
        TransactionDetailDialog(
            transaction = transaction,
            onDismiss = { showDetailDialog = null }
        )
    }
}

@Composable
private fun LedgerContent(
    state: CommissionLedgerState.Loaded,
    onFilterSelected: (CommissionFilter) -> Unit,
    onTransactionClick: (CommissionTransaction) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 摘要卡片
        SummaryCard(
            currentMonthEarnings = state.currentMonthEarnings,
            totalEarnings = state.totalEarnings
        )
        
        // 筛选标签
        FilterTabs(
            selectedFilter = state.selectedFilter,
            onFilterSelected = onFilterSelected
        )
        
        // 交易列表
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(state.transactions) { transaction ->
                TransactionItem(
                    transaction = transaction,
                    onClick = { onTransactionClick(transaction) }
                )
            }
            
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun SummaryCard(
    currentMonthEarnings: BigDecimal,
    totalEarnings: BigDecimal
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFF1F2937))
            .padding(20.dp)
    ) {
        Text(
            text = "收益摘要",
            color = Color.White,
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "本月收益",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$currentMonthEarnings",
                    color = Color(0xFF22C55E),
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            
            Divider(
                color = Color(0xFF374151),
                modifier = Modifier
                    .width(1.dp)
                    .height(50.dp)
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp)
            ) {
                Text(
                    text = "累计收益",
                    color = Color(0xFF9CA3AF),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$$totalEarnings",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun FilterTabs(
    selectedFilter: CommissionFilter,
    onFilterSelected: (CommissionFilter) -> Unit
) {
    val filters = listOf(
        CommissionFilter.ALL to "全部",
        CommissionFilter.LEVEL1 to "一级",
        CommissionFilter.LEVEL2 to "二级",
        CommissionFilter.FROZEN to "冻结"
    )
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        filters.forEach { (filter, label) ->
            val isSelected = filter == selectedFilter
            
            FilterChip(
                selected = isSelected,
                onClick = { onFilterSelected(filter) },
                label = {
                    Text(
                        text = label,
                        fontSize = 13.sp,
                        fontWeight = if (isSelected) FontWeight.Medium else FontWeight.Normal
                    )
                },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Color(0xFF1D4ED8),
                    selectedLabelColor = Color.White,
                    containerColor = Color(0xFF1F2937),
                    labelColor = Color(0xFF9CA3AF)
                ),
                border = if (!isSelected) {
                    androidx.compose.foundation.BorderStroke(
                        width = 1.dp,
                        color = Color(0xFF374151)
                    )
                } else null,
                shape = RoundedCornerShape(20.dp)
            )
        }
    }
}

@Composable
private fun TransactionItem(
    transaction: CommissionTransaction,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF1F2937))
            .clickable(onClick = onClick)
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 层级标签
        LevelBadge(level = transaction.level)
        
        Spacer(modifier = Modifier.width(12.dp))
        
        // 信息
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = transaction.source,
                    color = Color.White,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                StatusBadge(status = transaction.status)
            }
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = formatTimestamp(transaction.timestamp),
                color = Color(0xFF6B7280),
                fontSize = 12.sp
            )
        }
        
        // 金额
        Column(horizontalAlignment = Alignment.End) {
            Text(
                text = "+${transaction.amount}",
                color = Color(0xFF22C55E),
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
            
            if (transaction.status == TransactionStatus.FROZEN) {
                Text(
                    text = "冻结中",
                    color = Color(0xFFF59E0B),
                    fontSize = 11.sp
                )
            }
        }
    }
}

@Composable
private fun LevelBadge(level: CommissionLevel) {
    val (text, color) = when (level) {
        CommissionLevel.LEVEL1 -> "一级" to Color(0xFF22C55E)
        CommissionLevel.LEVEL2 -> "二级" to Color(0xFF3B82F6)
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .background(color.copy(alpha = 0.2f))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusBadge(status: TransactionStatus) {
    val (text, color) = when (status) {
        TransactionStatus.PENDING -> "待确认" to Color(0xFFF59E0B)
        TransactionStatus.CONFIRMED -> "已确认" to Color(0xFF22C55E)
        TransactionStatus.FROZEN -> "冻结" to Color(0xFFEF4444)
        TransactionStatus.WITHDRAWN -> "已提现" to Color(0xFF9CA3AF)
    }
    
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(4.dp))
            .border(
                width = 1.dp,
                color = color.copy(alpha = 0.5f),
                shape = RoundedCornerShape(4.dp)
            )
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp
        )
    }
}

@Composable
private fun TransactionDetailDialog(
    transaction: CommissionTransaction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF1F2937),
        title = {
            Text(
                text = "交易详情",
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold
            )
        },
        text = {
            Column {
                // 金额
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "+${transaction.amount}",
                        color = Color(0xFF22C55E),
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                
                Spacer(modifier = Modifier.height(20.dp))
                
                Divider(color = Color(0xFF374151))
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // 详情项
                DetailRow("交易ID", transaction.id.take(16) + "...")
                DetailRow("佣金层级", if (transaction.level == CommissionLevel.LEVEL1) "一级邀请" else "二级邀请")
                DetailRow("来源用户", transaction.source)
                DetailRow("交易时间", formatTimestamp(transaction.timestamp))
                DetailRow("状态", getStatusText(transaction.status))
                
                transaction.description?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    DetailRow("备注", it)
                }
            }
        },
        confirmButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("关闭")
            }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            color = Color(0xFF9CA3AF),
            fontSize = 14.sp
        )
        Text(
            text = value,
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun EmptyView(message: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.ReceiptLong,
            contentDescription = null,
            tint = Color(0xFF6B7280),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color(0xFF9CA3AF),
            fontSize = 16.sp
        )
    }
}

@Composable
private fun ErrorRetryView(
    message: String,
    canRetry: Boolean,
    onRetry: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ErrorOutline,
            contentDescription = null,
            tint = Color(0xFFEF4444),
            modifier = Modifier.size(64.dp)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = message,
            color = Color.White,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(horizontal = 32.dp)
        )
        if (canRetry) {
            Spacer(modifier = Modifier.height(24.dp))
            Button(
                onClick = onRetry,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1D4ED8)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("重试")
            }
        }
    }
}

private fun formatTimestamp(timestamp: Long): String {
    val date = Date(timestamp)
    val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
    return formatter.format(date)
}

private fun getStatusText(status: TransactionStatus): String {
    return when (status) {
        TransactionStatus.PENDING -> "待确认"
        TransactionStatus.CONFIRMED -> "已确认"
        TransactionStatus.FROZEN -> "冻结中"
        TransactionStatus.WITHDRAWN -> "已提现"
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun CommissionLedgerPagePreview() {
    CryptoVPNTheme {
        LedgerContent(
            state = CommissionLedgerState.Loaded(
                currentMonthEarnings = BigDecimal("45.50"),
                totalEarnings = BigDecimal("170.75"),
                selectedFilter = CommissionFilter.ALL,
                transactions = listOf(
                    CommissionTransaction(
                        id = "TXN001",
                        level = CommissionLevel.LEVEL1,
                        amount = BigDecimal("10.00"),
                        source = "User123***",
                        status = TransactionStatus.CONFIRMED,
                        timestamp = System.currentTimeMillis(),
                        description = "充值佣金"
                    ),
                    CommissionTransaction(
                        id = "TXN002",
                        level = CommissionLevel.LEVEL2,
                        amount = BigDecimal("5.00"),
                        source = "User456***",
                        status = TransactionStatus.FROZEN,
                        timestamp = System.currentTimeMillis() - 86400000,
                        description = "充值佣金"
                    ),
                    CommissionTransaction(
                        id = "TXN003",
                        level = CommissionLevel.LEVEL1,
                        amount = BigDecimal("20.00"),
                        source = "User789***",
                        status = TransactionStatus.WITHDRAWN,
                        timestamp = System.currentTimeMillis() - 172800000
                    )
                )
            ),
            onFilterSelected = {},
            onTransactionClick = {}
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFF0B1020)
@Composable
fun CommissionLedgerPageEmptyPreview() {
    CryptoVPNTheme {
        EmptyView(message = "暂无佣金记录")
    }
}
