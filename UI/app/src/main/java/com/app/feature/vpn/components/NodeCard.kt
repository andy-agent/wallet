package com.app.feature.vpn.components

import androidx.compose.runtime.Composable
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.widgets.NodeSignalBadge
import com.app.vpncore.model.VpnNode

@Composable
fun NodeCard(node: VpnNode, selected: Boolean, onSelect: () -> Unit) {
    GradientCard(title = node.name, subtitle = "${node.country} · ${node.protocol.name}") {
        NodeSignalBadge(node.latencyMs)
        InfoRow("线路", node.host)
        if (selected) androidx.compose.material3.Text("当前已选")
        androidx.compose.material3.TextButton(onClick = onSelect) { androidx.compose.material3.Text(if (selected) "已选择" else "选择节点") }
    }
}
