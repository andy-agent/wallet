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
        P2Card(title = "桥接面板", subtitle = "支持常用主流链之间的稳定币与主资产桥接。") {
            FieldRow("来源链", "TRON")
            Spacer(modifier = Modifier.height(8.dp))
            FieldRow("目标链", "Solana")
            Spacer(modifier = Modifier.height(8.dp))
            FieldRow("资产", "USDT")
            Spacer(modifier = Modifier.height(8.dp))
            FieldRow("数量", "580.00")
            Spacer(modifier = Modifier.height(12.dp))
            KpiRow(
                listOf(
                    "桥接费用" to "$1.9",
                    "预计用时" to "3 min",
                    "到账数量" to "578.1",
                ),
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
