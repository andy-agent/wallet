package com.v2ray.ang.composeui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
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
    val rows = detailRows(uiState)
    var highlightedRowIndex by rememberSaveable { mutableIntStateOf(rows.lastIndex.coerceAtLeast(0)) }

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
            trailing = { P1SecureHub(label = "TRACE") },
        )

        P1SelectableCard(
            selected = true,
            accentColor = rows.getOrNull(highlightedRowIndex)?.accentColor ?: Color(0xFF49D89B),
        ) {
            P01CardHeader(title = "订单摘要")
            P01CardCopy("订单已完成，全部4台设备权限同步正常。")
            P01List {
                rows.forEachIndexed { index, row ->
                    P1FeedbackRow(
                        title = row.title,
                        value = row.value,
                        selected = index == highlightedRowIndex,
                        accentColor = row.accentColor,
                        valueColor = row.accentColor,
                        onClick = { highlightedRowIndex = index },
                    )
                }
            }
        }
    }
}

private data class OrderDetailRowUi(
    val title: String,
    val value: String,
    val accentColor: Color,
)

private fun detailRows(uiState: OrderDetailUiState): List<OrderDetailRowUi> {
    val highlightList = uiState.highlights.p1ContentItems()
    val metricMap = uiState.metrics.associate { it.label to it.value }
    return listOf(
        OrderDetailRowUi(
            title = highlightList.getOrNull(0)?.title ?: "年费 Pro",
            value = highlightList.getOrNull(0)?.trailing ?: "TRON / 149 USDT",
            accentColor = Color(0xFF4276FF),
        ),
        OrderDetailRowUi(
            title = highlightList.getOrNull(0)?.subtitle ?: "ORD-2025-08-0224",
            value = "• 创建订单",
            accentColor = Color(0xFF7B8DB0),
        ),
        OrderDetailRowUi(
            title = highlightList.getOrNull(1)?.trailing ?: "2025-04-09 18:21",
            value = "• 链上转账已广播",
            accentColor = Color(0xFF20C4F4),
        ),
        OrderDetailRowUi(
            title = "TXid: ${highlightList.getOrNull(1)?.subtitle ?: "7F3A...901"}",
            value = "• 区块确认完成",
            accentColor = Color(0xFF4276FF),
        ),
        OrderDetailRowUi(
            title = metricMap["状态"] ?: "TRON． 1/1 block",
            value = "• 套餐已激活",
            accentColor = Color(0xFF49D89B),
        ),
        OrderDetailRowUi(
            title = "东京/新加坡节点可用",
            value = "• 可用设备 4台",
            accentColor = Color(0xFF49D89B),
        ),
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
