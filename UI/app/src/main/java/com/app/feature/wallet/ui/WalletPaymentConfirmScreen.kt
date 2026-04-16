package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.AppDimens
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.components.*
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.core.utils.Formatters

import com.app.feature.vpn.viewmodel.VpnViewModel

@Composable
fun WalletPaymentConfirmScreen(
    orderId: String,
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
    onConfirm: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val order = state.orders.firstOrNull { it.id == orderId }
    AppScaffold(title = "钱包支付确认", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = order?.planName ?: "订单确认", subtitle = "使用钱包资产支付订阅订单") {
                InfoRow("订单号", orderId)
                InfoRow("应付", order?.let { Formatters.money(it.amountUsd) } ?: "$24.90")
                InfoRow("付款币种", order?.paySymbol ?: "USDT")
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "确认支付", onClick = { onConfirm() })
            }
        }
    }
}
