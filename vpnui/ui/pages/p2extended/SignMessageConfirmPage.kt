package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.SignMessageConfirmEvent
import com.cryptovpn.ui.p2extended.model.SignMessageConfirmUiState
import com.cryptovpn.ui.p2extended.model.signMessageConfirmPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.SignMessageConfirmViewModel

@Composable
fun SignMessageConfirmRoute(
    viewModel: SignMessageConfirmViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SignMessageConfirmScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SignMessageConfirmEvent.PrimaryActionClicked -> onPrimaryAction()
                SignMessageConfirmEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SignMessageConfirmScreen(
    uiState: SignMessageConfirmUiState,
    onEvent: (SignMessageConfirmEvent) -> Unit,
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
        currentRoute = "sign_message_confirm",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(SignMessageConfirmEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(SignMessageConfirmEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(SignMessageConfirmEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SignMessageConfirmPreview() {
    CryptoVpnTheme {
        SignMessageConfirmScreen(
            uiState = signMessageConfirmPreviewState(),
            onEvent = {},
        )
    }
}
