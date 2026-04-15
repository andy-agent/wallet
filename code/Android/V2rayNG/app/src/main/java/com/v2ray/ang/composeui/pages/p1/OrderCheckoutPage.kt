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
import com.v2ray.ang.composeui.p0.ui.P01RealQr
import com.v2ray.ang.composeui.p0.ui.P01SecondaryButton
import com.v2ray.ang.composeui.p0.ui.P01Tab
import com.v2ray.ang.composeui.p1.model.OrderCheckoutEvent
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.checkoutPaymentLabel
import com.v2ray.ang.composeui.p1.model.orderCheckoutPreviewState
import com.v2ray.ang.composeui.p1.model.resolvedPaymentQrText
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
        onCreateOrder = { viewModel.onEvent(OrderCheckoutEvent.CreateOrderClicked) },
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
    onCreateOrder: () -> Unit,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onPaymentOptionRoute: (String) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val clipboardManager = LocalClipboardManager.current
    val qrText = uiState.resolvedPaymentQrText()
    val paymentLabel = checkoutPaymentLabel(uiState.assetCode, uiState.networkCode)
    val rows = listOfNotNull(
        "套餐" to uiState.planTitle.ifBlank { uiState.planCode.orEmpty() }.ifBlank { null },
        "节点区域" to uiState.selectedRegionLabel.ifBlank { "支付后补选" },
        "支付网络" to paymentLabel.ifBlank { null },
        "订单金额" to uiState.payableAmount.takeIf { it.isNotBlank() }?.let { "$it ${uiState.assetCode}" },
    )
    val orderLabel = uiState.orderNo?.let { "ORD-${it.takeLast(8)}" }
    val statusMessage = uiState.screenState.unavailableMessage
        ?: uiState.screenState.errorMessage
        ?: uiState.screenState.emptyMessage
        ?: uiState.summary
    val secondaryButtonText = when {
        uiState.collectionAddress.isNotBlank() -> "复制地址"
        uiState.orderNo != null -> "刷新订单"
        else -> "重选区域"
    }
    val primaryButtonText = when {
        uiState.orderNo != null -> "我已完成支付"
        uiState.screenState.isLoading -> "正在创建订单"
        else -> "创建订单"
    }

    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.orderCheckout.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "CHECKOUT",
            title = "订单收银台",
            subtitle = "",
            chips = listOf(uiState.expiresAt?.substringBefore('.')?.replace('T', ' ') ?: "待创建订单"),
            backLabel = "<",
            onBack = onSecondaryAction,
            trailing = { P1SecureHub(label = paymentLabel.ifBlank { "PAY" }) },
        )

        P01Card {
            P01CardHeader(title = "订单信息")
            P01CardCopy(statusMessage)
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
            if (paymentLabel.isNotBlank()) {
                P01Chip(text = paymentLabel)
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
            }
        }

        if (uiState.orderNo != null || qrText.isNotBlank() || uiState.collectionAddress.isNotBlank()) {
            P1SelectableCard(
                selected = qrText.isNotBlank(),
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
                    if (uiState.screenState.isUnavailable) {
                        statusMessage
                    } else {
                        ""
                    }
                )
                if (qrText.isNotBlank()) {
                    P01RealQr(content = qrText)
                    P01CardCopy(uiState.collectionAddress)
                } else {
                    P01CardCopy(
                        if (uiState.screenState.isUnavailable) {
                            statusMessage
                        } else {
                            uiState.collectionAddress
                        },
                    )
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            P01SecondaryButton(
                text = secondaryButtonText,
                onClick = {
                    when {
                        uiState.collectionAddress.isNotBlank() -> {
                            clipboardManager.setText(AnnotatedString(uiState.collectionAddress))
                        }
                        uiState.orderNo != null -> {
                            onRefresh()
                        }
                        else -> {
                            onSecondaryAction()
                        }
                    }
                },
                modifier = Modifier.weight(1f),
            )
            P1PrimaryCta(
                text = primaryButtonText,
                onClick = {
                    if (uiState.orderNo == null) {
                        onCreateOrder()
                    } else {
                        onPrimaryAction()
                    }
                },
                modifier = Modifier.weight(1f),
                active = if (uiState.orderNo == null) {
                    uiState.paymentOptions.isNotEmpty() && !uiState.screenState.isLoading
                } else {
                    true
                },
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
            onCreateOrder = {},
            onPrimaryAction = {},
            onSecondaryAction = {},
            onPaymentOptionRoute = {},
        )
    }
}
