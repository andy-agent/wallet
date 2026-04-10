package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.SendResultEvent
import com.v2ray.ang.composeui.p2.model.SendResultUiState
import com.v2ray.ang.composeui.p2.model.sendResultPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.SendResultViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun SendResultRoute(
    viewModel: SendResultViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SendResultScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SendResultEvent.PrimaryActionClicked -> onPrimaryAction()
                SendResultEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SendResultScreen(
    uiState: SendResultUiState,
    onEvent: (SendResultEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.secondaryActionLabel ?: "查看交易哈希",
        onPrimaryAction = { onEvent(SendResultEvent.SecondaryActionClicked) },
        secondaryActionLabel = uiState.primaryActionLabel,
        onSecondaryAction = { onEvent(SendResultEvent.PrimaryActionClicked) },
    ) {
        P2CoreMetricGrid(
            items = uiState.metrics.take(4).map { it.label to it.value },
        )
        P2CoreCard {
            P2CoreCardHeader(title = "转账详情")
            uiState.highlights.forEach { item ->
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                )
            }
            P2CoreNoteCard(title = "说明", text = uiState.note)
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SendResultPreview() {
    CryptoVpnTheme {
        SendResultScreen(
            uiState = sendResultPreviewState(),
            onEvent = {},
        )
    }
}
