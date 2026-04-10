package com.v2ray.ang.composeui.pages.p0

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.model.RegionSpeed
import com.v2ray.ang.composeui.p0.model.VpnConnectionStatus
import com.v2ray.ang.composeui.p0.model.VpnHomeEvent
import com.v2ray.ang.composeui.p0.model.VpnHomeUiState
import com.v2ray.ang.composeui.p0.repository.MockP0Repository
import com.v2ray.ang.composeui.p0.ui.P01ButtonRow
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01ListRow
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.viewmodel.VpnHomeViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun VpnHomeRoute(
    currentRoute: String,
    viewModel: VpnHomeViewModel,
    onBottomNav: (String) -> Unit,
    onWalletHome: () -> Unit,
    onPlans: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    VpnHomeScreen(
        currentRoute = currentRoute,
        uiState = uiState,
        onToggleConnection = { viewModel.onEvent(VpnHomeEvent.ToggleConnection) },
        onSelectRegion = { viewModel.onEvent(VpnHomeEvent.RegionSelected(it)) },
        onBottomNav = onBottomNav,
        onWalletHome = onWalletHome,
        onPlans = onPlans,
    )
}

@Composable
fun VpnHomeScreen(
    currentRoute: String,
    uiState: VpnHomeUiState,
    onToggleConnection: () -> Unit,
    onSelectRegion: (RegionSpeed) -> Unit,
    onBottomNav: (String) -> Unit,
    onWalletHome: () -> Unit,
    onPlans: () -> Unit,
) {
    P01PhoneScaffold(
        statusTime = "18:30",
        currentRoute = currentRoute,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "SECURE TUNNEL",
            title = "VPN核心",
            subtitle = "将节点健康、实时延迟、套餐状态与支付续费融合到一屏。",
            chips = listOf("• ${connectionChipLabel(uiState.connectionStatus)}"),
        )

        P01Card {
            P01CardHeader(
                title = "当前连接",
                trailing = {
                    P01Chip(text = "${uiState.subscription.planName}• 剩余${uiState.subscription.expiresInDays}天")
                },
                subtitle = "${uiState.selectedRegion.regionName} • ${uiState.selectedRegion.protocol}",
            )
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("CONNECTED", connectionPrimaryValue(uiState)),
                    P01MetricCell("延迟", "${uiState.selectedRegion.latencyMs}ms"),
                    P01MetricCell("在线时长", if (uiState.connectionStatus == VpnConnectionStatus.CONNECTED) "08h 42m" else "--"),
                    P01MetricCell("节点评分", "97/100"),
                ),
            )
            P01ButtonRow(
                primaryLabel = "断开/重新连接",
                onPrimaryClick = onToggleConnection,
                secondaryLabel = "切换节点",
                onSecondaryClick = {
                    onSelectRegion(uiState.selectedRegion)
                    onBottomNav(CryptoVpnRouteSpec.regionSelection.pattern)
                },
            )
        }

        P01Card {
            P01CardHeader(title = "流量与健康")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                VpnQuickCell("12.4 GB", onClick = { onBottomNav(CryptoVpnRouteSpec.receiveRoute("USDT", "tron")) }, modifier = Modifier.weight(1f))
                VpnQuickCell("82.1 GB", onClick = { onBottomNav(CryptoVpnRouteSpec.sendRoute("USDT", "tron")) }, modifier = Modifier.weight(1f))
                VpnQuickCell("0.2%", onClick = { onBottomNav(CryptoVpnRouteSpec.regionSelection.pattern) }, modifier = Modifier.weight(1f))
                VpnQuickCell("查看订单", onClick = { onBottomNav(CryptoVpnRouteSpec.orderList.pattern) }, modifier = Modifier.weight(1f))
            }
            P01CardCopy("今日保护设备4台，智能路由命中率持续提升。")
        }

        P01Card {
            P01CardHeader(title = "最近提醒")
            P01List {
                P01ListRow(
                    title = "节点抖动已恢复•套餐将在3天后自动续费",
                    value = "查看订单",
                    onClick = { onBottomNav(CryptoVpnRouteSpec.orderList.pattern) },
                )
            }
        }

        P01Card {
            P01CardHeader(title = "钱包快照")
            P01CardCopy("进入多链钱包总览，查看可用于套餐续费的资产与支付网络。")
            P01ButtonRow(
                primaryLabel = "打开钱包",
                onPrimaryClick = onWalletHome,
                secondaryLabel = "购买套餐",
                onSecondaryClick = onPlans,
            )
        }
    }
}

@Composable
private fun VpnQuickCell(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .background(Color.White.copy(alpha = 0.82f), RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp, horizontal = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        androidx.compose.foundation.Canvas(modifier = Modifier.size(36.dp)) {
            drawRoundRect(
                color = Color(0x1A4276FF),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(14.dp.toPx(), 14.dp.toPx()),
            )
            drawCircle(
                color = Color(0xFF4276FF),
                radius = size.minDimension * 0.12f,
                center = center,
            )
        }
        androidx.compose.material3.Text(text = label, color = Color(0xFF4D6287))
    }
}

private fun connectionChipLabel(status: VpnConnectionStatus): String = when (status) {
    VpnConnectionStatus.CONNECTED -> "已连接"
    VpnConnectionStatus.CONNECTING -> "连接中"
    VpnConnectionStatus.DISCONNECTED -> "未连接"
}

private fun connectionPrimaryValue(status: VpnHomeUiState): String = when (status.connectionStatus) {
    VpnConnectionStatus.CONNECTED -> "89Mbps"
    VpnConnectionStatus.CONNECTING -> "连接中"
    VpnConnectionStatus.DISCONNECTED -> "0Mbps"
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun VpnHomePreview() {
    CryptoVpnTheme {
        VpnHomeScreen(
            currentRoute = CryptoVpnRouteSpec.vpnHome.name,
            uiState = VpnHomeViewModel(MockP0Repository()).uiState.value,
            onToggleConnection = {},
            onSelectRegion = {},
            onBottomNav = {},
            onWalletHome = {},
            onPlans = {},
        )
    }
}
