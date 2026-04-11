package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.CreateWalletEvent
import com.v2ray.ang.composeui.p2extended.model.CreateWalletUiState
import com.v2ray.ang.composeui.p2extended.model.createWalletPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.CreateWalletViewModel

@Composable
fun CreateWalletRoute(
    viewModel: CreateWalletViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    CreateWalletScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                CreateWalletEvent.PrimaryActionClicked -> onPrimaryAction()
                CreateWalletEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun CreateWalletScreen(
    uiState: CreateWalletUiState,
    onEvent: (CreateWalletEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(CreateWalletEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(CreateWalletEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(CreateWalletEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CreateWalletPreview() {
    CryptoVpnTheme {
        CreateWalletScreen(
            uiState = createWalletPreviewState(),
            onEvent = {},
        )
    }
}
