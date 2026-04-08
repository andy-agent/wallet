package com.v2ray.ang.composeui.pages.growth

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

private enum class CommissionFilter(val label: String) {
    ALL("全部"),
    AVAILABLE("可用"),
    SETTLED("已结算"),
}

enum class CommissionType(
    val badge: String,
    val tint: Color,
    val icon: ImageVector,
) {
    LEVEL1("一级邀请", GrowthPositive, Icons.Default.Groups),
    LEVEL2("二级返佣", GrowthAccent, Icons.Default.AutoGraph),
}

data class CommissionRecord(
    val id: String,
    val type: CommissionType,
    val amount: String,
    val sourceOrderNo: String,
    val sourceAccountMasked: String,
    val sourceAssetCode: String,
    val sourceAmount: String,
    val status: String,
    val createdAt: String,
)

sealed class CommissionLedgerState {
    data object Loading : CommissionLedgerState()

    data class Loaded(
        val settlementAssetCode: String,
        val settlementNetworkCode: String,
        val availableAmount: String,
        val frozenAmount: String,
        val withdrawingAmount: String,
        val withdrawnTotal: String,
        val records: List<CommissionRecord>,
    ) : CommissionLedgerState()

    data class Error(val message: String) : CommissionLedgerState()
}

class CommissionLedgerViewModel(application: Application) : AndroidViewModel(application) {
    private val growthBridgeRepository = GrowthBridgeRepository(application)
    private val _state = MutableStateFlow<CommissionLedgerState>(CommissionLedgerState.Loading)
    val state: StateFlow<CommissionLedgerState> = _state

    init {
        loadCommissionData()
    }

    fun loadCommissionData() {
        viewModelScope.launch {
            _state.value = CommissionLedgerState.Loading

            val summaryResult = growthBridgeRepository.getCommissionSummary()
            val ledgerResult = growthBridgeRepository.getCommissionLedger()
            if (summaryResult.isFailure) {
                _state.value = CommissionLedgerState.Error(
                    summaryResult.exceptionOrNull()?.message ?: "加载佣金概览失败",
                )
                return@launch
            }
            if (ledgerResult.isFailure) {
                _state.value = CommissionLedgerState.Error(
                    ledgerResult.exceptionOrNull()?.message ?: "加载佣金账本失败",
                )
                return@launch
            }

            val summary = summaryResult.getOrNull()
            val records = ledgerResult.getOrNull()
            if (summary == null || records == null) {
                _state.value = CommissionLedgerState.Error("账本数据为空")
                return@launch
            }

            _state.value = CommissionLedgerState.Loaded(
                settlementAssetCode = summary.settlementAssetCode,
                settlementNetworkCode = summary.settlementNetworkCode,
                availableAmount = summary.availableAmount,
                frozenAmount = summary.frozenAmount,
                withdrawingAmount = summary.withdrawingAmount,
                withdrawnTotal = summary.withdrawnTotal,
                records = records.map {
                    CommissionRecord(
                        id = it.entryNo,
                        type = if (it.commissionLevel.equals("LEVEL2", true)) {
                            CommissionType.LEVEL2
                        } else {
                            CommissionType.LEVEL1
                        },
                        amount = "${it.settlementAmountUsdt} ${summary.settlementAssetCode}",
                        sourceOrderNo = it.sourceOrderNo,
                        sourceAccountMasked = it.sourceAccountMasked,
                        sourceAssetCode = it.sourceAssetCode,
                        sourceAmount = it.sourceAmount,
                        status = it.status,
                        createdAt = it.createdAt,
                    )
                },
            )
        }
    }
}

@Composable
fun CommissionLedgerPage(
    viewModel: CommissionLedgerViewModel = androidx.lifecycle.viewmodel.compose.viewModel(),
    onNavigateBack: () -> Unit = {},
    onNavigateToWithdraw: () -> Unit = {},
) {
    val state by viewModel.state.collectAsState()

    GrowthPageScaffold(
        topBar = {
            GrowthTopBar(
                title = "奖励记录",
                subtitle = "佣金账本与提现页共用同一结算节奏",
                onNavigateBack = onNavigateBack,
            )
        },
        bottomBar = {
            if (state is CommissionLedgerState.Loaded) {
                Surface(
                    color = GrowthPageBackground.copy(alpha = 0.98f),
                    tonalElevation = 0.dp,
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                    ) {
                        GrowthPrimaryButton(
                            text = "发起提现",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = onNavigateToWithdraw,
                        )
                    }
                }
            }
        },
    ) { paddingValues ->
        when (val currentState = state) {
            CommissionLedgerState.Loading -> Unit
            is CommissionLedgerState.Error -> {
                GrowthStatusView(
                    title = "账本加载失败",
                    message = currentState.message,
                    modifier = Modifier.padding(paddingValues),
                    action = {
                        GrowthPrimaryButton(
                            text = "重新加载",
                            modifier = Modifier.fillMaxWidth(),
                            onClick = viewModel::loadCommissionData,
                        )
                    },
                )
            }

            is CommissionLedgerState.Loaded -> {
                CommissionLedgerContent(
                    state = currentState,
                    paddingValues = paddingValues,
                )
            }
        }
    }
}

@Composable
private fun CommissionLedgerContent(
    state: CommissionLedgerState.Loaded,
    paddingValues: PaddingValues,
) {
    var filter by rememberSaveable { mutableStateOf(CommissionFilter.ALL) }
    val filteredRecords = state.records.filter {
        when (filter) {
            CommissionFilter.ALL -> true
            CommissionFilter.AVAILABLE -> it.status.equals("AVAILABLE", true)
            CommissionFilter.SETTLED -> it.status.equals("SETTLED", true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(
            start = 20.dp,
            end = 20.dp,
            top = 6.dp,
            bottom = 110.dp,
        ),
        verticalArrangement = Arrangement.spacedBy(18.dp),
    ) {
        item {
            CommissionSummaryHero(state = state)
        }

        item {
            GrowthSectionCard(contentPadding = PaddingValues(horizontal = 20.dp, vertical = 18.dp)) {
                GrowthSectionTitle(
                    title = "账本筛选",
                    subtitle = "优先展示摘要区，再通过小型 tabs 切换状态，不把筛选抢到首屏标题上方。",
                )
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    CommissionFilter.entries.forEach { item ->
                        Surface(
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(Color.Transparent)
                                .padding(0.dp),
                            shape = RoundedCornerShape(999.dp),
                            color = if (item == filter) GrowthSurfaceStrong else GrowthSurfaceRaised,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (item == filter) GrowthAccent else GrowthBorder,
                            ),
                        ) {
                            Text(
                                text = item.label,
                                color = if (item == filter) GrowthTextPrimary else GrowthTextSecondary,
                                fontWeight = if (item == filter) FontWeight.SemiBold else FontWeight.Medium,
                                modifier = Modifier
                                    .padding(horizontal = 16.dp, vertical = 10.dp)
                                    .clickable { filter = item },
                            )
                        }
                    }
                }
            }
        }

        item {
            GrowthSectionCard {
                GrowthSectionTitle(
                    title = "收益流水",
                    subtitle = "缺图页复用“单摘要区 + 连续内容区”的法则，把所有记录收纳进同一卡组。",
                    trailing = { GrowthBadge(text = "${filteredRecords.size} 条") },
                )

                if (filteredRecords.isEmpty()) {
                    Surface(
                        shape = RoundedCornerShape(22.dp),
                        color = GrowthSurfaceRaised,
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 18.dp, vertical = 20.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                        ) {
                            Text(
                                text = "当前筛选下暂无记录",
                                color = GrowthTextPrimary,
                                fontWeight = FontWeight.SemiBold,
                            )
                            Text(
                                text = "邀请好友下单后，返佣明细会按时间顺序出现在这里。",
                                color = GrowthTextSecondary,
                                fontSize = 13.sp,
                                lineHeight = 18.sp,
                            )
                        }
                    }
                } else {
                    filteredRecords.forEachIndexed { index, record ->
                        CommissionRecordRow(record = record)
                        if (index != filteredRecords.lastIndex) {
                            GrowthListDivider()
                        }
                    }
                }
            }
        }

        item { Spacer(modifier = Modifier.height(8.dp)) }
    }
}

@Composable
private fun CommissionSummaryHero(state: CommissionLedgerState.Loaded) {
    GrowthHighlightCard {
        GrowthBadge(text = "${state.settlementAssetCode} / ${state.settlementNetworkCode}")
        Text(
            text = "${state.availableAmount} ${state.settlementAssetCode}",
            color = GrowthTextPrimary,
            fontSize = 34.sp,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "当前可提现佣金",
            color = GrowthTextSecondary,
            fontSize = 14.sp,
        )
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            GrowthStatChip(
                label = "冻结中",
                value = state.frozenAmount,
                modifier = Modifier.weight(1f),
            )
            GrowthStatChip(
                label = "提现中",
                value = state.withdrawingAmount,
                modifier = Modifier.weight(1f),
            )
            GrowthStatChip(
                label = "累计到账",
                value = state.withdrawnTotal,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun CommissionRecordRow(record: CommissionRecord) {
    val statusColor = when (record.status.uppercase()) {
        "AVAILABLE" -> GrowthPositive
        "SETTLED" -> GrowthAccent
        "FROZEN" -> GrowthWarningText
        else -> GrowthTextSecondary
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(14.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = record.type.tint.copy(alpha = 0.16f),
        ) {
            Box(
                modifier = Modifier.size(46.dp),
                contentAlignment = Alignment.Center,
            ) {
                Icon(
                    imageVector = record.type.icon,
                    contentDescription = null,
                    tint = record.type.tint,
                )
            }
        }

        androidx.compose.foundation.layout.Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top,
            ) {
                androidx.compose.foundation.layout.Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(5.dp),
                ) {
                    Text(
                        text = "来源订单 ${record.sourceOrderNo}",
                        color = GrowthTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                    Text(
                        text = "${formatLedgerDate(record.createdAt)}  ·  ${record.sourceAccountMasked}",
                        color = GrowthTextSecondary,
                        fontSize = 12.sp,
                    )
                }
                androidx.compose.foundation.layout.Column(
                    horizontalAlignment = Alignment.End,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Text(
                        text = record.amount,
                        color = GrowthTextPrimary,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                    )
                    GrowthBadge(
                        text = record.type.badge,
                        containerColor = record.type.tint.copy(alpha = 0.14f),
                        contentColor = record.type.tint,
                    )
                }
            }

            GrowthInfoRow(label = "状态", value = record.status, valueColor = statusColor, emphasize = true)
            GrowthInfoRow(
                label = "来源资产",
                value = "${record.sourceAmount} ${record.sourceAssetCode}",
            )
            GrowthInfoRow(
                label = "流水号",
                value = record.id.takeLast(10),
            )
        }
    }
}

private fun formatLedgerDate(value: String): String {
    val normalized = value.replace("T", " ").replace("Z", "")
    return if (normalized.length >= 16) normalized.substring(0, 16) else normalized
}

@Preview(showBackground = true)
@Composable
private fun CommissionLedgerPagePreview() {
    MaterialTheme {
        GrowthBitgetBackground {
            CommissionLedgerContent(
                state = CommissionLedgerState.Loaded(
                    settlementAssetCode = "USDT",
                    settlementNetworkCode = "SOLANA",
                    availableAmount = "96.24",
                    frozenAmount = "12.00",
                    withdrawingAmount = "10.00",
                    withdrawnTotal = "512.30",
                    records = listOf(
                        CommissionRecord(
                            id = "LEDGER-20260407-01",
                            type = CommissionType.LEVEL1,
                            amount = "12.50 USDT",
                            sourceOrderNo = "ORD-1048",
                            sourceAccountMasked = "zsc***rui@gmail.com",
                            sourceAssetCode = "USDT",
                            sourceAmount = "59.90",
                            status = "AVAILABLE",
                            createdAt = "2026-04-07T10:24:00Z",
                        ),
                        CommissionRecord(
                            id = "LEDGER-20260407-02",
                            type = CommissionType.LEVEL2,
                            amount = "6.20 USDT",
                            sourceOrderNo = "ORD-1062",
                            sourceAccountMasked = "xue***@mail.com",
                            sourceAssetCode = "USDT",
                            sourceAmount = "31.20",
                            status = "SETTLED",
                            createdAt = "2026-04-06T18:02:00Z",
                        ),
                    ),
                ),
                paddingValues = PaddingValues(),
            )
        }
    }
}
