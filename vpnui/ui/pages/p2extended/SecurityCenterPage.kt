package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.SecurityCenterEvent
import com.cryptovpn.ui.p2extended.model.SecurityCenterUiState
import com.cryptovpn.ui.p2extended.model.securityCenterPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.SecurityCenterViewModel

@Composable
fun SecurityCenterRoute(
    viewModel: SecurityCenterViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SecurityCenterScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SecurityCenterEvent.PrimaryActionClicked -> onPrimaryAction()
                SecurityCenterEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SecurityCenterScreen(
    uiState: SecurityCenterUiState,
    onEvent: (SecurityCenterEvent) -> Unit,
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
        currentRoute = "security_center",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(SecurityCenterEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(SecurityCenterEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(SecurityCenterEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SecurityCenterPreview() {
    CryptoVpnTheme {
        SecurityCenterScreen(
            uiState = securityCenterPreviewState(),
            onEvent = {},
        )
    }
}
