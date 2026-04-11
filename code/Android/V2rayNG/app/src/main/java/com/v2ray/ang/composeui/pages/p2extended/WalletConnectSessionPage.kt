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
    ) {
        SearchShell(
            placeholder = "搜索会话 / 域名 / 钱包地址",
            hint = "高风险域名会在列表内标橙提醒",
        )
        Spacer(modifier = Modifier.height(12.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            P2Card(title = "Jupiter", subtitle = "读取余额 / 发起兑换 · 当前网络：Solana") {
                DetailRow("会话时长", "2h 14m")
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("最近请求", "3 min ago")
                Spacer(modifier = Modifier.height(10.dp))
                DisconnectPill()
            }
            P2Card(title = "Sunswap", subtitle = "读取地址 / 发起授权 · 当前网络：TRON") {
                DetailRow("会话时长", "1h 42m")
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("最近请求", "9 min ago")
                Spacer(modifier = Modifier.height(10.dp))
                DisconnectPill()
            }
            P2Card(title = "Aave", subtitle = "读取资产 / 发起存款 · 当前网络：Ethereum") {
                DetailRow("会话时长", "34m")
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("最近请求", "1 min ago")
                Spacer(modifier = Modifier.height(10.dp))
                DisconnectPill()
            }
            P2Card(title = "Magic Eden", subtitle = "读取 NFT / 签名 · 当前网络：Solana") {
                DetailRow("会话时长", "5h 02m")
                Spacer(modifier = Modifier.height(8.dp))
                DetailRow("最近请求", "19 min ago")
                Spacer(modifier = Modifier.height(10.dp))
                DisconnectPill()
            }
        }
        Spacer(modifier = Modifier.height(14.dp))
        NoteCard(title = "会话安全", text = "高风险域名会自动标红并在签名前再次确认")
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
