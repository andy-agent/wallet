package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.SubscriptionDetailEvent
import com.cryptovpn.ui.p2extended.model.SubscriptionDetailUiState
import com.cryptovpn.ui.p2extended.model.subscriptionDetailPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.SubscriptionDetailViewModel

@Composable
fun SubscriptionDetailRoute(
    viewModel: SubscriptionDetailViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SubscriptionDetailScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SubscriptionDetailEvent.PrimaryActionClicked -> onPrimaryAction()
                SubscriptionDetailEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SubscriptionDetailScreen(
    uiState: SubscriptionDetailUiState,
    onEvent: (SubscriptionDetailEvent) -> Unit,
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
        currentRoute = "subscription_detail",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(SubscriptionDetailEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(SubscriptionDetailEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(SubscriptionDetailEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SubscriptionDetailPreview() {
    CryptoVpnTheme {
        SubscriptionDetailScreen(
            uiState = subscriptionDetailPreviewState(),
            onEvent = {},
        )
    }
}
