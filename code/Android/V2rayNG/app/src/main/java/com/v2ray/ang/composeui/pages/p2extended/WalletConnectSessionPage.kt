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
    P2ExtendedPageScaffold(
        kicker = "WALLETCONNECT",
        title = "连接会话",
        subtitle = "管理与 DApp的已连接会话、权限范围与自动断开。",
        hubLabel = "6 会话",
        onHubClick = { onEvent(WalletConnectSessionEvent.Refresh) },
        primaryActionLabel = "批量审计会话",
        onPrimaryAction = { onEvent(WalletConnectSessionEvent.PrimaryActionClicked) },
        secondaryActionLabel = "断开全部高风险",
        onSecondaryAction = { onEvent(WalletConnectSessionEvent.SecondaryActionClicked) },
    ) {
        P2SearchShell(
            placeholder = "搜索 DApp / 会话地址",
            quickHint = "按链与风险等级筛选，避免过期授权长期保留。",
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            P2SessionAppCard(
                title = "Jupiter",
                subtitle = "读取余额 / 发起兑换",
                network = "Solana",
            )
            P2SessionAppCard(
                title = "Sunswap",
                subtitle = "读取地址 / 发起授权",
                network = "TRON",
            )
            P2SessionAppCard(
                title = "Aave",
                subtitle = "读取资产 / 发起存款",
                network = "Ethereum",
            )
            P2SessionAppCard(
                title = "Unknown DEX",
                subtitle = "合约写入 / 签名请求",
                network = "Polygon",
                riskFlag = true,
            )
        }
        Spacer(modifier = Modifier.height(14.dp))
        P2InlineWarningCard(
            title = "会话安全",
            text = "高风险域名会自动标红并在签名前再次确认。",
        )
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
