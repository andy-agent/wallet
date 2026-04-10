package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01ListRow
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p1.model.OrderListUiState
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
    val orders = uiState.highlights.ifEmpty {
        listOf(
            FeatureListItem("年费 Pro", "TRON / 149 USDT", "已完成", "ORD-2025-0001"),
            FeatureListItem("月费 Pro", "SOL / 8.90", "待支付", "ORD-2025-0002"),
        )
    }

    P01PhoneScaffold(
        statusTime = "18:24",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDERS",
            title = "订单中心",
            backLabel = "<",
            onBack = { onBottomNav(CryptoVpnRouteSpec.vpnHome.pattern) },
        )

        P01Card {
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
                orders.forEach { order ->
                    P01ListRow(
                        title = order.title,
                        copy = order.subtitle,
                        value = order.trailing,
                        onClick = { onOpenOrder(order.badge.ifBlank { "ORD-2025-0001" }) },
                    )
                }
            }
        }
    }
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
