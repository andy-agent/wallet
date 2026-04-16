package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.components.feature.FeaturePageTemplate
import com.v2ray.ang.composeui.effects.MotionProfile
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderEvent
import com.v2ray.ang.composeui.p2extended.model.ExpiryReminderUiState
import com.v2ray.ang.composeui.p2extended.model.expiryReminderPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ExpiryReminderViewModel

@Composable
fun ExpiryReminderRoute(
    viewModel: ExpiryReminderViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ExpiryReminderScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ExpiryReminderEvent.PrimaryActionClicked -> onPrimaryAction()
                ExpiryReminderEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ExpiryReminderScreen(
    uiState: ExpiryReminderUiState,
    onEvent: (ExpiryReminderEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    FeaturePageTemplate(
        title = uiState.title,
        subtitle = "",
        badge = "",
        summary = "",
        heroAccent = uiState.heroAccent,
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = emptyList(),
        checklist = emptyList(),
        note = "",
        primaryActionLabel = uiState.primaryActionLabel,
        secondaryActionLabel = uiState.secondaryActionLabel,
        showBottomBar = false,
        currentRoute = "expiry_reminder",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(ExpiryReminderEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(ExpiryReminderEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(ExpiryReminderEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ExpiryReminderPreview() {
    CryptoVpnTheme {
        ExpiryReminderScreen(
            uiState = expiryReminderPreviewState(),
            onEvent = {},
        )
    }
}
