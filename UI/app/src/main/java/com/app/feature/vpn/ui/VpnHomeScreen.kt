package com.app.feature.vpn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.feature.vpn.components.*
import com.app.feature.vpn.viewmodel.VpnViewModel
import com.app.vpncore.model.VpnState

@Composable
fun VpnHomeScreen(
    viewModel: VpnViewModel = viewModel(),
    onOpenNodes: () -> Unit = {},
    onOpenPlans: () -> Unit = {},
    onOpenSubscription: () -> Unit = {},
    onOpenOrders: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "", showTopBar = false) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                StatusChip("P1 VPN 首页")
            }
            item {
                Text("安全连接", style = MaterialTheme.typography.headlineLarge)
            }
            item {
                GradientCard(title = "连接状态", subtitle = when (val s = state.vpnState) {
                    is VpnState.Connected -> "已连接 ${s.nodeName}"
                    is VpnState.Connecting -> "连接中 ${s.nodeName}"
                    is VpnState.Disconnecting -> "断开中"
                    is VpnState.Error -> s.message
                    else -> "未连接"
                }) {
                    Text("基于 v2rayNG 架构思路的 mock core 控制层", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Box(modifier = Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 18.dp), contentAlignment = Alignment.Center) {
                        VpnPowerButton(active = state.vpnState is VpnState.Connected, onClick = viewModel::connectOrDisconnect)
                    }
                    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        MetricPill("节点", state.selectedNode?.country ?: "--")
                        MetricPill("延迟", state.selectedNode?.latencyMs?.let { "$it ms" } ?: "--")
                    }
                }
            }
            item {
                GradientCard(title = "当前节点", subtitle = state.selectedNode?.name ?: "尚未选择") {
                    Text(state.selectedNode?.host ?: "请先刷新订阅并选择节点", style = MaterialTheme.typography.bodyMedium)
                    PrimaryButton(text = "节点列表", onClick = onOpenNodes, modifier = Modifier.padding(top = 12.dp))
                }
            }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(Modifier.weight(1f)) { SecondaryButton(text = "套餐", onClick = onOpenPlans) }
                    Box(Modifier.weight(1f)) { SecondaryButton(text = "订阅", onClick = onOpenSubscription) }
                }
            }
            item { SecondaryButton(text = "订单中心", onClick = onOpenOrders) }
        }
    }
}
