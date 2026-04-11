package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.OrderListUiState
import com.v2ray.ang.composeui.p1.model.P1OrderSummary
import com.v2ray.ang.composeui.p1.model.orderListPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderListViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderListRoute(
    viewModel: OrderListViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderListScreen(
        uiState = uiState,
        onOpenOrder = { orderId -> onBottomNav(CryptoVpnRouteSpec.orderDetailRoute(orderId)) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderListScreen(
    uiState: OrderListUiState,
    onOpenOrder: (String) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    val orders = orderListItems(uiState)
    var selectedOrderId by rememberSaveable { mutableStateOf(orders.firstOrNull()?.orderId.orEmpty()) }
    val stateInfo = uiState.stateInfo
    val filteredOrders = orders.filter { order ->
        when (selectedTab) {
            1 -> order.status == "已完成" || order.status == "COMPLETED"
            2 -> order.status == "已退款" || order.status == "REFUNDED"
            else -> true
        }
    }

    P01PhoneScaffold(
        statusTime = "18:24",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDERS",
            title = uiState.title,
            backLabel = "<",
            onBack = { onBottomNav(CryptoVpnRouteSpec.vpnHome.pattern) },
            trailing = { P1SecureHub(label = orderListHubLabel(selectedTab)) },
        )

        P1SelectableCard(
            selected = filteredOrders.isNotEmpty() && stateInfo.state == P1ScreenState.Content,
            accentColor = Color(0xFF4276FF),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                listOf("全部", "已完成", "已退款").forEachIndexed { index, label ->
                    P01Tab(
                        text = label,
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                    )
                }
            }
            if (stateInfo.message.isNotBlank()) {
                P01CardCopy(stateInfo.message)
            }
            P01List {
                filteredOrders.forEach { order ->
                    P1FeedbackRow(
                        title = order.title,
                        copy = order.copy,
                        value = order.status,
                        selected = order.orderId == selectedOrderId,
                        accentColor = order.accentColor,
                        valueColor = order.accentColor,
                        onClick = {
                            selectedOrderId = order.orderId
                            onOpenOrder(order.orderId)
                        },
                    )
                }
                if (filteredOrders.isEmpty()) {
                    P1FeedbackRow(
                        title = stateInfo.title.ifBlank { "当前筛选下暂无订单" },
                        copy = stateInfo.message.ifBlank { "当前账号暂无符合条件的真实订单。" },
                        accentColor = Color(0xFF7B8DB0),
                    )
                }
            }
        }
    }
}

private data class OrderListItemUi(
    val title: String,
    val copy: String,
    val status: String,
    val orderId: String,
    val accentColor: Color,
)

private fun orderListItems(uiState: OrderListUiState): List<OrderListItemUi> =
    uiState.orders.map(::toOrderListItem)

private fun toOrderListItem(order: P1OrderSummary): OrderListItemUi =
    OrderListItemUi(
        title = order.planName,
        copy = "${order.networkCode} / ${order.amountText}",
        status = order.statusText.ifBlank { order.status },
        orderId = order.orderNo,
        accentColor = orderStatusColor(order.statusText.ifBlank { order.status }),
    )

private fun orderStatusColor(status: String): Color =
    when (status) {
        "已完成" -> Color(0xFF49D89B)
        "待支付" -> Color(0xFFF6B155)
        "已退款" -> Color(0xFF7B8DB0)
        else -> Color(0xFF4276FF)
    }

private fun orderListHubLabel(selectedTab: Int): String = when (selectedTab) {
    1 -> "DONE"
    2 -> "REFUND"
    else -> "ALL"
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderListPreview() {
    CryptoVpnTheme {
        OrderListScreen(
            uiState = orderListPreviewState(),
            onOpenOrder = {},
        )
    }
}
