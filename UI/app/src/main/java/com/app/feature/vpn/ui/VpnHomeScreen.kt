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
fun VpnHomeScreen(
    viewModel: VpnViewModel = viewModel(),
    onOpenNodes: () -> Unit = {},
    onOpenPlans: () -> Unit = {},
    onOpenSubscription: () -> Unit = {},
    onOpenOrders: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "VPN 首页") { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                GradientCard(title = "连接状态", subtitle = when (val s = state.vpnState) {
                    is VpnState.Connected -> "已连接 $${s.nodeName}"
                    is VpnState.Connecting -> "连接中 $${s.nodeName}"
                    is VpnState.Disconnecting -> "断开中"
                    is VpnState.Error -> s.message
                    else -> "未连接"
                }) {
                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) { VpnPowerButton(active = state.vpnState is VpnState.Connected, onClick = viewModel::connectOrDisconnect) }
                }
            }
            item { GradientCard(title = "当前节点", subtitle = state.selectedNode?.name ?: "尚未选择") { PrimaryButton(text = "节点列表", onClick = onOpenNodes) } }
            item { Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { Box(Modifier.weight(1f)) { SecondaryButton(text = "套餐", onClick = onOpenPlans) }; Box(Modifier.weight(1f)) { SecondaryButton(text = "订阅", onClick = onOpenSubscription) } } }
            item { SecondaryButton(text = "订单中心", onClick = onOpenOrders) }
        }
    }
}
