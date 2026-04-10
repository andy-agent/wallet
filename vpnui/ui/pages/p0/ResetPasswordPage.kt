package com.cryptovpn.ui.pages.p0

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p0.model.ResetPasswordEvent
import com.cryptovpn.ui.p0.model.ResetPasswordUiState
import com.cryptovpn.ui.p0.model.resetPasswordPreviewState
import com.cryptovpn.ui.p0.viewmodel.ResetPasswordViewModel

@Composable
fun ResetPasswordRoute(
    viewModel: ResetPasswordViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ResetPasswordScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ResetPasswordEvent.PrimaryActionClicked -> onPrimaryAction()
                ResetPasswordEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ResetPasswordScreen(
    uiState: ResetPasswordUiState,
    onEvent: (ResetPasswordEvent) -> Unit,
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
        currentRoute = "reset_password",
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(ResetPasswordEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(ResetPasswordEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(ResetPasswordEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ResetPasswordPreview() {
    CryptoVpnTheme {
        ResetPasswordScreen(
            uiState = resetPasswordPreviewState(),
            onEvent = {},
        )
    }
}
