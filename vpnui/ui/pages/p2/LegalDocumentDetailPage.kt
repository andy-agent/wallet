package com.cryptovpn.ui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2.model.LegalDocumentDetailEvent
import com.cryptovpn.ui.p2.model.LegalDocumentDetailUiState
import com.cryptovpn.ui.p2.model.legalDocumentDetailPreviewState
import com.cryptovpn.ui.p2.viewmodel.LegalDocumentDetailViewModel

@Composable
fun LegalDocumentDetailRoute(
    viewModel: LegalDocumentDetailViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LegalDocumentDetailScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                LegalDocumentDetailEvent.PrimaryActionClicked -> onPrimaryAction()
                LegalDocumentDetailEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun LegalDocumentDetailScreen(
    uiState: LegalDocumentDetailUiState,
    onEvent: (LegalDocumentDetailEvent) -> Unit,
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
        currentRoute = "legal_document_detail",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(LegalDocumentDetailEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(LegalDocumentDetailEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(LegalDocumentDetailEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun LegalDocumentDetailPreview() {
    CryptoVpnTheme {
        LegalDocumentDetailScreen(
            uiState = legalDocumentDetailPreviewState(),
            onEvent = {},
        )
    }
}
