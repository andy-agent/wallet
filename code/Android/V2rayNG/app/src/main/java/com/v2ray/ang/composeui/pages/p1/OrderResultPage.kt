package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p0.ui.P01SuccessBadge
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p1.model.OrderResultEvent
import com.v2ray.ang.composeui.p1.model.OrderResultUiState
import com.v2ray.ang.composeui.p1.model.orderResultPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderResultViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderResultRoute(
    viewModel: OrderResultViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderResultScreen(
        uiState = uiState,
        onOpenDetail = {
            onBottomNav(CryptoVpnRouteSpec.orderDetailRoute(extractOrderId(uiState)))
        },
        onGoHome = { onBottomNav(CryptoVpnRouteSpec.vpnHome.pattern) },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderResultScreen(
    uiState: OrderResultUiState,
    onOpenDetail: () -> Unit,
    onGoHome: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P01PhoneScaffold(
        statusTime = "18:23",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P1SelectableCard(
            selected = true,
            centered = true,
            accentColor = Color(0xFF49D89B),
        ) {
            P01SuccessBadge(symbol = "", tint = Color(0xFF49D89B))
            P01CardHeader(title = "订单已生效")
            P01CardCopy("套餐与节点权限已经下发，你可以立即开始使用加密网络。")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                P01SecondaryButton(
                    text = "查看订单详情",
                    onClick = onOpenDetail,
                    modifier = Modifier.weight(1f),
                )
                P1PrimaryCta(
                    text = "开始连接并进入首页",
                    onClick = onGoHome,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun extractOrderId(uiState: OrderResultUiState): String =
    uiState.highlights.firstOrNull { it.title.contains("订单号") }?.subtitle
        ?.takeIf { it.isNotBlank() } ?: uiState.metrics.firstOrNull()?.value ?: "ORD-2025-0001"

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderResultPreview() {
    CryptoVpnTheme {
        OrderResultScreen(
            uiState = orderResultPreviewState(),
            onOpenDetail = {},
            onGoHome = {},
        )
    }
}
