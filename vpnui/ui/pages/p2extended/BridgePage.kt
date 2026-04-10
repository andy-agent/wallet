package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.BridgeEvent
import com.cryptovpn.ui.p2extended.model.BridgeUiState
import com.cryptovpn.ui.p2extended.model.bridgePreviewState
import com.cryptovpn.ui.p2extended.viewmodel.BridgeViewModel

@Composable
fun BridgeRoute(
    viewModel: BridgeViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    BridgeScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                BridgeEvent.PrimaryActionClicked -> onPrimaryAction()
                BridgeEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun BridgeScreen(
    uiState: BridgeUiState,
    onEvent: (BridgeEvent) -> Unit,
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
        currentRoute = "bridge",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(BridgeEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(BridgeEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(BridgeEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun BridgePreview() {
    CryptoVpnTheme {
        BridgeScreen(
            uiState = bridgePreviewState(),
            onEvent = {},
        )
    }
}
