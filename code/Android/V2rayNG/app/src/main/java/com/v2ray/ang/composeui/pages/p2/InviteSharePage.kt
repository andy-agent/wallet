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
    val link = uiState.metrics.firstOrNull { it.label == "链接" }?.value.orEmpty()
    val inviteCode = uiState.metrics.firstOrNull { it.label == "邀请码" }?.value ?: "--"
    val channel = uiState.metrics.firstOrNull { it.label == "渠道" }?.value ?: "分享"
    val shareFocus = rememberCoreLoopingIndex(itemCount = 2, durationMillis = 4200)
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = null,
        activeSection = CoreNavSection.Growth,
        onBottomNav = onBottomNav,
        secureHubLabel = if (shareFocus == 0) "QR" else "LINK",
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(InviteShareEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(InviteShareEvent.SecondaryActionClicked) },
    ) {
        P2CoreHeroValueCard(
            label = "推广邀请码",
            value = inviteCode.ifBlank { "CVPN-2025-GLOW" },
            supportingText = uiState.summary,
            highlight = uiState.badge,
            stats = listOf(
                "分享渠道" to channel,
                "状态" to "可转发",
            ),
        )
        P2CoreQrAddressCard(
            title = "推广二维码",
            subtitle = "扫码后自动带入邀请关系",
            status = "Share Ready",
            address = link,
            addressLabel = "推广链接",
            supportingText = uiState.note,
        ) {
            if (inviteCode.isNotBlank()) {
                P2CoreField(
                    label = "邀请码",
                    value = inviteCode,
                    supportingText = "可单独复制并发送给新用户",
                )
            }
            P2CoreNoteCard(title = "分享提示", text = uiState.note)
        }
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
