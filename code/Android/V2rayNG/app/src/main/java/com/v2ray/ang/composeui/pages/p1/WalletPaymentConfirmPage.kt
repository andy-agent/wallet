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
            title = "钱包支付确认",
            subtitle = "把 VPN 续费变成标准的钱包支付流，而不是额外弹窗。",
            chips = listOf("链路安全"),
            trailing = { P1SecureHub(label = paymentConfirmHubLabel(selectedRiskIndex, focusedSummaryIndex)) },
        )

        P01Card {
            P01CardHeader(
                title = "订单摘要",
                trailing = { P01Chip(text = rows.orderNoLabel) },
            )
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

        P1SelectableCard(
            selected = true,
            accentColor = Color(0xFF49D89B),
        ) {
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("剩余余额", rows.balanceText),
                    P01MetricCell("路由状态", "已加密"),
                ),
            )
            P01CardCopy(
                if (selectedRiskIndex >= 0) {
                    "风控项已确认 · VPN 广播交易。"
                } else {
                    "支付后 · VPN 广播交易。"
                },
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
                listOf(
                    "收款地址已绑定官方商户" to "避免因中间人攻击造成错误转账。",
                    "当前节点延迟稳定" to "提交交易不会因重试产生重复扣款。",
                ).forEachIndexed { index, (title, copy) ->
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

        P1PrimaryCta(
            text = "确认支付并开通",
            onClick = onPrimaryAction,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}

private data class WalletPaymentConfirmRows(
    val orderNoLabel: String,
    val balanceText: String,
    val summaryRows: List<Pair<String, String>>,
)

private fun paymentConfirmRows(uiState: WalletPaymentConfirmUiState): WalletPaymentConfirmRows {
    val metrics = uiState.metrics.associate { it.label to it.value }
    val contentHighlights = uiState.highlights.p1ContentItems()
    val highlightMap = contentHighlights.associateBy { it.title }
    val planTitle = highlightMap.keys.firstOrNull { !it.contains("收款地址") && !it.contains("订单状态") }
    return WalletPaymentConfirmRows(
        orderNoLabel = metrics["订单号"]?.let { "订单 #$it" } ?: "订单 #CVP-2409",
        balanceText = "12,781.99",
        summaryRows = listOf(
            "套餐" to (planTitle ?: "年度 Pro"),
            "可用设备" to "5 台",
            "节点权益" to "高速专线 + 智能分流",
            "支付资产" to ((metrics["支付币种"] ?: "USDT") + " · TRON"),
            "支付金额" to (highlightMap[planTitle]?.trailing ?: "58.00 USDT"),
            "网络费" to (metrics["网络手续费"] ?: "1.24 USDT"),
            "预计开通" to "1 分钟内",
        ),
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
