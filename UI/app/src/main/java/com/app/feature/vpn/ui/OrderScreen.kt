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
fun OrderScreen(
    planId: String,
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
    onNext: (String) -> Unit = {},
) {
    val plan = viewModel.plan(planId) ?: return
    AppScaffold(title = "下单确认", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = plan.title, subtitle = plan.durationLabel) {
                InfoRow("金额", Formatters.money(plan.priceUsd))
                InfoRow("流量", "$${plan.trafficGb} GB")
                InfoRow("设备", plan.deviceLimit.toString())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "创建订单", onClick = { viewModel.createOrder(planId) { onNext(it.id) } })
            }
        }
    }
}
