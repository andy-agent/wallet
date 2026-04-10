package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.WalletManagerEvent
import com.cryptovpn.ui.p2extended.model.WalletManagerUiState
import com.cryptovpn.ui.p2extended.model.walletManagerPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.WalletManagerViewModel

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
    FeaturePageTemplate(
        title = uiState.title,
        subtitle = uiState.subtitle,
        badge = uiState.badge,
        summary = uiState.summary,
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
        primaryActionLabel = uiState.primaryActionLabel,
        secondaryActionLabel = uiState.secondaryActionLabel,
        showBottomBar = false,
        currentRoute = "wallet_manager",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(WalletManagerEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(WalletManagerEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(WalletManagerEvent.SecondaryActionClicked)
        },
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
