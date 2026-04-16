package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionEvent
import com.v2ray.ang.composeui.p2extended.model.WalletConnectSessionUiState
import com.v2ray.ang.composeui.p2extended.model.walletConnectSessionPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.WalletConnectSessionViewModel

@Composable
fun WalletConnectSessionRoute(
    viewModel: WalletConnectSessionViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WalletConnectSessionScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                WalletConnectSessionEvent.PrimaryActionClicked -> onPrimaryAction()
                WalletConnectSessionEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WalletConnectSessionScreen(
    uiState: WalletConnectSessionUiState,
    onEvent: (WalletConnectSessionEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val sessionCards = uiState.highlights
        .mapNotNull { item ->
            val title = item.title.takeUnless { it.isBlank() }
            val subtitle = item.subtitle.takeUnless { it.isBlank() }
            val network = item.badge.takeUnless { it.isBlank() }
            if (title == null || subtitle == null || network == null) null else Triple(title, subtitle, network)
        }
        .take(4)
    val displayCards = sessionCards.ifEmpty {
        listOf(Triple("会话列表待接入", "当前未返回 WalletConnect 会话。", "待接入"))
    }
    P2ExtendedPageScaffold(
        kicker = "",
        title = "连接会话",
        subtitle = "",
        currentRoute = "wallet_connect_session",
        onBottomNav = onBottomNav,
        hubLabel = "",
        onHubClick = { onEvent(WalletConnectSessionEvent.Refresh) },
        primaryActionLabel = "批量审计会话",
        onPrimaryAction = { onEvent(WalletConnectSessionEvent.PrimaryActionClicked) },
        secondaryActionLabel = "断开全部高风险",
        onSecondaryAction = { onEvent(WalletConnectSessionEvent.SecondaryActionClicked) },
    ) {
        P2SearchShell(
            placeholder = "搜索 DApp / 会话地址",
            quickHint = "",
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            displayCards.forEach { (title, subtitle, network) ->
                val riskFlag = subtitle.contains("高风险") || subtitle.contains("待接入") || subtitle.contains("阻塞")
                P2SessionAppCard(
                    title = title,
                    subtitle = subtitle,
                    network = network,
                    riskFlag = riskFlag,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WalletConnectSessionPreview() {
    CryptoVpnTheme {
        WalletConnectSessionScreen(
            uiState = walletConnectSessionPreviewState(),
            onEvent = {},
        )
    }
}
