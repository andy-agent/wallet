package com.cryptovpn.ui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2.model.LegalDocumentsEvent
import com.cryptovpn.ui.p2.model.LegalDocumentsUiState
import com.cryptovpn.ui.p2.model.legalDocumentsPreviewState
import com.cryptovpn.ui.p2.viewmodel.LegalDocumentsViewModel

@Composable
fun LegalDocumentsRoute(
    viewModel: LegalDocumentsViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LegalDocumentsScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                LegalDocumentsEvent.PrimaryActionClicked -> onPrimaryAction()
                LegalDocumentsEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun LegalDocumentsScreen(
    uiState: LegalDocumentsUiState,
    onEvent: (LegalDocumentsEvent) -> Unit,
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
        showBottomBar = true,
        currentRoute = "profile",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(LegalDocumentsEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(LegalDocumentsEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(LegalDocumentsEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun LegalDocumentsPreview() {
    CryptoVpnTheme {
        LegalDocumentsScreen(
            uiState = legalDocumentsPreviewState(),
            onEvent = {},
        )
    }
}
