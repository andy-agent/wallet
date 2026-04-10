package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.GasSettingsEvent
import com.cryptovpn.ui.p2extended.model.GasSettingsUiState
import com.cryptovpn.ui.p2extended.model.gasSettingsPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.GasSettingsViewModel

@Composable
fun GasSettingsRoute(
    viewModel: GasSettingsViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    GasSettingsScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                GasSettingsEvent.PrimaryActionClicked -> onPrimaryAction()
                GasSettingsEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun GasSettingsScreen(
    uiState: GasSettingsUiState,
    onEvent: (GasSettingsEvent) -> Unit,
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
        currentRoute = "gas_settings",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(GasSettingsEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(GasSettingsEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(GasSettingsEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun GasSettingsPreview() {
    CryptoVpnTheme {
        GasSettingsScreen(
            uiState = gasSettingsPreviewState(),
            onEvent = {},
        )
    }
}
