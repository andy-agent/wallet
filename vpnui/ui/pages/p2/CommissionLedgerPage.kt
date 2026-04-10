package com.cryptovpn.ui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2.model.CommissionLedgerEvent
import com.cryptovpn.ui.p2.model.CommissionLedgerUiState
import com.cryptovpn.ui.p2.model.commissionLedgerPreviewState
import com.cryptovpn.ui.p2.viewmodel.CommissionLedgerViewModel

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
        currentRoute = "commission_ledger",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(CommissionLedgerEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(CommissionLedgerEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(CommissionLedgerEvent.SecondaryActionClicked)
        },
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
