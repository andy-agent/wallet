package com.v2ray.ang.composeui.pages.p1

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
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
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmEvent
import com.v2ray.ang.composeui.p1.model.WalletPaymentConfirmUiState
import com.v2ray.ang.composeui.p1.model.walletPaymentConfirmPreviewState
import com.v2ray.ang.composeui.p1.model.resolvedPaymentQrText
import com.v2ray.ang.composeui.p1.viewmodel.WalletPaymentConfirmViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

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
    val qrText = uiState.resolvedPaymentQrText()
    val rows = listOfNotNull(
        "套餐" to uiState.planTitle.ifBlank { uiState.planCode.orEmpty() }.ifBlank { null },
        "订单状态" to uiState.statusText.ifBlank { uiState.status.orEmpty() }.ifBlank { null },
        "支付资产" to listOf(uiState.assetCode, uiState.networkCode).filter { it.isNotBlank() }.joinToString(" · ").ifBlank { null },
        "订单号" to uiState.orderNo,
        "应付金额" to uiState.payableAmount.takeIf { it.isNotBlank() }?.let { "$it ${uiState.assetCode}" },
        "唯一尾差" to uiState.uniqueAmountDelta,
        "到期时间" to uiState.expiresAt,
    )

    P01PhoneScaffold(
        currentRoute = CryptoVpnRouteSpec.walletPaymentConfirm.name,
        onBottomNav = onBottomNav,
    ) {
        P01Header(
            eyebrow = "WALLET PAYMENT",
            title = "钱包支付确认",
            subtitle = uiState.summary,
            chips = listOf("链路安全"),
            trailing = { P1SecureHub(label = uiState.statusText.ifBlank { "SAFE" }) },
        )

        P01Card {
            P01CardHeader(
                title = "订单摘要",
                trailing = {
                    uiState.orderNo?.let { P01Chip(text = "订单 #$it") }
                },
            )
            P01CardCopy(
                uiState.screenState.unavailableMessage
                    ?: uiState.screenState.errorMessage
                    ?: uiState.screenState.emptyMessage
                    ?: uiState.note,
            )
            P01List {
                rows.forEach { (title, value) ->
                    value?.takeIf { it.isNotBlank() }?.let {
                        P1FeedbackRow(
                            title = title,
                            value = it,
                            selected = title == "套餐",
                        )
                    }
                }
                if (uiState.collectionAddress.isNotBlank()) {
                    P1FeedbackRow(
                        title = "收款地址",
                        value = uiState.collectionAddress,
                    )
                }
            }
        }

        P1SelectableCard(
            selected = qrText.isNotBlank(),
        ) {
            P01CardHeader(title = "扫码支付")
            P01CardCopy(
                if (qrText.isBlank()) {
                    ""
                } else {
                    ""
                },
            )
            if (qrText.isNotBlank()) {
                P01RealQr(content = qrText)
                if (uiState.collectionAddress.isNotBlank()) {
                    P01CardCopy(uiState.collectionAddress)
                }
            } else if (uiState.collectionAddress.isNotBlank()) {
                P01CardCopy(uiState.collectionAddress)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            P01SecondaryButton(
                text = uiState.secondaryActionLabel ?: "返回结算页",
                onClick = onSecondaryAction,
                modifier = Modifier.weight(1f),
            )
            P1PrimaryCta(
                text = if (uiState.orderNo == null) "刷新订单" else uiState.primaryActionLabel,
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
private fun WalletPaymentConfirmPreview() {
    CryptoVpnTheme {
        WalletPaymentConfirmScreen(
            uiState = walletPaymentConfirmPreviewState(),
            onRefresh = {},
            onPrimaryAction = {},
            onSecondaryAction = {},
        )
    }
}
