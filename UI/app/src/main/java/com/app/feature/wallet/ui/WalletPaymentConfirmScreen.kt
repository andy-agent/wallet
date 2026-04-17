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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.PrimaryButton
import com.app.common.components.StatusChip
import com.app.common.widgets.MetricPill
import com.app.core.ui.AppScaffold
import com.app.core.utils.Formatters
import com.app.data.model.OrderStatus
import com.app.feature.vpn.viewmodel.VpnViewModel
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.feature.wallet.viewmodel.formatWalletAmount

@Composable
fun WalletPaymentConfirmScreen(
    orderId: String,
    viewModel: VpnViewModel = viewModel(),
    walletViewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val vpnState by viewModel.uiState.collectAsState()
    val walletState by walletViewModel.uiState.collectAsState()
    val order = vpnState.orders.firstOrNull { it.id == orderId }
    val paymentDraft = walletViewModel.evaluatePayment(order)
    var isProcessing by rememberSaveable(orderId) { mutableStateOf(false) }
    var paymentTxId by rememberSaveable(orderId) { mutableStateOf<String?>(null) }
    var paymentError by rememberSaveable(orderId) { mutableStateOf<String?>(null) }

    AppScaffold(title = "钱包支付确认", onBack = onBack) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            GradientCard(
                title = order?.planName ?: "订单确认",
                subtitle = "使用钱包资产支付订阅订单",
            ) {
                if (order == null) {
                    Text("未找到订单信息，无法发起钱包支付。", style = MaterialTheme.typography.bodyMedium)
                } else {
                    StatusChip(text = orderStatusLabel(order.status), positive = order.status != OrderStatus.Failed)
                    Spacer(Modifier.height(12.dp))
                    InfoRow("订单号", order.id)
                    InfoRow("应付金额", Formatters.money(order.amountUsd))
                    InfoRow("下单时间", Formatters.dateTime(order.createdAt))
                    InfoRow("当前币种", order.paySymbol)
                }
            }

            if (paymentDraft != null) {
                GradientCard(title = "支付资产", subtitle = "确认支付网络、可用余额与商户收款地址") {
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricPill("付款币种", paymentDraft.asset.symbol)
                        MetricPill("支付网络", paymentDraft.network.chainName)
                        MetricPill("钱包资产", walletState.assets.size.toString())
                    }
                    Spacer(Modifier.height(12.dp))
                    InfoRow("可用余额", formatWalletAmount(paymentDraft.asset.balance, paymentDraft.asset.symbol))
                    InfoRow("商户地址", paymentDraft.merchantAddress)
                    InfoRow("确认时效", "${paymentDraft.network.settlementMinutes} 分钟")
                }

                GradientCard(title = "扣款明细", subtitle = "模拟确认前先核对资产与手续费") {
                    InfoRow("订单折算", formatWalletAmount(paymentDraft.tokenAmount, paymentDraft.asset.symbol))
                    InfoRow("预估网络费", formatWalletAmount(paymentDraft.feeAmount, paymentDraft.asset.symbol))
                    InfoRow("总扣除", formatWalletAmount(paymentDraft.totalDeduction, paymentDraft.asset.symbol))
                    InfoRow("支付后余额", formatWalletAmount(paymentDraft.remainingBalance, paymentDraft.asset.symbol))
                    when {
                        paymentDraft.alreadySettled -> {
                            Spacer(Modifier.height(12.dp))
                            StatusChip(text = "订单已完成扣款或已生效", positive = true)
                        }
                        paymentDraft.balanceError != null -> {
                            Spacer(Modifier.height(12.dp))
                            StatusChip(text = "余额不足", positive = false)
                            Spacer(Modifier.height(8.dp))
                            Text(paymentDraft.balanceError, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                    paymentError?.let { error ->
                        Spacer(Modifier.height(12.dp))
                        Text(error, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }

                if (paymentTxId != null) {
                    GradientCard(title = "支付已确认", subtitle = "模拟订单支付已提交并写入钱包流水") {
                        StatusChip(text = "支付成功", positive = true)
                        Spacer(Modifier.height(12.dp))
                        InfoRow("支付流水", paymentTxId.orEmpty())
                        InfoRow("订单状态", "已完成钱包确认")
                        Spacer(Modifier.height(12.dp))
                        PrimaryButton(text = "查看订单结果", onClick = onConfirm)
                    }
                } else {
                    PrimaryButton(
                        text = if (paymentDraft.alreadySettled) "查看订单结果" else if (isProcessing) "确认中..." else "确认支付",
                        enabled = paymentDraft.alreadySettled || (paymentDraft.canConfirm && !isProcessing),
                        onClick = {
                            if (paymentDraft.alreadySettled) {
                                onConfirm()
                            } else if (!isProcessing && order != null) {
                                paymentError = null
                                isProcessing = true
                                walletViewModel.payOrder(
                                    orderId = order.id,
                                    paySymbol = paymentDraft.asset.symbol,
                                    amountUsd = order.amountUsd,
                                    merchantAddress = paymentDraft.merchantAddress,
                                ) { txId, error ->
                                    if (error != null) {
                                        paymentError = error
                                        isProcessing = false
                                    } else {
                                        viewModel.confirmPayment(order.id, paymentDraft.asset.symbol) {
                                            paymentTxId = txId
                                            isProcessing = false
                                        }
                                    }
                                }
                            }
                        },
                    )
                }
            }
        }
    }
}

private fun orderStatusLabel(status: OrderStatus): String = when (status) {
    OrderStatus.Pending -> "待支付"
    OrderStatus.Paid -> "已支付"
    OrderStatus.Active -> "已生效"
    OrderStatus.Expired -> "已过期"
    OrderStatus.Failed -> "支付失败"
}

