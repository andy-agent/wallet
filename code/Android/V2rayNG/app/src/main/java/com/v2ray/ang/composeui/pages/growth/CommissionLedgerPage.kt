package com.v2ray.ang.composeui.pages.growth

import android.app.Application
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material.icons.filled.NorthEast
import androidx.compose.material.icons.filled.PeopleAlt
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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

enum class CommissionType {
    INVITE,
    REBATE,
    WITHDRAW
}

data class CommissionRecord(
    val id: String,
    val type: CommissionType,
    val amount: String,
    val description: String,
    val fromUser: String?,
    val timestamp: Date,
    val status: String
)

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
                    status = it.status,
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

@Composable
fun CommissionLedgerPage(
    viewModel: CommissionLedgerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    GrowthPageScaffold(
        title = "佣金账本",
        onNavigateBack = onNavigateBack
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(GrowthPageBackground)
                .padding(paddingValues)
        ) {
            when (val currentState = state) {
                is CommissionLedgerState.Loaded -> {
                    CommissionLedgerContent(
                        state = currentState,
                        onNavigateToWithdraw = onNavigateToWithdraw
                    )
                }

                is CommissionLedgerState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator(color = GrowthAccent)
                    }
                }

                is CommissionLedgerState.Empty -> {
                    GrowthStatusView(
                        title = "暂无账本",
                        message = "邀请好友并完成订单后，收益记录会以 Discover 流式卡片形式出现在这里。"
                    )
                }

                is CommissionLedgerState.Error -> {
                    GrowthStatusView(
                        title = "账本加载失败",
                        message = currentState.message
                    )
                }

                CommissionLedgerState.Idle -> Unit
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
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        CommissionOverviewCard(
            totalCommission = state.totalCommission,
            availableCommission = state.availableCommission,
            recordCount = state.records.size,
            onNavigateToWithdraw = onNavigateToWithdraw
        )

        GrowthSectionCard(modifier = Modifier.weight(1f), contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)) {
            Column(modifier = Modifier.padding(20.dp)) {
                GrowthSectionTitle(
                    title = "收益流水",
                    subtitle = "按 Discover feed 的节奏把账本主信息压缩进单卡：订单来源、账户掩码、状态与金额。",
                    trailing = { GrowthBadge(text = "${state.records.size} 条") }
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(start = 20.dp, end = 20.dp, bottom = 20.dp)
            ) {
                items(state.records) { record ->
                    CommissionRecordRow(record = record)
                }
            }
        }
    }
}

@Composable
private fun CommissionOverviewCard(
    totalCommission: String,
    availableCommission: String,
    recordCount: Int,
    onNavigateToWithdraw: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(28.dp))
            .background(GrowthHeroGradient)
            .padding(22.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text("Discover Ledger", color = Color(0xFF5E4300), fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Spacer(modifier = Modifier.height(8.dp))
                Text(availableCommission, color = Color(0xFF161A1E), fontSize = 34.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(6.dp))
                Text("当前可提现佣金", color = Color(0xFF5E4300), fontSize = 13.sp)
            }
            GrowthBadge(text = "实时结算")
        }

        Spacer(modifier = Modifier.height(20.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GrowthStatChip(label = "累计到账", value = totalCommission, modifier = Modifier.weight(1f))
            GrowthStatChip(label = "流水数量", value = recordCount.toString(), modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(18.dp))

        Button(
            onClick = onNavigateToWithdraw,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF161A1E),
                contentColor = GrowthTextPrimary
            ),
            shape = RoundedCornerShape(18.dp)
        ) {
            Text("发起提现", fontWeight = FontWeight.SemiBold)
        }
    }
}

@Composable
private fun CommissionRecordRow(record: CommissionRecord) {
    val dateFormat = SimpleDateFormat("MM-dd HH:mm", Locale.getDefault())
    val (icon, iconTint, amountTint, badgeText) = when (record.type) {
        CommissionType.INVITE -> LedgerVisual(Icons.Default.PeopleAlt, GrowthPositive, GrowthPositive, "邀请返佣")
        CommissionType.REBATE -> LedgerVisual(Icons.Default.Wallet, GrowthAccent, GrowthAccent, "二级返利")
        CommissionType.WITHDRAW -> LedgerVisual(Icons.Default.NorthEast, GrowthNegative, GrowthNegative, "提现支出")
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(GrowthSurfaceRaised)
            .padding(horizontal = 16.dp, vertical = 14.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(RoundedCornerShape(14.dp))
                        .background(iconTint.copy(alpha = 0.12f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(icon, contentDescription = badgeText, tint = iconTint)
                }
                Column(modifier = Modifier.padding(start = 12.dp)) {
                    Text(record.description, color = GrowthTextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = buildString {
                            append(dateFormat.format(record.timestamp))
                            if (!record.fromUser.isNullOrBlank()) {
                                append("  ·  ")
                                append(record.fromUser)
                            }
                        },
                        color = GrowthTextSecondary,
                        fontSize = 12.sp
                    )
                }
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(record.amount, color = amountTint, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Spacer(modifier = Modifier.height(4.dp))
                GrowthBadge(text = badgeText)
            }
        }

        Spacer(modifier = Modifier.height(14.dp))
        GrowthListDivider()
        Spacer(modifier = Modifier.height(12.dp))

        GrowthInfoRow(label = "状态", value = record.status, emphasize = true)
        Spacer(modifier = Modifier.height(8.dp))
        GrowthInfoRow(label = "流水号", value = record.id.takeLast(8))
    }
}

private data class LedgerVisual(
    val icon: ImageVector,
    val iconTint: Color,
    val amountTint: Color,
    val badgeText: String
)

@Preview(showBackground = true)
@Composable
private fun CommissionLedgerPagePreview() {
    MaterialTheme {
        CommissionLedgerContent(
            state = CommissionLedgerState.Loaded(
                totalCommission = "$512.30",
                availableCommission = "$96.24",
                records = listOf(
                    CommissionRecord(
                        id = "LEDGER-20260407-01",
                        type = CommissionType.INVITE,
                        amount = "+$12.00",
                        description = "来源订单 ORD-1048",
                        fromUser = "u***6",
                        timestamp = Date(),
                        status = "PENDING"
                    ),
                    CommissionRecord(
                        id = "LEDGER-20260407-02",
                        type = CommissionType.WITHDRAW,
                        amount = "-$50.00",
                        description = "提现申请 WD-88",
                        fromUser = null,
                        timestamp = Date(),
                        status = "SETTLED"
                    )
                )
            ),
            onNavigateToWithdraw = {}
        )
    }
}
