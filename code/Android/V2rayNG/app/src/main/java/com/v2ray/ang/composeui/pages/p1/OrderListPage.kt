package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.v2ray.ang.composeui.p1.model.OrderListEvent
import com.v2ray.ang.composeui.p1.model.OrderListUiState
import com.v2ray.ang.composeui.p1.model.orderListPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderListViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderListRoute(
    viewModel: OrderListViewModel,
    onPrimaryAction: ((String) -> Unit)? = null,
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderListScreen(
        uiState = uiState,
        onRefresh = { viewModel.onEvent(OrderListEvent.Refresh) },
        onOpenOrder = { orderNo ->
            viewModel.onEvent(OrderListEvent.PrimaryActionClicked)
            onPrimaryAction?.invoke(orderNo)
        },
        onBack = {
            viewModel.onEvent(OrderListEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderListScreen(
    uiState: OrderListUiState,
    onRefresh: () -> Unit,
    onOpenOrder: (String) -> Unit,
    onBack: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(0) }
    var selectedOrderNo by rememberSaveable { mutableStateOf(uiState.orders.firstOrNull()?.orderNo.orEmpty()) }
    val filteredOrders = uiState.orders.filter { order ->
        when (selectedTab) {
            1 -> order.statusText == "已完成"
            2 -> order.statusText.contains("退款")
            else -> true
        }
    }

    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.orderList.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDERS",
            title = "订单中心",
            backLabel = "<",
            onBack = onBack,
            trailing = { P1SecureHub(label = orderListHubLabel(selectedTab)) },
        )

        P1SelectableCard(
            selected = filteredOrders.isNotEmpty(),
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
            P01List {
                if (uiState.screenState.hasError || uiState.screenState.isUnavailable || uiState.screenState.isEmpty) {
                    P01CardCopy(
                        uiState.screenState.unavailableMessage
                            ?: uiState.screenState.errorMessage
                            ?: uiState.screenState.emptyMessage
                            ?: uiState.note,
                    )
                }
                filteredOrders.forEach { order ->
                    P1FeedbackRow(
                        title = order.planTitle,
                        copy = "${order.amountText} · ${order.createdAt}",
                        value = order.statusText,
                        selected = order.orderNo == selectedOrderNo,
                        accentColor = orderStatusColor(order.status),
                        valueColor = orderStatusColor(order.status),
                        onClick = {
                            selectedOrderNo = order.orderNo
                            onOpenOrder(order.orderNo)
                        },
                    )
                }
            }
        }
    }
}

private fun orderStatusColor(status: String): Color = when (status.uppercase()) {
    "COMPLETED", "PAID", "PROVISIONING" -> Color(0xFF49D89B)
    "AWAITING_PAYMENT", "PAYMENT_DETECTED", "CONFIRMING" -> Color(0xFFF6B155)
    "FAILED", "EXPIRED", "CANCELED" -> Color(0xFFE55D67)
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
            onRefresh = {},
            onOpenOrder = {},
            onBack = {},
        )
    }
}
