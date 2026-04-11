package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.StakingEarnEvent
import com.v2ray.ang.composeui.p2extended.model.StakingEarnUiState
import com.v2ray.ang.composeui.p2extended.model.stakingEarnPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.StakingEarnViewModel

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
    P2ExtendedFeatureTemplate(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        hubLabel = uiState.badge,
        onHubClick = { onEvent(StakingEarnEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(StakingEarnEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(StakingEarnEvent.SecondaryActionClicked) },
        metrics = uiState.metrics,
        fields = uiState.fields,
        highlights = uiState.highlights,
        checklist = uiState.checklist,
        note = uiState.note,
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
