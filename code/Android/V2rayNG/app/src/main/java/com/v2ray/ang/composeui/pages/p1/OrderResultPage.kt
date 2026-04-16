package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p1.model.OrderResultEvent
import com.v2ray.ang.composeui.p1.model.OrderResultUiState
import com.v2ray.ang.composeui.p1.model.orderResultPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderResultViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderResultRoute(
    viewModel: OrderResultViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: ((String) -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderResultScreen(
        uiState = uiState,
        onRefresh = {
            viewModel.onEvent(OrderResultEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onSecondaryAction = {
            viewModel.onEvent(OrderResultEvent.SecondaryActionClicked)
            uiState.orderNo?.let { orderNo -> onSecondaryAction?.invoke(orderNo) }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderResultScreen(
    uiState: OrderResultUiState,
    onRefresh: () -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val rows = listOfNotNull(
        "订单号" to uiState.orderNo,
        "订单状态" to uiState.statusText.ifBlank { uiState.status.orEmpty() }.ifBlank { null },
        "链上交易" to uiState.txHash,
        "支付确认时间" to uiState.paymentMatchedAt,
        "完成时间" to uiState.completedAt,
        "到期时间" to uiState.expiresAt,
        "订阅链接" to uiState.subscriptionUrl,
        "失败原因" to uiState.failureReason,
    )
    val accent = resultAccent(uiState.status)

    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.orderResult.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDER RESULT",
            title = "订单状态",
            subtitle = "",
            chips = listOf(uiState.statusText.ifBlank { uiState.badge }),
            trailing = { P1SecureHub(label = if (uiState.statusText.isBlank()) "ORDER" else "LIVE") },
        )

        P1SelectableCard(
            selected = true,
            accentColor = accent,
        ) {
            P01CardHeader(
                title = uiState.statusText.ifBlank { "待刷新" },
            )
            P01CardCopy(
                uiState.screenState.unavailableMessage
                    ?: uiState.screenState.errorMessage
                    ?: uiState.screenState.emptyMessage
                    ?: uiState.note.ifBlank { "" },
            )
            P01List {
                rows.forEach { (title, value) ->
                    value?.takeIf { it.isNotBlank() }?.let {
                        P1FeedbackRow(
                            title = title,
                            value = it,
                            selected = title == "订单状态",
                            accentColor = accent,
                            valueColor = accent,
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            P01SecondaryButton(
                text = uiState.secondaryActionLabel ?: "查看订单详情",
                onClick = onSecondaryAction,
                modifier = Modifier.weight(1f),
            )
            P1PrimaryCta(
                text = uiState.primaryActionLabel,
                onClick = onRefresh,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

private fun resultAccent(status: String?): Color = when (status?.uppercase()) {
    "COMPLETED", "PAID", "PROVISIONING" -> Color(0xFF49D89B)
    "AWAITING_PAYMENT", "PAYMENT_DETECTED", "CONFIRMING" -> Color(0xFFF6B155)
    "FAILED", "EXPIRED", "CANCELED" -> Color(0xFFE55D67)
    else -> Color(0xFF4276FF)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderResultPreview() {
    CryptoVpnTheme {
        OrderResultScreen(
            uiState = orderResultPreviewState(),
            onRefresh = {},
            onSecondaryAction = {},
        )
    }
}
