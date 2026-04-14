package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p1.model.OrderCheckoutEvent
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.orderCheckoutPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.OrderCheckoutViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun OrderCheckoutRoute(
    viewModel: OrderCheckoutViewModel,
    onPrimaryAction: (String) -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onPaymentOptionRoute: (String) -> Unit = {},
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    OrderCheckoutScreen(
        uiState = uiState,
        onRefresh = { viewModel.onEvent(OrderCheckoutEvent.Refresh) },
        onPrimaryAction = {
            viewModel.onEvent(OrderCheckoutEvent.PrimaryActionClicked)
            uiState.orderNo?.let(onPrimaryAction)
        },
        onSecondaryAction = {
            viewModel.onEvent(OrderCheckoutEvent.SecondaryActionClicked)
            onSecondaryAction?.invoke()
        },
        onPaymentOptionRoute = onPaymentOptionRoute,
        onBottomNav = onBottomNav,
    )
}

@Composable
fun OrderCheckoutScreen(
    uiState: OrderCheckoutUiState,
    onRefresh: () -> Unit,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onPaymentOptionRoute: (String) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val rows = listOfNotNull(
        "套餐" to uiState.planTitle.ifBlank { uiState.planCode.orEmpty() }.ifBlank { null },
        "支付网络" to uiState.networkCode.ifBlank { null },
        "订单金额" to uiState.payableAmount.takeIf { it.isNotBlank() }?.let { "$it ${uiState.assetCode}" },
    )
    val orderLabel = uiState.orderNo?.let { "ORD-${it.takeLast(8)}" }

    P01PhoneScaffold(
        statusTime = "18:33",
        currentRoute = CryptoVpnRouteSpec.orderCheckout.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "CHECKOUT",
            title = "订单收银台",
            subtitle = "确认套餐、支付网络与到账说明。",
            chips = listOf(uiState.expiresAt?.substringBefore('.')?.replace('T', ' ') ?: "当前订单"),
            backLabel = "<",
            onBack = onSecondaryAction,
            trailing = { P1SecureHub(label = uiState.networkCode.ifBlank { "PAY" }) },
        )

        P01Card {
            P01CardHeader(title = "订单信息")
            P01CardCopy(
                uiState.screenState.unavailableMessage
                    ?: uiState.screenState.errorMessage
                    ?: uiState.screenState.emptyMessage
                    ?: "当前页面直接展示真实订单号与真实 payment target，不再使用示例地址。"
            )
            P01List {
                rows.forEach { (title, value) ->
                    value?.let {
                        P1FeedbackRow(
                            title = title,
                            value = it,
                            selected = title == "订单金额" || title == "支付网络",
                        )
                    }
                }
            }
        }

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (uiState.networkCode.isNotBlank()) {
                P01Chip(text = uiState.networkCode)
            }
        }

        if (uiState.paymentOptions.isNotEmpty()) {
            P01Card {
                P01CardHeader(title = "选择支付网络")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    uiState.paymentOptions.forEach { option ->
                        P01Tab(
                            text = option.label,
                            selected = option.selected,
                            onClick = {
                                val planCode = uiState.planCode ?: return@P01Tab
                                onPaymentOptionRoute(
                                    CryptoVpnRouteSpec.orderCheckoutRoute(
                                        planId = planCode,
                                        assetCode = option.assetCode,
                                        networkCode = option.networkCode,
                                    ),
                                )
                            },
                        )
                    }
                }
                P01CardCopy("支付选项来自服务端钱包资产目录，切换后会重新创建对应真实订单。")
            }
        }

        P1SelectableCard(
            selected = uiState.collectionAddress.isNotBlank(),
        ) {
            P01CardHeader(
                title = "扫码支付",
                trailing = {
                    if (!orderLabel.isNullOrBlank()) {
                        P01Chip(text = orderLabel)
                    }
                },
            )
            P01CardCopy(
                if (uiState.collectionAddress.isBlank()) {
                    "当前服务不可签发链路"
                } else {
                    "使用当前网络的钱包完成付款，或复制真实收款地址。"
                },
            )
            if (uiState.collectionAddress.isNotBlank()) {
                P01QrArt()
                P01CardCopy(uiState.collectionAddress)
            } else {
                P01CardCopy("当前没有可用的真实收款地址，请刷新订单。")
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            P01SecondaryButton(
                text = if (uiState.collectionAddress.isBlank()) "刷新订单" else "复制地址",
                onClick = {
                    if (uiState.collectionAddress.isBlank()) {
                        onRefresh()
                    } else {
                        clipboardManager.setText(AnnotatedString(uiState.collectionAddress))
                    }
                },
                modifier = Modifier.weight(1f),
            )
            P1PrimaryCta(
                text = if (uiState.orderNo == null) "重试创建订单" else "我已完成支付",
                onClick = {
                    if (uiState.orderNo == null) {
                        onRefresh()
                    } else {
                        onPrimaryAction()
                    }
                },
                modifier = Modifier.weight(1f),
                active = uiState.orderNo != null || uiState.screenState.hasError,
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun OrderCheckoutPreview() {
    CryptoVpnTheme {
        OrderCheckoutScreen(
            uiState = orderCheckoutPreviewState(),
            onRefresh = {},
            onPrimaryAction = {},
            onSecondaryAction = {},
            onPaymentOptionRoute = {},
        )
    }
}
