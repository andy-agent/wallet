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
        P2CoreCard {
            P2CoreCardHeader(
                title = "邀请收益总览",
                subtitle = uiState.summary,
                trailing = uiState.badge,
                trailingColor = Color(0xFFFFF2E7),
            )
            P2CoreMetricGrid(
                items = uiState.metrics.map { it.label to it.value },
                accentIndexes = setOf(1),
            )
        }
        P2CoreCard {
            P2CoreCardHeader(title = "我的邀请码")
            uiState.highlights.forEachIndexed { index, item ->
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    trailingColor = if (index == 0) Color(0xFF2F5BFF) else Color(0xFF66739D),
                    onClick = when (index) {
                        0 -> { { onEvent(InviteCenterEvent.PrimaryActionClicked) } }
                        2 -> { { onBottomNav("invite_share") } }
                        else -> null
                    },
                )
            }
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
