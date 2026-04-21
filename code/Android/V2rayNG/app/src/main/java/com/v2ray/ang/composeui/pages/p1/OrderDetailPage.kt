package com.v2ray.ang.composeui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p1.model.OrderDetailUiState
import com.v2ray.ang.composeui.p1.model.orderDetailPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderDetailViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderDetailRoute(
    viewModel: OrderDetailViewModel,
    onPrimaryAction: (() -> Unit)? = null,
    onSecondaryAction: () -> Unit = {},
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderDetailScreen(
        uiState = uiState,
        onBack = onSecondaryAction,
        onOpenPayment = { onPrimaryAction?.invoke() },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderDetailScreen(
    uiState: OrderDetailUiState,
    onBack: () -> Unit,
    onOpenPayment: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val highlightValue = uiState.rows.firstOrNull { it.label == "金额" }?.value
        ?: uiState.rows.firstOrNull()?.value
        ?: "--"
    val detailRows = uiState.rows.filterNot { it.label == "金额" }
    val canPay = uiState.status?.uppercase() in setOf("AWAITING_PAYMENT", "PAYMENT_DETECTED", "CONFIRMING")

    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.orderDetail.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "ORDER DETAIL",
            title = "订单详情",
            subtitle = uiState.summary,
            chips = listOf("• ${uiState.statusText.ifBlank { "--" }}"),
            backLabel = "<",
            onBack = onBack,
            trailing = { P1SecureHub(label = "PLAN") },
        )

        P1SelectableCard(
            selected = true,
            accentColor = detailAccent(uiState.status),
        ) {
            P01CardHeader(title = "订单摘要")
            P01CardCopy(
                uiState.screenState.unavailableMessage
                    ?: uiState.screenState.errorMessage
                    ?: uiState.screenState.emptyMessage
                    ?: ""
            )
            P1FeedbackRow(
                title = uiState.planTitle.ifBlank { uiState.planCode.orEmpty().ifBlank { "订单" } },
                value = highlightValue,
                selected = true,
                accentColor = Color(0xFF4276FF),
                valueColor = Color(0xFF4276FF),
            )
            P01List {
                detailRows.forEach { row ->
                    P1FeedbackRow(
                        title = row.label,
                        value = row.value,
                        selected = row.label == "订单号",
                    )
                }
            }
            if (canPay && onOpenPayment != null) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    P01SecondaryButton(
                        text = "返回订单中心",
                        onClick = onBack,
                        modifier = Modifier.weight(1f),
                    )
                    P01PrimaryButton(
                        text = "去支付",
                        onClick = onOpenPayment,
                        modifier = Modifier.weight(1f),
                    )
                }
            }
        }
    }
}

private fun detailAccent(status: String?): Color = when (status?.uppercase()) {
    "COMPLETED", "PAID", "PROVISIONING" -> Color(0xFF49D89B)
    "AWAITING_PAYMENT", "PAYMENT_DETECTED", "CONFIRMING" -> Color(0xFFF6B155)
    "FAILED", "EXPIRED", "CANCELED" -> Color(0xFFE55D67)
    else -> Color(0xFF4276FF)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderDetailPreview() {
    CryptoVpnTheme {
        OrderDetailScreen(
            uiState = orderDetailPreviewState(),
            onBack = {},
            onOpenPayment = {},
        )
    }
}
