package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmEvent
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmUiState
import com.v2ray.ang.composeui.p1.model.resolvedPaymentQrText
import com.v2ray.ang.composeui.p1.model.walletPaymentConfirmPreviewState
import com.v2ray.ang.composeui.p1.viewmodel.WalletPaymentConfirmViewModel
import com.v2ray.ang.composeui.theme.AppTheme
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.theme.tokens.OverviewBaselineTokens

private val ConfirmGlowBlue = Color(0x224F7CFF)
private val ConfirmGlowPurple = Color(0x148C7CFF)

@Composable
fun WalletPaymentConfirmRoute(
    viewModel: WalletPaymentConfirmViewModel,
    onPrimaryAction: (String) -> Unit = {},
    onSecondaryAction: ((String) -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletPaymentConfirmScreen(
        uiState = uiState,
        onRefresh = { viewModel.onEvent(WalletPaymentConfirmEvent.Refresh) },
        onPrimaryAction = {
            viewModel.onEvent(WalletPaymentConfirmEvent.PrimaryActionClicked)
            uiState.orderNo?.let(onPrimaryAction)
        },
        onSecondaryAction = {
            viewModel.onEvent(WalletPaymentConfirmEvent.SecondaryActionClicked)
            uiState.planCode?.let(onSecondaryAction ?: return@WalletPaymentConfirmScreen)
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletPaymentConfirmScreen(
    uiState: WalletPaymentConfirmUiState,
    onRefresh: () -> Unit,
    onPrimaryAction: () -> Unit,
    onSecondaryAction: () -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val baseline = OverviewBaselineTokens.primary
    val qrText = uiState.resolvedPaymentQrText()
    val orderLabel = uiState.orderNo?.let { "订单 #$it" }
    val statusMessage = uiState.screenState.unavailableMessage
        ?: uiState.screenState.errorMessage
        ?: uiState.screenState.emptyMessage
        ?: uiState.note
    val summaryFields = buildList {
        add(PaymentSummaryField("套餐", uiState.planTitle.ifBlank { uiState.planCode.orEmpty() }.ifBlank { "--" }))
        add(PaymentSummaryField("订单状态", uiState.statusText.ifBlank { uiState.status.orEmpty() }.ifBlank { "--" }))
        val paymentLabel = listOf(uiState.assetCode, uiState.networkCode).filter { it.isNotBlank() }.joinToString(" · ")
        if (paymentLabel.isNotBlank()) add(PaymentSummaryField("支付资产", paymentLabel))
        if (!uiState.orderNo.isNullOrBlank()) add(PaymentSummaryField("订单号", uiState.orderNo))
        if (uiState.payableAmount.isNotBlank()) add(PaymentSummaryField("应付金额", "${uiState.payableAmount} ${uiState.assetCode}"))
        uiState.uniqueAmountDelta?.takeIf { it.isNotBlank() }?.let { add(PaymentSummaryField("唯一尾差", it)) }
        uiState.expiresAt?.takeIf { it.isNotBlank() }?.let { add(PaymentSummaryField("到期时间", it)) }
    }

    AppPageScaffold(
        backgroundStyle = AppPageBackgroundStyle.Hero,
        background = { WalletConfirmBackgroundGlow() },
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
                title = "钱包支付确认",
                subtitle = uiState.subtitle,
                mode = AppTopBarMode.Hero,
                actions = {
                    AppChip(
                        text = uiState.statusText.ifBlank { "SAFE" },
                        tone = confirmStatusTone(uiState.statusText.ifBlank { uiState.status.orEmpty() }),
                    )
                },
            )

            PaymentSummaryCard(
                title = "订单摘要",
                subtitle = statusMessage,
                fields = summaryFields,
            )

            if (uiState.screenState.hasError && uiState.orderNo == null) {
                EmptyStateCard(
                    title = "支付确认暂不可用",
                    message = statusMessage,
                    actionLabel = "刷新订单",
                    onAction = onRefresh,
                )
            }

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

            ActionCluster(
                actions = listOf(
                    ActionClusterAction(
                        label = uiState.secondaryActionLabel ?: "返回结算页",
                        onClick = onSecondaryAction,
                        variant = AppButtonVariant.Secondary,
                    ),
                    ActionClusterAction(
                        label = if (uiState.orderNo == null) "刷新订单" else uiState.primaryActionLabel,
                        onClick = {
                            if (uiState.orderNo == null) {
                                onRefresh()
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
private fun WalletConfirmBackgroundGlow() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 36.dp),
    ) {
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .size(220.dp)
                .background(ConfirmGlowPurple, RoundedCornerShape(999.dp))
                .blur(48.dp),
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(top = 320.dp)
                .size(260.dp)
                .background(ConfirmGlowBlue, RoundedCornerShape(999.dp))
                .blur(60.dp),
        )
    }
}

private fun confirmStatusTone(status: String): AppChipTone = when {
    status.contains("完成", ignoreCase = true) -> AppChipTone.Success
    status.contains("PENDING", ignoreCase = true) -> AppChipTone.Warning
    status.contains("待", ignoreCase = true) -> AppChipTone.Warning
    status.contains("失败", ignoreCase = true) -> AppChipTone.Error
    status.contains("ERROR", ignoreCase = true) -> AppChipTone.Error
    else -> AppChipTone.Info
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletPaymentConfirmPreview() {
    CryptoVpnTheme {
        Surface {
            WalletPaymentConfirmScreen(
                uiState = walletPaymentConfirmPreviewState(),
                onRefresh = {},
                onPrimaryAction = {},
                onSecondaryAction = {},
            )
        }
    }
}
