package com.v2ray.ang.composeui.pages.growth

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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.growth.GrowthBridgeRepository
import android.app.Application
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

/**
 * 佣金记录类型
 */
enum class CommissionType {
    INVITE,     // 邀请奖励
    REBATE,     // 返利
    WITHDRAW    // 提现
}

/**
 * 佣金记录
 */
data class CommissionRecord(
    val id: String,
    val type: CommissionType,
    val amount: String,
    val description: String,
    val fromUser: String?,
    val timestamp: Date,
    val status: String
)

/**
 * 佣金账本页状态
 */
sealed class CommissionLedgerState {
    object Idle : CommissionLedgerState()
    object Loading : CommissionLedgerState()
    data class Loaded(
        val totalCommission: String,
        val availableCommission: String,
        val records: List<CommissionRecord>
    ) : CommissionLedgerState()
    data class Error(val message: String) : CommissionLedgerState()
    object Empty : CommissionLedgerState()
}

/**
 * 佣金账本页ViewModel
 */
class CommissionLedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val growthBridgeRepository = GrowthBridgeRepository(application)
    private val _state = MutableStateFlow<CommissionLedgerState>(CommissionLedgerState.Idle)
    val state: StateFlow<CommissionLedgerState> = _state

    init {
        loadCommissionData()
    }

    private fun loadCommissionData() {
        viewModelScope.launch {
            _state.value = CommissionLedgerState.Loading

            val summaryResult = growthBridgeRepository.getCommissionSummary()
            val ledgerResult = growthBridgeRepository.getCommissionLedger()
            if (summaryResult.isFailure) {
                _state.value = CommissionLedgerState.Error(summaryResult.exceptionOrNull()?.message ?: "加载佣金概览失败")
                return@launch
            }
            if (ledgerResult.isFailure) {
                _state.value = CommissionLedgerState.Error(ledgerResult.exceptionOrNull()?.message ?: "加载佣金账本失败")
                return@launch
            }

            val summary = summaryResult.getOrNull() ?: run {
                _state.value = CommissionLedgerState.Error("佣金概览为空")
                return@launch
            }
            val records = (ledgerResult.getOrNull() ?: emptyList()).map {
                CommissionRecord(
                    id = it.entryNo,
                    type = when {
                        it.status.equals("SETTLED", true) -> CommissionType.WITHDRAW
                        it.commissionLevel.equals("LEVEL2", true) -> CommissionType.REBATE
                        else -> CommissionType.INVITE
                    },
                    amount = "+$${it.settlementAmountUsdt}",
                    description = "来源订单 ${it.sourceOrderNo}",
                    fromUser = it.sourceAccountMasked,
                    timestamp = Date(),
                    status = it.status
                )
            }

            _state.value = if (records.isEmpty()) {
                CommissionLedgerState.Empty
            } else {
                CommissionLedgerState.Loaded(
                    totalCommission = "$${summary.withdrawnTotal}",
                    availableCommission = "$${summary.availableAmount}",
                    records = records
                )
            }
        }
    }
}

/**
 * 佣金账本页
 * 显示佣金记录列表
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommissionLedgerPage(
    viewModel: CommissionLedgerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("佣金账本") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
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
                is CommissionLedgerState.Loaded -> {
                    val loadedState = state as CommissionLedgerState.Loaded
                    CommissionLedgerContent(
                        state = loadedState,
                        onNavigateToWithdraw = onNavigateToWithdraw
                    )
                }
                is CommissionLedgerState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                is CommissionLedgerState.Empty -> {
                    EmptyCommissionView()
                }
                is CommissionLedgerState.Error -> {
                    ErrorView(message = (state as CommissionLedgerState.Error).message)
                }
                else -> {}
            }
        }
    }
}

@Composable
private fun CommissionLedgerContent(
    state: CommissionLedgerState.Loaded,
    onNavigateToWithdraw: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 佣金统计卡片
        CommissionStatsCard(
            totalCommission = state.totalCommission,
            availableCommission = state.availableCommission,
            onNavigateToWithdraw = onNavigateToWithdraw
        )

        // 记录列表标题
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "收支明细",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "${state.records.size} 条记录",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // 记录列表
        LazyColumn(
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(state.records) { record ->
                CommissionRecordItem(record = record)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun CommissionStatsCard(
    totalCommission: String,
    availableCommission: String,
    onNavigateToWithdraw: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        )
    ) {
        Column(
            modifier = Modifier.padding(24.dp)
        ) {
            // 可用佣金
            Text(
                text = "可提现佣金",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = availableCommission,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
                
                Button(
                    onClick = onNavigateToWithdraw,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary,
                        contentColor = MaterialTheme.colorScheme.primary
                    ),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Text(
                        text = "提现",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Divider(color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.2f))
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // 累计佣金
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "累计佣金",
                    fontSize = 14.sp,
                    color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f)
                )
                Text(
                    text = totalCommission,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Composable
private fun CommissionRecordItem(record: CommissionRecord) {
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    
    val (icon, iconColor, amountColor) = when (record.type) {
        CommissionType.INVITE, CommissionType.REBATE -> Triple(
            Icons.Default.PersonAdd,
            Color(0xFF22C55E),
            Color(0xFF22C55E)
        )
        CommissionType.WITHDRAW -> Triple(
            Icons.Default.ArrowUpward,
            Color(0xFFEF4444),
            Color(0xFFEF4444)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
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
                        contentDescription = record.type.name,
                        tint = iconColor,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // 记录信息
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.description,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Spacer(modifier = Modifier.height(4.dp))
                
                Row {
                    Text(
                        text = dateFormat.format(record.timestamp),
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    record.fromUser?.let { user ->
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "来自: $user",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // 金额和状态
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = record.amount,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = amountColor
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = record.status,
                    fontSize = 11.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun EmptyCommissionView() {
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.AccountBalance,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = "暂无记录",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = "邀请好友即可获得佣金",
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
        )
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
fun CommissionLedgerPagePreview() {
    MaterialTheme {
        CommissionLedgerPage()
    }
}
