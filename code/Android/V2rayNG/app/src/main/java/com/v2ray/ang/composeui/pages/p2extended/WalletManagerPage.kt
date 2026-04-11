package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.WalletManagerEvent
import com.v2ray.ang.composeui.p2extended.model.WalletManagerUiState
import com.v2ray.ang.composeui.p2extended.model.walletManagerPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.WalletManagerViewModel

@Composable
fun WalletManagerRoute(
    viewModel: WalletManagerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletManagerScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                WalletManagerEvent.PrimaryActionClicked -> onPrimaryAction()
                WalletManagerEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletManagerScreen(
    uiState: WalletManagerUiState,
    onEvent: (WalletManagerEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(WalletManagerEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(WalletManagerEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(WalletManagerEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletManagerPreview() {
    CryptoVpnTheme {
        WalletManagerScreen(
            uiState = walletManagerPreviewState(),
            onEvent = {},
        )
    }
}
