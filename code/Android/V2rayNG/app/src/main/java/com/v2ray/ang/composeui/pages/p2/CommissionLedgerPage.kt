package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.CommissionLedgerEvent
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState
import com.v2ray.ang.composeui.p2.model.commissionLedgerPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.CommissionLedgerViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun CommissionLedgerRoute(
    viewModel: CommissionLedgerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    CommissionLedgerScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                CommissionLedgerEvent.PrimaryActionClicked -> onPrimaryAction()
                CommissionLedgerEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun CommissionLedgerScreen(
    uiState: CommissionLedgerUiState,
    onEvent: (CommissionLedgerEvent) -> Unit,
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
        currentRoute = "invite_center",
        onBottomNav = onBottomNav,
        onPrimaryAction = { onEvent(CommissionLedgerEvent.PrimaryActionClicked) },
        onSecondaryAction = { onEvent(CommissionLedgerEvent.SecondaryActionClicked) },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CommissionLedgerPreview() {
    CryptoVpnTheme {
        CommissionLedgerScreen(
            uiState = commissionLedgerPreviewState(),
            onEvent = {},
        )
    }
}
