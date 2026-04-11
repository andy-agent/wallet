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
    val truthState = buildP2TruthState(
        badge = uiState.badge,
        summary = uiState.summary,
        note = uiState.note,
        checklist = uiState.checklist,
        isLoading = uiState.isLoading,
        errorMessage = uiState.errorMessage,
        emptyMessage = uiState.emptyMessage,
        blockerTitle = uiState.blockerTitle,
        blockerMessage = uiState.blockerMessage,
        primaryActionLabel = uiState.primaryActionLabel,
        secondaryActionLabel = uiState.secondaryActionLabel,
    )
    P2TruthFeaturePage(
        title = uiState.title,
        subtitle = uiState.subtitle,
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        truthState = truthState,
        currentRoute = "wallet_home",
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(ReceiveEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = { onEvent(ReceiveEvent.PrimaryActionClicked) },
        onSecondaryAction = { onEvent(ReceiveEvent.SecondaryActionClicked) },
    )
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
