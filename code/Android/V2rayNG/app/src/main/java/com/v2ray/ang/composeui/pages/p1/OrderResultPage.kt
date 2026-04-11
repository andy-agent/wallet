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
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p0.ui.P01SuccessBadge
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p1.model.OrderResultEvent
import com.v2ray.ang.composeui.p1.model.P1ScreenState
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
    val stateInfo = uiState.stateInfo
    val order = uiState.order
    val statusLabel = order?.statusText ?: stateInfo.title.ifBlank { "订单状态" }
    val successLike = order?.status in listOf("FULFILLED", "COMPLETED") || uiState.canEnterHome
    P01PhoneScaffold(
        statusTime = "18:23",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDER RESULT",
            title = uiState.title,
            subtitle = uiState.summary,
            trailing = { P1SecureHub(label = resultHubLabel(order?.status ?: stateInfo.state.name)) },
        )

        P1SelectableCard(
            selected = true,
            centered = true,
            accentColor = if (successLike) Color(0xFF49D89B) else Color(0xFF4276FF),
        ) {
            P01SuccessBadge(symbol = "", tint = if (successLike) Color(0xFF49D89B) else Color(0xFF4276FF))
            P01CardHeader(title = statusLabel)
            P01CardCopy(stateInfo.message.ifBlank { uiState.note.ifBlank { uiState.summary } })
            if (order != null || uiState.detailLines.isNotEmpty()) {
                com.v2ray.ang.composeui.p0.ui.P01List {
                    order?.let {
                        P1FeedbackRow(
                            title = "订单号",
                            value = it.orderNo,
                            accentColor = Color(0xFF4276FF),
                            valueColor = Color(0xFF4276FF),
                        )
                        P1FeedbackRow(
                            title = "订单状态",
                            value = it.statusText.ifBlank { it.status },
                            accentColor = if (successLike) Color(0xFF49D89B) else Color(0xFFF6B155),
                            valueColor = if (successLike) Color(0xFF49D89B) else Color(0xFFF6B155),
                        )
                    }
                    uiState.detailLines.forEach { line ->
                        P1FeedbackRow(
                            title = line.label,
                            value = line.value,
                            accentColor = Color(0xFF4276FF),
                            valueColor = Color(0xFF4276FF),
                        )
                    }
                }
            }
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
                    text = if (uiState.canEnterHome) "开始连接并进入首页" else "返回首页查看状态",
                    onClick = onGoHome,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private fun extractOrderId(uiState: OrderResultUiState): String =
    uiState.order?.orderNo
        ?: uiState.detailLines.firstOrNull { it.label.contains("订单号") }?.value
        ?.takeIf { it.isNotBlank() }
        ?: uiState.metrics.firstOrNull()?.value
        ?: ""

private fun resultHubLabel(status: String): String = when {
    status.contains("FULFILLED", ignoreCase = true) || status.contains("完成") -> "DONE"
    status.contains("PENDING", ignoreCase = true) || status.contains("待") -> "WAIT"
    status.contains("FAIL", ignoreCase = true) || status.contains("ERR", ignoreCase = true) -> "ERR"
    else -> "LIVE"
}

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
