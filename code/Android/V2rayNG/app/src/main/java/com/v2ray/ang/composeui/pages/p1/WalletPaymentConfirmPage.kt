package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01ListRow
import com.v2ray.ang.composeui.p0.ui.P01MetricCell
import com.v2ray.ang.composeui.p0.ui.P01MetricGrid
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01PrimaryButton
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
        )

        P01Card {
            P01CardHeader(
                title = "订单摘要",
                trailing = { P01Chip(text = rows.orderNoLabel) },
            )
            P01List {
                rows.summaryRows.forEach { (title, value) ->
                    P01ListRow(title = title, value = value)
                }
            }
        }

        P01Card {
            P01MetricGrid(
                items = listOf(
                    P01MetricCell("剩余余额", rows.balanceText),
                    P01MetricCell("路由状态", "已加密"),
                ),
            )
            P01CardCopy("支付后 · VPN 广播交易。")
        }

        P01Card {
            P01CardHeader(
                title = "风控提示",
                trailing = { P01Chip(text = "建议查看") },
            )
            P01List {
                P01ListRow(
                    title = "收款地址已绑定官方商户",
                    copy = "避免因中间人攻击造成错误转账。",
                )
                P01ListRow(
                    title = "当前节点延迟稳定",
                    copy = "提交交易不会因重试产生重复扣款。",
                )
            }
        }

        P01PrimaryButton(
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
    val highlightMap = uiState.highlights.associateBy { it.title }
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
