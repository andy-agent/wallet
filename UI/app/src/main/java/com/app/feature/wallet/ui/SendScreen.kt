package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.PrimaryButton
import com.app.common.components.SecondaryButton
import com.app.common.components.StatusChip
import com.app.common.widgets.MetricPill
import com.app.core.theme.CardGlassStrong
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.core.utils.Formatters
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.feature.wallet.viewmodel.formatWalletAmount
import java.math.BigDecimal

@Composable
fun SendScreen(
    symbol: String,
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSent: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val asset = state.assets.firstOrNull { it.symbol.equals(symbol, true) }
    var address by rememberSaveable(symbol) { mutableStateOf("") }
    var amount by rememberSaveable(symbol) { mutableStateOf("") }
    var reviewMode by rememberSaveable(symbol) { mutableStateOf(false) }
    var isSubmitting by rememberSaveable(symbol) { mutableStateOf(false) }
    var submittedTxId by rememberSaveable(symbol) { mutableStateOf<String?>(null) }
    val draft = viewModel.evaluateSend(symbol, address, amount)
    val network = draft?.network ?: viewModel.networkContext(symbol)
    val maxAmount = asset?.let { (it.balance - (network?.feeAmount ?: 0.0)).coerceAtLeast(0.0) } ?: 0.0

    AppScaffold(title = "发送资产", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GradientCard(title = "发送 ${asset?.symbol ?: symbol}", subtitle = asset?.name ?: "链上转账") {
                Text(
                    asset?.let { Formatters.money(it.balance * it.priceUsd) } ?: "--",
                    style = MaterialTheme.typography.headlineMedium,
                )
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricPill("网络", network?.chainName ?: "--")
                    MetricPill("可用余额", asset?.let { formatWalletAmount(it.balance, it.symbol) } ?: "--")
                }
                Spacer(Modifier.height(12.dp))
                InfoRow("预估到账", "${network?.settlementMinutes ?: 1} 分钟")
                InfoRow("币种价格", asset?.let { Formatters.money(it.priceUsd) } ?: "--")
            }

            GradientCard(title = "收款地址", subtitle = "仅支持当前网络的链上地址") {
                OutlinedTextField(
                    value = address,
                    onValueChange = {
                        address = it
                        if (reviewMode) reviewMode = false
                    },
                    label = { Text("收款地址") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardGlassStrong.copy(alpha = 0.76f),
                        focusedContainerColor = CardGlassStrong,
                    ),
                )
                Spacer(Modifier.height(10.dp))
                when {
                    draft?.addressError != null -> {
                        StatusChip(text = "地址校验失败", positive = false)
                        Spacer(Modifier.height(8.dp))
                        Text(draft.addressError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                    address.isNotBlank() -> {
                        StatusChip(text = "地址格式已通过 ${network?.chainName ?: "网络"} 校验", positive = true)
                    }
                    else -> {
                        Text("支持后续接扫码粘贴与常用地址簿。", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    }
                }
            }

            GradientCard(title = "发送数量", subtitle = "先校验金额，再进入确认页") {
                OutlinedTextField(
                    value = amount,
                    onValueChange = {
                        amount = it
                        if (reviewMode) reviewMode = false
                    },
                    label = { Text("数量") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardGlassStrong.copy(alpha = 0.76f),
                        focusedContainerColor = CardGlassStrong,
                    ),
                )
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    TextButton(onClick = { amount = editableAmount(maxAmount) }) {
                        Text("全部发送")
                    }
                }
                InfoRow("可发送上限", asset?.let { formatWalletAmount(maxAmount, it.symbol) } ?: "--")
                InfoRow("折合法币", Formatters.money(draft?.fiatAmount ?: 0.0))
                InfoRow(
                    "预估网络费",
                    asset?.let { formatWalletAmount(draft?.feeAmount ?: (network?.feeAmount ?: 0.0), it.symbol) } ?: "--",
                )
                InfoRow(
                    "总扣除",
                    asset?.let { formatWalletAmount(draft?.totalDeduction ?: 0.0, it.symbol) } ?: "--",
                )
                InfoRow(
                    "发送后余额",
                    asset?.let { formatWalletAmount(draft?.remainingBalance ?: it.balance, it.symbol) } ?: "--",
                )
                when {
                    draft?.amountError != null -> {
                        Spacer(Modifier.height(10.dp))
                        Text(draft.amountError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                    draft?.balanceError != null -> {
                        Spacer(Modifier.height(10.dp))
                        StatusChip(text = "余额不足", positive = false)
                        Spacer(Modifier.height(8.dp))
                        Text(draft.balanceError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            if (draft != null && reviewMode && submittedTxId == null) {
                GradientCard(title = "发送确认", subtitle = "广播前再次核对网络、地址与费用") {
                    InfoRow("发送资产", "${draft.asset.name} (${draft.asset.symbol})")
                    InfoRow("目标网络", draft.network.chainName)
                    InfoRow("收款地址", draft.address)
                    InfoRow("发送数量", formatWalletAmount(draft.amount, draft.asset.symbol))
                    InfoRow("到账数量", formatWalletAmount(draft.amount, draft.asset.symbol))
                    InfoRow("网络费", formatWalletAmount(draft.feeAmount, draft.asset.symbol))
                    InfoRow("总扣除", formatWalletAmount(draft.totalDeduction, draft.asset.symbol))
                    Spacer(Modifier.height(12.dp))
                    SecondaryButton(text = "返回修改", onClick = { reviewMode = false })
                    Spacer(Modifier.height(10.dp))
                    PrimaryButton(
                        text = if (isSubmitting) "提交中..." else "确认广播",
                        enabled = draft.canContinue && !isSubmitting,
                        onClick = {
                            isSubmitting = true
                            viewModel.send(symbol, draft.address, draft.amount) { txId ->
                                submittedTxId = txId
                                isSubmitting = false
                            }
                        },
                    )
                }
            }

            if (submittedTxId != null && draft != null) {
                GradientCard(title = "发送已提交", subtitle = "模拟广播已完成，等待链上确认") {
                    StatusChip(text = "广播成功", positive = true)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("交易 ID", submittedTxId.orEmpty())
                    InfoRow("目标地址", draft.address)
                    InfoRow("总扣除", formatWalletAmount(draft.totalDeduction, draft.asset.symbol))
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(text = "查看发送结果", onClick = { onSent(submittedTxId.orEmpty()) })
                }
            } else if (submittedTxId == null) {
                PrimaryButton(
                    text = "预览发送",
                    enabled = draft?.canContinue == true && !reviewMode,
                    onClick = { reviewMode = true },
                )
            }
        }
    }
}

private fun editableAmount(value: Double): String = BigDecimal.valueOf(value).stripTrailingZeros().toPlainString()
