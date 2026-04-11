package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.ReceiveEvent
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.receivePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.ReceiveViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ReceiveRoute(
    viewModel: ReceiveViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ReceiveScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ReceiveEvent.PrimaryActionClicked -> onPrimaryAction()
                ReceiveEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ReceiveScreen(
    uiState: ReceiveUiState,
    onEvent: (ReceiveEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val chips = uiState.metrics.take(3).map { it.value }
    val address = uiState.fields.firstOrNull()?.value ?: "--"
    val status = uiState.metrics.getOrNull(3)?.value ?: "已校验"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
    ) {
        if (chips.isNotEmpty()) {
            P2CoreChipRow(items = chips, activeIndex = 0)
        }
        P2CoreQrAddressCard(
            title = "收款二维码",
            subtitle = "扫码转账前请确认网络与资产一致",
            status = status,
            address = address,
        ) {
            P2CoreActionValueRow(
                label = "收款地址",
                value = address,
                actionLabel = uiState.secondaryActionLabel ?: "分享二维码",
                onAction = { onEvent(ReceiveEvent.SecondaryActionClicked) },
            )
        }
        P2CoreHeroValueCard(
            label = "到账预估",
            value = uiState.metrics.firstOrNull()?.value ?: "--",
            supportingText = uiState.summary,
            highlight = uiState.badge,
            stats = uiState.metrics.drop(1).take(2).map { it.label to it.value },
        )
        P2CoreAddressModule(
            title = "收款地址",
            value = address,
            supportingText = uiState.note,
            status = status,
            primaryActionLabel = uiState.secondaryActionLabel ?: "分享二维码",
            onPrimaryAction = { onEvent(ReceiveEvent.SecondaryActionClicked) },
            secondaryActionLabel = uiState.primaryActionLabel,
            onSecondaryAction = { onEvent(ReceiveEvent.PrimaryActionClicked) },
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReceivePreview() {
    CryptoVpnTheme {
        ReceiveScreen(
            uiState = receivePreviewState(),
            onEvent = {},
        )
    }
}
