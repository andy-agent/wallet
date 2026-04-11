package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.RiskAuthorizationsEvent
import com.v2ray.ang.composeui.p2extended.model.RiskAuthorizationsUiState
import com.v2ray.ang.composeui.p2extended.model.riskAuthorizationsPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.RiskAuthorizationsViewModel

@Composable
fun RiskAuthorizationsRoute(
    viewModel: RiskAuthorizationsViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    RiskAuthorizationsScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                RiskAuthorizationsEvent.PrimaryActionClicked -> onPrimaryAction()
                RiskAuthorizationsEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun RiskAuthorizationsScreen(
    uiState: RiskAuthorizationsUiState,
    onEvent: (RiskAuthorizationsEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(RiskAuthorizationsEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(RiskAuthorizationsEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(RiskAuthorizationsEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun RiskAuthorizationsPreview() {
    CryptoVpnTheme {
        RiskAuthorizationsScreen(
            uiState = riskAuthorizationsPreviewState(),
            onEvent = {},
        )
    }
}
