package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.BridgeEvent
import com.v2ray.ang.composeui.p2extended.model.BridgeUiState
import com.v2ray.ang.composeui.p2extended.model.bridgePreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.BridgeViewModel

@Composable
fun BridgeRoute(
    viewModel: BridgeViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    BridgeScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                BridgeEvent.PrimaryActionClicked -> onPrimaryAction()
                BridgeEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun BridgeScreen(
    uiState: BridgeUiState,
    onEvent: (BridgeEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "BRIDGE",
        title = "跨链桥接",
        subtitle = "补齐多链钱包核心能力：资产跨链迁移与到账追踪。",
        hubLabel = "BRIDGE",
        onHubClick = { onEvent(BridgeEvent.Refresh) },
        primaryActionLabel = "预览桥接并继续",
        onPrimaryAction = { onEvent(BridgeEvent.PrimaryActionClicked) },
        secondaryActionLabel = "返回 Swap",
        onSecondaryAction = { onEvent(BridgeEvent.SecondaryActionClicked) },
    ) {
        P2BridgeFlowCard(
            sourceChain = "TRON",
            targetChain = "Solana",
            asset = "USDT",
            amount = "580.00",
            eta = "3 min",
            fee = "$1.90",
        )
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "桥接参数", subtitle = "执行前确认到账链与最小到账数量。") {
            KpiRow(
                listOf(
                    "最小到账" to "578.10",
                    "桥接路由" to "Stargate",
                    "状态" to "可执行",
                ),
            )
            Spacer(modifier = Modifier.height(10.dp))
            P2InlineWarningCard(
                title = "跨链提醒",
                text = "桥接提交后不可撤销，请确认目标地址网络一致。",
            )
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun BridgePreview() {
    CryptoVpnTheme {
        BridgeScreen(
            uiState = bridgePreviewState(),
            onEvent = {},
        )
    }
}
