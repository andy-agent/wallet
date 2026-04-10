package com.v2ray.ang.composeui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01ListRow
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p1.model.OrderDetailUiState
import com.v2ray.ang.composeui.p1.model.orderDetailPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderDetailViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderDetailRoute(
    viewModel: OrderDetailViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderDetailScreen(
        uiState = uiState,
        onBack = { onBottomNav(CryptoVpnRouteSpec.orderList.pattern) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderDetailScreen(
    uiState: OrderDetailUiState,
    onBack: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:42",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDER DETAIL",
            title = "订单详情",
            subtitle = "追踪支付状态、激活进度与设备生效情况。",
            chips = listOf("• 已完成"),
            backLabel = "<",
            onBack = onBack,
        )

        P01Card {
            P01CardHeader(title = "订单摘要")
            P01CardCopy("订单已完成，全部4台设备权限同步正常。")
            P01List {
                detailRows(uiState).forEach { (title, value) ->
                    P01ListRow(title = title, value = value)
                }
            }
        }
    }
}

private fun detailRows(uiState: OrderDetailUiState): List<Pair<String, String>> {
    val highlightList = uiState.highlights
    val metricMap = uiState.metrics.associate { it.label to it.value }
    return listOf(
        (highlightList.getOrNull(0)?.title ?: "年费 Pro") to (highlightList.getOrNull(0)?.trailing ?: "TRON / 149 USDT"),
        (highlightList.getOrNull(0)?.subtitle ?: "ORD-2025-08-0224") to "• 创建订单",
        (highlightList.getOrNull(1)?.trailing ?: "2025-04-09 18:21") to "• 链上转账已广播",
        ("TXid: ${highlightList.getOrNull(1)?.subtitle ?: "7F3A...901"}") to "• 区块确认完成",
        ((metricMap["状态"] ?: "TRON． 1/1 block")) to "• 套餐已激活",
        ("东京/新加坡节点可用") to "• 可用设备 4台",
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderDetailPreview() {
    CryptoVpnTheme {
        OrderDetailScreen(
            uiState = orderDetailPreviewState(),
            onBack = {},
        )
    }
}
