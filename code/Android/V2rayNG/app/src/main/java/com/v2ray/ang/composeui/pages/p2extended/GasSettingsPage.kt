package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.GasSettingsEvent
import com.v2ray.ang.composeui.p2extended.model.GasSettingsUiState
import com.v2ray.ang.composeui.p2extended.model.gasSettingsPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.GasSettingsViewModel

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
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(GasSettingsEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(GasSettingsEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(GasSettingsEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
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
