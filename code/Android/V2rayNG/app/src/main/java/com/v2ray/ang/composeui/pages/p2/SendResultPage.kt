package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.SendResultEvent
import com.v2ray.ang.composeui.p2.model.SendResultUiState
import com.v2ray.ang.composeui.p2.model.sendResultPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.SendResultViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun SendResultRoute(
    viewModel: SendResultViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SendResultScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SendResultEvent.PrimaryActionClicked -> onPrimaryAction()
                SendResultEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SendResultScreen(
    uiState: SendResultUiState,
    onEvent: (SendResultEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val amount = uiState.metrics.firstOrNull()?.value ?: "--"
    val txHash = uiState.highlights.getOrNull(1)?.subtitle ?: "--"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.secondaryActionLabel ?: "查看交易哈希",
        onPrimaryAction = { onEvent(SendResultEvent.SecondaryActionClicked) },
        secondaryActionLabel = uiState.primaryActionLabel,
        onSecondaryAction = { onEvent(SendResultEvent.PrimaryActionClicked) },
    ) {
        uiState.blockerTitle?.let { title ->
            P2CoreNoteCard(title = title, text = uiState.blockerMessage ?: uiState.note)
        }
        P2CoreHeroValueCard(
            label = "到账金额",
            value = amount,
            supportingText = uiState.summary,
            highlight = uiState.badge,
            stats = uiState.metrics.drop(1).take(2).map { it.label to it.value },
        )
        P2CoreAddressModule(
            title = "交易哈希",
            value = txHash,
            supportingText = "可复制哈希到链上浏览器追踪确认进度",
            status = uiState.badge,
        )
        P2CoreActionValueRow(
            label = "交易状态",
            value = uiState.highlights.firstOrNull()?.title ?: "链上确认中",
            actionLabel = uiState.secondaryActionLabel,
            onAction = uiState.secondaryActionLabel?.let { { onEvent(SendResultEvent.SecondaryActionClicked) } },
            valueColor = Color(0xFF1BBF93),
        )
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SendResultPreview() {
    CryptoVpnTheme {
        SendResultScreen(
            uiState = sendResultPreviewState(),
            onEvent = {},
        )
    }
}
