package com.cryptovpn.ui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2.model.SendResultEvent
import com.cryptovpn.ui.p2.model.SendResultUiState
import com.cryptovpn.ui.p2.model.sendResultPreviewState
import com.cryptovpn.ui.p2.viewmodel.SendResultViewModel

@Composable
fun SendResultRoute(
    viewModel: SendResultViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SendResultScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SendResultEvent.PrimaryActionClicked -> onPrimaryAction()
                SendResultEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SendResultScreen(
    uiState: SendResultUiState,
    onEvent: (SendResultEvent) -> Unit,
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
        currentRoute = "send_result",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(SendResultEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(SendResultEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(SendResultEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SendResultPreview() {
    CryptoVpnTheme {
        SendResultScreen(
            uiState = sendResultPreviewState(),
            onEvent = {},
        )
    }
}
