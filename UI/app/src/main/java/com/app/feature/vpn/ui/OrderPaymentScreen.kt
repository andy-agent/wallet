package com.app.feature.vpn.ui

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
import com.app.feature.vpn.components.*
import com.app.feature.vpn.viewmodel.VpnViewModel
import com.app.vpncore.model.VpnState
import com.app.core.utils.Formatters


@Composable
fun OrderPaymentScreen(
    orderId: String,
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
    onConfirmWallet: () -> Unit = {},
) {
    val order = viewModel.order(orderId)
    AppScaffold(title = "订单收银台", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = order?.planName ?: "订单支付", subtitle = orderId) {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) { Text("付款二维码") }
                Spacer(Modifier.height(12.dp))
                InfoRow("订单金额", order?.let { Formatters.money(it.amountUsd) } ?: "$0")
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "使用钱包支付", onClick = onConfirmWallet)
            }
        }
    }
}
