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
import com.v2ray.ang.composeui.p2extended.model.SwapEvent
import com.v2ray.ang.composeui.p2extended.model.SwapUiState
import com.v2ray.ang.composeui.p2extended.model.swapPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.SwapViewModel

@Composable
fun SwapRoute(
    viewModel: SwapViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SwapScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SwapEvent.PrimaryActionClicked -> onPrimaryAction()
                SwapEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SwapScreen(
    uiState: SwapUiState,
    onEvent: (SwapEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "Swap",
        title = "币币兑换",
        subtitle = "补齐钱包内兑换能力，支持同链资产快速换币。",
        hubLabel = "低滑点",
        onHubClick = { onEvent(SwapEvent.Refresh) },
        primaryActionLabel = "预览兑换并签名",
        onPrimaryAction = { onEvent(SwapEvent.PrimaryActionClicked) },
        secondaryActionLabel = "预览兑换并继续",
        onSecondaryAction = { onEvent(SwapEvent.SecondaryActionClicked) },
    ) {
        P2Card(title = "兑换面板", subtitle = "支持 SOL / USDT / ETH / TRX 等常见资产。") {
            SwapAssetBlock(
                label = "支付",
                symbol = "USDT",
                chain = "TRON",
                amount = "580.00",
            )
            Spacer(modifier = Modifier.height(8.dp))
            SwapArrowConnector()
            Spacer(modifier = Modifier.height(8.dp))
            SwapAssetBlock(
                label = "获得",
                symbol = "SOL",
                chain = "Solana",
                amount = "82.60",
                emphasized = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
            SwapDirectionPill(label = "TRON -> Solana · Route B")
            Spacer(modifier = Modifier.height(10.dp))
            DetailRow("滑点", "0.50%")
            Spacer(modifier = Modifier.height(6.dp))
            DetailRow("路由", "2 hops")
            Spacer(modifier = Modifier.height(6.dp))
            DetailRow("预计到账", "82.10 SOL", emphasized = true)
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SwapPreview() {
    CryptoVpnTheme {
        SwapScreen(
            uiState = swapPreviewState(),
            onEvent = {},
        )
    }
}
