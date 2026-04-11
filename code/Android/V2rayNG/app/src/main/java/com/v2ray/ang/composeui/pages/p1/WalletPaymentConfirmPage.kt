package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p1.model.P1ScreenState
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmEvent
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmUiState
import com.v2ray.ang.composeui.p1.model.walletPaymentConfirmPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.WalletPaymentConfirmViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun WalletPaymentConfirmRoute(
    viewModel: WalletPaymentConfirmViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletPaymentConfirmScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(WalletPaymentConfirmEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletPaymentConfirmScreen(
    uiState: WalletPaymentConfirmUiState,
    onPrimaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val rows = paymentConfirmRows(uiState)
    var focusedSummaryIndex by rememberSaveable {
        mutableIntStateOf(rows.summaryRows.indexOfFirst { it.first == "支付金额" }.coerceAtLeast(0))
    }
    var selectedRiskIndex by rememberSaveable { mutableIntStateOf(-1) }
    val stateInfo = uiState.stateInfo
    val summaryAccent = if (focusedSummaryIndex == rows.summaryRows.indexOfFirst { it.first == "支付金额" }) {
        Color(0xFFF6B155)
    } else {
        Color(0xFF4276FF)
    }

    P01PhoneScaffold(
        statusTime = "18:14",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "WALLET PAYMENT",
            title = uiState.title,
            subtitle = uiState.summary,
            chips = listOf("链路安全"),
            trailing = { P1SecureHub(label = paymentConfirmHubLabel(selectedRiskIndex, focusedSummaryIndex)) },
        )

        P01Card {
            P01CardHeader(
                title = "订单摘要",
                trailing = { P01Chip(text = rows.orderNoLabel) },
            )
            if (rows.summaryRows.isEmpty()) {
                P01CardCopy(stateInfo.message.ifBlank { "当前未查询到真实支付确认单。" })
            } else {
                P01List {
                    rows.summaryRows.forEachIndexed { index, (title, value) ->
                        P1FeedbackRow(
                            title = title,
                            value = value,
                            selected = index == focusedSummaryIndex,
                            accentColor = if (title == "支付金额") Color(0xFFF6B155) else summaryAccent,
                            valueColor = if (title == "支付金额") Color(0xFFF6B155) else summaryAccent,
                            onClick = { focusedSummaryIndex = index },
                        )
                    }
                }
            }
        }

        P1SelectableCard(
            selected = uiState.order != null && stateInfo.state == P1ScreenState.Content,
            accentColor = Color(0xFF49D89B),
        ) {
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("订单状态", uiState.order?.statusText ?: "--"),
                    P01MetricCell("支付网络", uiState.order?.networkCode ?: "--"),
                ),
            )
            P01CardCopy(
                stateInfo.message.ifBlank { uiState.note.ifBlank { uiState.summary } },
            )
        }

        P01Card {
            P01CardHeader(
                title = "风控提示",
                trailing = {
                    P01Chip(
                        text = if (selectedRiskIndex >= 0) "已核对" else "建议查看",
                        highlighted = selectedRiskIndex >= 0,
                    )
                },
            )
            P01List {
                val risks = if (uiState.riskLines.isEmpty()) {
                    listOf("当前无额外风控提示" to "页面仅展示真实订单状态，不伪装自动开通成功。")
                } else {
                    uiState.riskLines.map { it.label to it.value }
                }
                risks.forEachIndexed { index, (title, copy) ->
                    P1FeedbackRow(
                        title = title,
                        copy = copy,
                        selected = index == selectedRiskIndex,
                        accentColor = Color(0xFF49D89B),
                        onClick = { selectedRiskIndex = index },
                    )
                }
            }
        }

        if (uiState.primaryActionLabel.isNotBlank()) {
            P1PrimaryCta(
                text = uiState.primaryActionLabel,
                onClick = onPrimaryAction,
                modifier = Modifier.fillMaxWidth(),
            )
        }
    }
}

private data class WalletPaymentConfirmRows(
    val orderNoLabel: String,
    val summaryRows: List<Pair<String, String>>,
)

private fun paymentConfirmRows(uiState: WalletPaymentConfirmUiState): WalletPaymentConfirmRows {
    val order = uiState.order
    return WalletPaymentConfirmRows(
        orderNoLabel = order?.orderNo?.let { "订单 #$it" } ?: "订单未找到",
        summaryRows = buildList {
            if (order != null) {
                add("套餐" to order.planName)
                add("订单状态" to order.statusText.ifBlank { order.status })
                add("支付资产" to "${order.assetCode} · ${order.networkCode}")
            }
            uiState.detailLines.forEach { add(it.label to it.value) }
        },
    )
}

private fun paymentConfirmHubLabel(
    selectedRiskIndex: Int,
    focusedSummaryIndex: Int,
): String = when {
    selectedRiskIndex >= 0 -> "CHECK"
    focusedSummaryIndex >= 4 -> "PAY"
    else -> "SAFE"
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletPaymentConfirmPreview() {
    CryptoVpnTheme {
        WalletPaymentConfirmScreen(
            uiState = walletPaymentConfirmPreviewState(),
            onPrimaryAction = {},
        )
    }
}
