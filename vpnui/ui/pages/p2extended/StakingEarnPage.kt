package com.cryptovpn.ui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.cryptovpn.ui.components.feature.FeaturePageTemplate
import com.cryptovpn.ui.effects.MotionProfile
import com.cryptovpn.ui.theme.CryptoVpnTheme
import com.cryptovpn.ui.p2extended.model.StakingEarnEvent
import com.cryptovpn.ui.p2extended.model.StakingEarnUiState
import com.cryptovpn.ui.p2extended.model.stakingEarnPreviewState
import com.cryptovpn.ui.p2extended.viewmodel.StakingEarnViewModel

@Composable
fun StakingEarnRoute(
    viewModel: StakingEarnViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    StakingEarnScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                StakingEarnEvent.PrimaryActionClicked -> onPrimaryAction()
                StakingEarnEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun StakingEarnScreen(
    uiState: StakingEarnUiState,
    onEvent: (StakingEarnEvent) -> Unit,
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
        currentRoute = "staking_earn",
        motionProfile = MotionProfile.L1,
        onBottomNav = onBottomNav,
        onFieldChanged = { key, value ->
            onEvent(StakingEarnEvent.FieldChanged(key = key, value = value))
        },
        onPrimaryAction = {
            onEvent(StakingEarnEvent.PrimaryActionClicked)
        },
        onSecondaryAction = {
            onEvent(StakingEarnEvent.SecondaryActionClicked)
        },
    )
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun StakingEarnPreview() {
    CryptoVpnTheme {
        StakingEarnScreen(
            uiState = stakingEarnPreviewState(),
            onEvent = {},
        )
    }
}
