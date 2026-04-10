package com.cryptovpn.ui.pages.p1

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p1.model.RegionSelectionEvent
import com.cryptovpn.ui.p1.model.RegionSelectionUiState
import com.cryptovpn.ui.p1.model.regionSelectionPreviewState
import com.cryptovpn.ui.p1.viewmodel.RegionSelectionViewModel

@Composable
fun RegionSelectionRoute(
    viewModel: RegionSelectionViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    RegionSelectionScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                RegionSelectionEvent.PrimaryActionClicked -> onPrimaryAction()
                RegionSelectionEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun RegionSelectionScreen(
    uiState: RegionSelectionUiState,
    onEvent: (RegionSelectionEvent) -> Unit,
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
        currentRoute = "region_selection",
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(RegionSelectionEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(RegionSelectionEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(RegionSelectionEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun RegionSelectionPreview() {
    CryptoVpnTheme {
        RegionSelectionScreen(
            uiState = regionSelectionPreviewState(),
            onEvent = {},
        )
    }
}
