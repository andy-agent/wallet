package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.components.actions.ActionCluster
import com.v2ray.ang.composeui.components.actions.ActionClusterAction
import com.v2ray.ang.composeui.components.app.AppPageBackgroundStyle
import com.v2ray.ang.composeui.components.app.AppPageScaffold
import com.v2ray.ang.composeui.components.buttons.AppButtonVariant
import com.v2ray.ang.composeui.components.cards.PaymentSummaryCard
import com.v2ray.ang.composeui.components.cards.PaymentSummaryField
import com.v2ray.ang.composeui.components.cards.QrAddressCard
import com.v2ray.ang.composeui.components.chips.AppChip
import com.v2ray.ang.composeui.components.chips.AppChipTone
import com.v2ray.ang.composeui.components.feedback.EmptyStateCard
import com.v2ray.ang.composeui.components.navigation.AppTopBar
import com.v2ray.ang.composeui.components.navigation.AppTopBarMode
import com.v2ray.ang.composeui.components.navigation.CryptoVpnBottomBar
import com.v2ray.ang.composeui.components.rows.LabelValueRow
import com.v2ray.ang.composeui.components.sections.InfoSection
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p1.model.OrderCheckoutEvent
import com.v2ray.ang.composeui.p1.model.OrderCheckoutUiState
import com.v2ray.ang.composeui.p1.model.PayerWalletOptionUi
import com.v2ray.ang.composeui.p1.model.checkoutPaymentLabel
import com.v2ray.ang.composeui.p1.model.orderCheckoutPreviewState
import com.v2ray.ang.composeui.p1.model.resolvedPaymentQrText
import com.v2ray.ang.composeui.p1.viewmodel.OrderCheckoutViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

private val CheckoutGlowBlue = Color(0x224F7CFF)
private val CheckoutGlowCyan = Color(0x1625D7FF)

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
        onSelectPayerWallet = { walletId, chainAccountId ->
            viewModel.onEvent(OrderCheckoutEvent.PayerWalletSelected(walletId, chainAccountId))
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
    onSelectPayerWallet: (String, String) -> Unit,
    onSecondaryAction: () -> Unit,
    onPaymentOptionRoute: (String) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val baseline = OverviewBaselineTokens.primary
    val clipboardManager = LocalClipboardManager.current
    val qrText = uiState.resolvedPaymentQrText()
    val paymentLabel = checkoutPaymentLabel(uiState.assetCode, uiState.networkCode)
    val orderLabel = uiState.orderNo?.let { "ORD-${it.takeLast(8)}" }
    val statusMessage = uiState.screenState.unavailableMessage
        ?: uiState.screenState.errorMessage
        ?: uiState.screenState.emptyMessage
        ?: uiState.summary
    val secondaryButtonText = when {
        uiState.collectionAddress.isNotBlank() -> "复制地址"
        uiState.orderNo != null -> "刷新订单"
        else -> uiState.secondaryActionLabel ?: "重选区域"
    }
    val primaryButtonText = when {
        uiState.orderNo != null -> "我已完成支付"
        uiState.screenState.isLoading -> "正在创建订单"
        else -> "创建订单"
    }
    val summaryFields = buildList {
        add(PaymentSummaryField("套餐", uiState.planTitle.ifBlank { uiState.planCode.orEmpty() }.ifBlank { "--" }))
        add(PaymentSummaryField("节点区域", uiState.selectedRegionLabel.ifBlank { "支付后补选" }))
        if (paymentLabel.isNotBlank()) {
            add(PaymentSummaryField("支付网络", paymentLabel))
        }
        if (uiState.payableAmount.isNotBlank()) {
            add(PaymentSummaryField("订单金额", "${uiState.payableAmount} ${uiState.assetCode}"))
        }
    }

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        background = { CheckoutBackgroundGlow() },
        bottomBar = {
            CryptoVpnBottomBar(
                currentRoute = CryptoVpnRouteSpec.plans.name,
                onRouteSelected = onBottomNav,
            )
        },
        contentPadding = PaddingValues(
            horizontal = baseline.pageHorizontal,
            vertical = baseline.pageTopSpacing,
        ),
    ) { _ ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .widthIn(max = 680.dp),
            verticalArrangement = Arrangement.spacedBy(baseline.sectionGap),
        ) {
            AppTopBar(
                title = "订单收银台",
                subtitle = uiState.subtitle,
                mode = AppTopBarMode.Hero,
                actions = {
                    if (!orderLabel.isNullOrBlank()) {
                        AppChip(text = orderLabel, tone = AppChipTone.Info)
                    } else if (paymentLabel.isNotBlank()) {
                        AppChip(text = paymentLabel, tone = AppChipTone.Brand)
                    }
                },
            )

            PaymentSummaryCard(
                title = "订单信息",
                subtitle = statusMessage,
                fields = summaryFields,
            )

            if (uiState.paymentOptions.isNotEmpty()) {
                InfoSection(
                    title = "选择支付网络",
                    subtitle = "切换支付资产与网络，不改当前业务流程",
                ) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
                    ) {
                        uiState.paymentOptions.forEach { option ->
                            AppChip(
                                text = option.label,
                                tone = AppChipTone.Brand,
                                selected = option.selected,
                                onClick = {
                                    val planCode = uiState.planCode ?: return@AppChip
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

            if (uiState.payerWalletOptions.isNotEmpty()) {
                InfoSection(
                    title = "选择付款钱包",
                    subtitle = "显式绑定付款钱包与链账户",
                ) {
                    Row(
                        modifier = Modifier.horizontalScroll(rememberScrollState()),
                        horizontalArrangement = Arrangement.spacedBy(AppTheme.spacing.space8),
                    ) {
                        uiState.payerWalletOptions.forEach { option ->
                            AppChip(
                                text = option.label,
                                tone = if (option.capability == "SIGN_AND_PAY") AppChipTone.Brand else AppChipTone.Info,
                                selected = option.selected,
                                onClick = {
                                    if (option.capability == "SIGN_AND_PAY") {
                                        onSelectPayerWallet(option.walletId, option.chainAccountId)
                                    }
                                },
                            )
                        }
                    }
                }
            }

            if (uiState.screenState.hasError && uiState.orderNo == null && uiState.paymentOptions.isEmpty()) {
                EmptyStateCard(
                    title = "当前无法创建订单",
                    message = statusMessage,
                    actionLabel = "重新加载",
                    onAction = onRefresh,
                )
            }

            if (uiState.orderNo != null || qrText.isNotBlank() || uiState.collectionAddress.isNotBlank()) {
                QrAddressCard(
                    title = "扫码支付",
                    subtitle = statusMessage,
                    qrContent = qrText,
                    address = uiState.collectionAddress,
                    addressLabel = "收款地址",
                    supportingText = "使用钱包扫码或复制地址完成支付",
                    status = orderLabel,
                    modifier = Modifier.fillMaxWidth(),
                )
            }

            ActionCluster(
                actions = listOf(
                    ActionClusterAction(
                        label = secondaryButtonText,
                        onClick = {
                            when {
                                uiState.collectionAddress.isNotBlank() -> {
                                    clipboardManager.setText(AnnotatedString(uiState.collectionAddress))
                                }

                                uiState.orderNo != null -> onRefresh()
                                else -> onSecondaryAction()
                            }
                        },
                        variant = AppButtonVariant.Secondary,
                    ),
                    ActionClusterAction(
                        label = primaryButtonText,
                        onClick = {
                            if (uiState.orderNo == null) {
                                onCreateOrder()
                            } else {
                                onPrimaryAction()
                            }
                        },
                        variant = AppButtonVariant.Primary,
                    ),
                ),
            )
        }
    }
}

@Composable
private fun CheckoutBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 40.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(CheckoutGlowBlue, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 320.dp)
                .size(260.dp)
                .background(CheckoutGlowCyan, RoundedCornerShape(999.dp))
                .blur(60.dp),
        )
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F7FF)
@Composable
private fun OrderCheckoutPreview() {
    CryptoVpnTheme {
        Surface {
            OrderCheckoutScreen(
                uiState = orderCheckoutPreviewState(),
                onRefresh = {},
                onCreateOrder = {},
                onPrimaryAction = {},
                onSelectPayerWallet = { _, _ -> },
                onSecondaryAction = {},
                onPaymentOptionRoute = {},
            )
        }
    }
}
