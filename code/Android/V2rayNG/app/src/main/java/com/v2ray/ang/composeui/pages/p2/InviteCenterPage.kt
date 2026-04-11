package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.InviteCenterEvent
import com.v2ray.ang.composeui.p2.model.InviteCenterUiState
import com.v2ray.ang.composeui.p2.model.inviteCenterPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.InviteCenterViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun InviteCenterRoute(
    viewModel: InviteCenterViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    InviteCenterScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                InviteCenterEvent.PrimaryActionClicked -> onPrimaryAction()
                InviteCenterEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun InviteCenterScreen(
    uiState: InviteCenterUiState,
    onEvent: (InviteCenterEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.note,
        badge = uiState.badge,
        activeSection = CoreNavSection.Growth,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(InviteCenterEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onBottomNav("invite_share") },
    ) {
        P2CoreHeroValueCard(
            label = "邀请收益总览",
            value = uiState.metrics.firstOrNull()?.value ?: "--",
            supportingText = uiState.summary,
            highlight = uiState.badge,
            highlightColor = Color(0x26FFF5E7),
            stats = uiState.metrics.drop(1).take(2).map { it.label to it.value },
        )
        P2CoreAddressModule(
            title = "我的邀请码",
            value = uiState.highlights.firstOrNull()?.title ?: "--",
            supportingText = uiState.highlights.firstOrNull()?.subtitle ?: uiState.note,
            status = uiState.highlights.firstOrNull()?.trailing,
            primaryActionLabel = uiState.primaryActionLabel,
            onPrimaryAction = { onEvent(InviteCenterEvent.PrimaryActionClicked) },
            secondaryActionLabel = uiState.secondaryActionLabel,
            onSecondaryAction = { onBottomNav("invite_share") },
        )
        P2CoreCard {
            P2CoreCardHeader(title = "增长指标", trailing = "实时同步", trailingColor = Color(0xFFEAF6FF))
            P2CoreMetricGrid(
                items = uiState.metrics.map { it.label to it.value },
                accentIndexes = setOf(1),
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun InviteCenterPreview() {
    CryptoVpnTheme {
        InviteCenterScreen(
            uiState = inviteCenterPreviewState(),
            onEvent = {},
        )
    }
}
