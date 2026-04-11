package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.AutoConnectRulesEvent
import com.v2ray.ang.composeui.p2extended.model.AutoConnectRulesUiState
import com.v2ray.ang.composeui.p2extended.model.autoConnectRulesPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.AutoConnectRulesViewModel

@Composable
fun AutoConnectRulesRoute(
    viewModel: AutoConnectRulesViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AutoConnectRulesScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                AutoConnectRulesEvent.PrimaryActionClicked -> onPrimaryAction()
                AutoConnectRulesEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun AutoConnectRulesScreen(
    uiState: AutoConnectRulesUiState,
    onEvent: (AutoConnectRulesEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(AutoConnectRulesEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(AutoConnectRulesEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(AutoConnectRulesEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AutoConnectRulesPreview() {
    CryptoVpnTheme {
        AutoConnectRulesScreen(
            uiState = autoConnectRulesPreviewState(),
            onEvent = {},
        )
    }
}
