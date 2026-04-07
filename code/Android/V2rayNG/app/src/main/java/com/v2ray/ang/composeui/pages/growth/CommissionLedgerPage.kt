package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowCircleUp
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Savings
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.bridge.growth.GrowthBridgeRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

private val LedgerBg = Color(0xFF0A101A)
private val LedgerSurface = Color(0xFF121A28)
private val LedgerSurfaceSoft = Color(0xFF1A2638)
private val LedgerPrimary = Color(0xFF00E5A8)
private val LedgerPrimarySoft = Color(0x3313F1B2)
private val LedgerText = Color(0xFFEAF0F7)
private val LedgerMuted = Color(0xFF8D9AB0)
private val LedgerDanger = Color(0xFFF45B69)

/**
 * 佣金记录类型
 */
enum class CommissionType {
    INVITE,
    REBATE,
    WITHDRAW
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
                    amount = "${it.settlementAmountUsdt} USDT",
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
                    totalCommission = "${summary.withdrawnTotal} USDT",
                    availableCommission = "${summary.availableAmount} USDT",
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
        containerColor = LedgerBg,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Growth / Ledger",
                        color = LedgerText,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = LedgerText
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = LedgerBg,
                    titleContentColor = LedgerText,
                    navigationIconContentColor = LedgerText
                )
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(LedgerBg)
                .padding(paddingValues)
        ) {
            when (val current = state) {
                is CommissionLedgerState.Loaded -> {
                    CommissionLedgerContent(
                        state = current,
                        onNavigateToWithdraw = onNavigateToWithdraw
                    )
                }

                is CommissionLedgerState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = LedgerPrimary)
                    }
                }

                is CommissionLedgerState.Empty -> {
                    EmptyCommissionView()
                }

                is CommissionLedgerState.Error -> {
                    LedgerErrorView(message = current.message)
                }

                else -> Unit
            }
        }
    }
}

@Composable
private fun CommissionLedgerContent(
    state: CommissionLedgerState.Loaded,
    onNavigateToWithdraw: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        CommissionStatsCard(
            totalCommission = state.totalCommission,
            availableCommission = state.availableCommission,
            onNavigateToWithdraw = onNavigateToWithdraw
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "流水明细",
                color = LedgerText,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Surface(
                shape = RoundedCornerShape(999.dp),
                color = LedgerSurface
            ) {
                Text(
                    text = "${state.records.size} 条",
                    color = LedgerMuted,
                    fontSize = 11.sp,
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                )
            }
        }

        LazyColumn(
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 0.dp, bottom = 18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(state.records) { record ->
                CommissionRecordItem(record = record)
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
            .padding(horizontal = 16.dp, vertical = 12.dp),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerSurface),
        border = BorderStroke(1.dp, LedgerPrimarySoft)
    ) {
        Column(
            modifier = Modifier
                .background(
                    Brush.linearGradient(
                        colors = listOf(Color(0x3322F5C6), Color.Transparent, Color(0x202D3E58))
                    )
                )
                .padding(18.dp)
        ) {
            Text(text = "可提现余额", color = LedgerMuted, fontSize = 12.sp)
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = availableCommission,
                fontSize = 38.sp,
                fontWeight = FontWeight.ExtraBold,
                color = LedgerText
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(text = "累计已结算", color = LedgerMuted, fontSize = 11.sp)
                    Text(
                        text = totalCommission,
                        color = LedgerText,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }
                Button(
                    onClick = onNavigateToWithdraw,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = LedgerPrimary,
                        contentColor = Color(0xFF072117)
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Text(text = "提现", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
private fun CommissionRecordItem(record: CommissionRecord) {
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())

    val (icon, accentColor, amountPrefix) = when (record.type) {
        CommissionType.INVITE -> Triple(Icons.Default.People, LedgerPrimary, "+")
        CommissionType.REBATE -> Triple(Icons.Default.Savings, Color(0xFF60C2FF), "+")
        CommissionType.WITHDRAW -> Triple(Icons.Default.ArrowCircleUp, LedgerDanger, "")
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = LedgerSurface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(accentColor.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = record.type.name,
                    tint = accentColor,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.size(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = record.description,
                    color = LedgerText,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = dateFormat.format(record.timestamp),
                    color = LedgerMuted,
                    fontSize = 11.sp
                )
                record.fromUser?.let { user ->
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "账号: $user",
                        color = LedgerMuted,
                        fontSize = 11.sp
                    )
                }
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "$amountPrefix${record.amount.removePrefix("+")}",
                    color = accentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    color = LedgerSurfaceSoft,
                    shape = RoundedCornerShape(999.dp)
                ) {
                    Text(
                        text = record.status,
                        color = LedgerMuted,
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyCommissionView() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Savings,
            contentDescription = null,
            tint = LedgerPrimary,
            modifier = Modifier.size(54.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "暂无账本记录", color = LedgerText, fontWeight = FontWeight.Bold, fontSize = 18.sp)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = "邀请好友并完成首单后，会在这里显示返佣流水", color = LedgerMuted, fontSize = 12.sp)
    }
}

@Composable
private fun LedgerErrorView(message: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 28.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = LedgerDanger,
            modifier = Modifier.size(50.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(text = "账本加载失败", color = LedgerDanger, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(6.dp))
        Text(text = message, color = LedgerMuted, fontSize = 12.sp)
    }
}

@Preview(showBackground = true)
@Composable
fun CommissionLedgerPagePreview() {
    MaterialTheme {
        CommissionLedgerPage()
    }
}
