package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionEvent
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionUiState
import com.v2ray.ang.composeui.p2extended.model.walletConnectSessionPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.WalletConnectSessionViewModel

@Composable
fun WalletConnectSessionRoute(
    viewModel: WalletConnectSessionViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletConnectSessionScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                WalletConnectSessionEvent.PrimaryActionClicked -> onPrimaryAction()
                WalletConnectSessionEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletConnectSessionScreen(
    uiState: WalletConnectSessionUiState,
    onEvent: (WalletConnectSessionEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(WalletConnectSessionEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(WalletConnectSessionEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(WalletConnectSessionEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletConnectSessionPreview() {
    CryptoVpnTheme {
        WalletConnectSessionScreen(
            uiState = walletConnectSessionPreviewState(),
            onEvent = {},
        )
    }
}
