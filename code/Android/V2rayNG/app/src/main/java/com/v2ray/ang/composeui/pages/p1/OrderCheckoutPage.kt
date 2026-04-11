package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p0.ui.P01Card
import com.v2ray.ang.composeui.p0.ui.P01CardCopy
import com.v2ray.ang.composeui.p0.ui.P01CardHeader
import com.v2ray.ang.composeui.p0.ui.P01Chip
import com.v2ray.ang.composeui.p0.ui.P01Header
import com.v2ray.ang.composeui.p0.ui.P01List
import com.v2ray.ang.composeui.p0.ui.P01PhoneScaffold
import com.v2ray.ang.composeui.p0.ui.P01QrArt
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p1.model.OrderCheckoutEvent
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.orderCheckoutPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderCheckoutViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderCheckoutRoute(
    viewModel: OrderCheckoutViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderCheckoutScreen(
        uiState = uiState,
        onPrimaryAction = {
            viewModel.onEvent(OrderCheckoutEvent.PrimaryActionClicked)
            onPrimaryAction()
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderCheckoutScreen(
    uiState: OrderCheckoutUiState,
    onPrimaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val checkoutData = rememberCheckoutData(uiState)
    var selectedNetwork by rememberSaveable { mutableStateOf(checkoutData.networks.first()) }
    var copiedNetwork by rememberSaveable { mutableStateOf<String?>(null) }
    val address = checkoutData.addresses[selectedNetwork] ?: checkoutData.addresses.values.first()
    val networkAccent = checkoutNetworkAccent(selectedNetwork)
    val orderRows = listOf(
        "套餐" to checkoutData.planTitle,
        "支付网络" to selectedNetwork,
        "订单金额" to checkoutData.amountText,
    )

    P01PhoneScaffold(
        statusTime = "18:33",
        currentRoute = CryptoVpnRouteSpec.plans.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "CHECKOUT",
            title = "订单收银台",
            subtitle = "确认套餐、支付网络与到账说明。",
            chips = listOf("• 10分钟有效"),
            backLabel = "<",
            onBack = { onBottomNav(CryptoVpnRouteSpec.plans.pattern) },
            trailing = { P1SecureHub(label = "PAY") },
        )

        P01Card {
            P01CardHeader(title = "订单信息")
            P01CardCopy("选择链上资产完成支付，系统会自动激活对应订阅。")
            P01List {
                orderRows.forEach { (title, value) ->
                    P1FeedbackRow(
                        title = title,
                        value = value,
                        selected = title == "支付网络" || title == "订单金额",
                        accentColor = if (title == "订单金额") Color(0xFFF6B155) else networkAccent,
                        valueColor = if (title == "订单金额") Color(0xFFF6B155) else networkAccent,
                    )
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            checkoutData.networks.forEach { label ->
                Box(
                    modifier = Modifier.clickable {
                        selectedNetwork = label
                        copiedNetwork = null
                    },
                ) {
                    P01Chip(
                        text = label,
                        highlighted = label == selectedNetwork,
                    )
                }
            }
        }

        P1SelectableCard(
            selected = true,
            accentColor = networkAccent,
        ) {
            P01CardHeader(title = "扫码支付")
            P01CardCopy("使用任意支持 $selectedNetwork 的钱包完成付款，或复制地址转账。")
            androidx.compose.foundation.layout.Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                P01QrArt()
                androidx.compose.material3.Text(
                    text = address,
                    color = if (copiedNetwork == selectedNetwork) networkAccent else Color(0xFF7B8DB0),
                )
            }
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                P01SecondaryButton(
                    text = if (copiedNetwork == selectedNetwork) "已复制地址" else "复制地址",
                    onClick = {
                        clipboardManager.setText(AnnotatedString(address))
                        copiedNetwork = selectedNetwork
                    },
                    modifier = Modifier.weight(1f),
                )
                P1PrimaryCta(
                    text = "我已完成支付",
                    onClick = onPrimaryAction,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

private data class CheckoutUiData(
    val planTitle: String,
    val amountText: String,
    val networks: List<String>,
    val addresses: Map<String, String>,
)

private fun rememberCheckoutData(uiState: OrderCheckoutUiState): CheckoutUiData {
    val metricMap = uiState.metrics.associate { it.label to it.value }
    val contentHighlights = uiState.highlights.p1ContentItems()
    val planTitle = metricMap["套餐"] ?: contentHighlights.firstOrNull()?.title ?: "年费 Pro"
    val amountText = metricMap["金额"] ?: contentHighlights.firstOrNull()?.trailing?.takeIf { it.isNotBlank() } ?: "149.00 USDT"
    return CheckoutUiData(
        planTitle = planTitle,
        amountText = amountText,
        networks = listOf("USDT-TRON", "USDT-Solana", "SOL"),
        addresses = mapOf(
            "USDT-TRON" to "TXvM...eR92",
            "USDT-Solana" to "7H8n...A6Qp",
            "SOL" to "4s9K...Lm12",
        ),
    )
}

private fun checkoutNetworkAccent(network: String): Color =
    when (network) {
        "USDT-TRON" -> Color(0xFF20C4F4)
        "USDT-Solana" -> Color(0xFF4276FF)
        else -> Color(0xFFF6B155)
    }

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderCheckoutPreview() {
    CryptoVpnTheme {
        OrderCheckoutScreen(
            uiState = orderCheckoutPreviewState(),
            onPrimaryAction = {},
        )
    }
}
