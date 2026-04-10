package com.cryptovpn.ui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2.model.SendEvent
import com.cryptovpn.ui.p2.model.SendUiState
import com.cryptovpn.ui.p2.model.sendPreviewState
import com.cryptovpn.ui.p2.viewmodel.SendViewModel

@Composable
fun SendRoute(
    viewModel: SendViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SendScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SendEvent.PrimaryActionClicked -> onPrimaryAction()
                SendEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SendScreen(
    uiState: SendUiState,
    onEvent: (SendEvent) -> Unit,
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
        currentRoute = "send",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(SendEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(SendEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(SendEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SendPreview() {
    CryptoVpnTheme {
        SendScreen(
            uiState = sendPreviewState(),
            onEvent = {},
        )
    }
}
