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
import com.v2ray.ang.composeui.p1.model.P1DetailLine
import com.v2ray.ang.composeui.p1.model.P1ScreenState
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
    val stateInfo = uiState.stateInfo

    P01PhoneScaffold(
        statusTime = "18:42",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDER DETAIL",
            title = uiState.title,
            subtitle = uiState.summary,
            chips = listOf("• ${uiState.order?.statusText ?: stateInfo.title.ifBlank { "订单状态" }}"),
            backLabel = "<",
            onBack = onBack,
            trailing = { P1SecureHub(label = orderDetailHubLabel(highlightedRowIndex)) },
        )

        P1SelectableCard(
            selected = uiState.order != null && stateInfo.state == P1ScreenState.Content,
            accentColor = rows.getOrNull(highlightedRowIndex)?.accentColor ?: Color(0xFF49D89B),
        ) {
            P01CardHeader(title = "订单摘要")
            P01CardCopy(stateInfo.message.ifBlank { uiState.note.ifBlank { uiState.summary } })
            P01List {
                if (rows.isEmpty()) {
                    P1FeedbackRow(
                        title = stateInfo.title.ifBlank { "当前没有真实订单详情" },
                        copy = stateInfo.message.ifBlank { "未查询到可展示的真实订单对象。" },
                        accentColor = Color(0xFF7B8DB0),
                    )
                } else {
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
}

private data class OrderDetailRowUi(
    val title: String,
    val value: String,
    val accentColor: Color,
)

private fun detailRows(uiState: OrderDetailUiState): List<OrderDetailRowUi> {
    val order = uiState.order
    if (order == null && uiState.detailLines.isEmpty()) return emptyList()
    val detailLines = buildList {
        if (order != null) {
            add(P1DetailLine(order.planName, "${order.networkCode} / ${order.amountText}"))
            add(P1DetailLine(order.orderNo, order.statusText.ifBlank { order.status }))
        }
        addAll(uiState.detailLines)
    }
    return detailLines.map { line ->
        OrderDetailRowUi(
            title = line.label,
            value = line.value,
            accentColor = when {
                line.label.contains("状态") || line.label.contains("订阅") -> Color(0xFF49D89B)
                line.label.contains("交易") || line.label.contains("TX") -> Color(0xFF20C4F4)
                else -> Color(0xFF4276FF)
            },
        )
    }
}

private fun orderDetailHubLabel(index: Int): String = when (index) {
    0 -> "PLAN"
    1 -> "ORD"
    2 -> "TX"
    3 -> "HASH"
    4 -> "LIVE"
    5 -> "NODE"
    else -> "TRACE"
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
