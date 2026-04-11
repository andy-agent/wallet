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
    val addressPreview = if (address.length > 14) "${address.take(6)}...${address.takeLast(6)}" else address
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
    ) {
        P2CoreHeroValueCard(
            label = "当前收款网络",
            value = chips.firstOrNull() ?: (uiState.badge ?: "--"),
            supportingText = uiState.summary,
            highlight = uiState.badge,
            stats = listOf(
                "地址尾号" to addressPreview,
                "校验状态" to status,
            ),
        )
        P2CoreQrAddressCard(
            title = "收款二维码",
            subtitle = "扫码或复制地址进行转账",
            status = status,
            statusColor = androidx.compose.ui.graphics.Color(0xFFE6FFF6),
            address = address,
            addressLabel = "收款地址",
            supportingText = uiState.note,
        ) {
            if (chips.isNotEmpty()) {
                P2CoreChipRow(items = chips, activeIndex = 0)
            }
            CoreActionRow(
                primaryActionLabel = uiState.secondaryActionLabel ?: "分享二维码",
                onPrimaryAction = { onEvent(ReceiveEvent.SecondaryActionClicked) },
                secondaryActionLabel = uiState.primaryActionLabel,
                onSecondaryAction = { onEvent(ReceiveEvent.PrimaryActionClicked) },
            )
            P2CoreNoteCard(title = "请确认链一致", text = uiState.note)
        }
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
