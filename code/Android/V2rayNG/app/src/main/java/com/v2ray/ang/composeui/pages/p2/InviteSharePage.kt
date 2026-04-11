package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.InviteShareEvent
import com.v2ray.ang.composeui.p2.model.InviteShareUiState
import com.v2ray.ang.composeui.p2.model.inviteSharePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.InviteShareViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun InviteShareRoute(
    viewModel: InviteShareViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    InviteShareScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                InviteShareEvent.PrimaryActionClicked -> onPrimaryAction()
                InviteShareEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun InviteShareScreen(
    uiState: InviteShareUiState,
    onEvent: (InviteShareEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val primaryMetric = uiState.metrics.firstOrNull()
    val link = uiState.highlights.firstOrNull()?.subtitle ?: primaryMetric?.value ?: ""
    val inviteCode = uiState.metrics.getOrNull(1)?.value ?: "--"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = null,
        activeSection = CoreNavSection.Growth,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(InviteShareEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(InviteShareEvent.SecondaryActionClicked) },
    ) {
        P2CoreQrAddressCard(
            title = primaryMetric?.label ?: "分享信息",
            subtitle = uiState.summary,
            status = "可复制",
            address = link,
        ) {
            P2CoreActionValueRow(
                label = "邀请码",
                value = inviteCode,
                actionLabel = uiState.primaryActionLabel,
                onAction = { onEvent(InviteShareEvent.PrimaryActionClicked) },
            )
        }
        P2CoreAddressModule(
            title = "邀请状态",
            value = uiState.highlights.firstOrNull()?.title ?: inviteCode,
            supportingText = "分享链接可追踪转化与佣金",
            status = uiState.highlights.firstOrNull()?.trailing ?: "跟踪中",
            primaryActionLabel = uiState.primaryActionLabel,
            onPrimaryAction = { onEvent(InviteShareEvent.PrimaryActionClicked) },
            secondaryActionLabel = uiState.secondaryActionLabel,
            onSecondaryAction = { onEvent(InviteShareEvent.SecondaryActionClicked) },
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun InviteSharePreview() {
    CryptoVpnTheme {
        InviteShareScreen(
            uiState = inviteSharePreviewState(),
            onEvent = {},
        )
    }
}
