package com.cryptovpn.ui.pages.p0

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p0.model.EmailRegisterEvent
import com.cryptovpn.ui.p0.model.EmailRegisterUiState
import com.cryptovpn.ui.p0.model.emailRegisterPreviewState
import com.cryptovpn.ui.p0.viewmodel.EmailRegisterViewModel

@Composable
fun EmailRegisterRoute(
    viewModel: EmailRegisterViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    EmailRegisterScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                EmailRegisterEvent.PrimaryActionClicked -> onPrimaryAction()
                EmailRegisterEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun EmailRegisterScreen(
    uiState: EmailRegisterUiState,
    onEvent: (EmailRegisterEvent) -> Unit,
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
        currentRoute = "email_register",
        motionProfile = MotionProfile.L2,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(EmailRegisterEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(EmailRegisterEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(EmailRegisterEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun EmailRegisterPreview() {
    CryptoVpnTheme {
        EmailRegisterScreen(
            uiState = emailRegisterPreviewState(),
            onEvent = {},
        )
    }
}
