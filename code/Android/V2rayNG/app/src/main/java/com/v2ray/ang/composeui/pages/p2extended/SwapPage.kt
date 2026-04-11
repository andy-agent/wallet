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
        P2SwapPairCard(
            payToken = "USDT",
            payChain = "TRON",
            payAmount = "580.00",
            receiveToken = "SOL",
            receiveChain = "Solana",
            receiveAmount = "82.60",
            routeDetail = "Jupiter -> Orca 两跳聚合，预计成交价偏差 0.42%",
        )
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "兑换控制", subtitle = "确认滑点与路由后再发起签名。") {
            ChipRow(items = listOf("0.3%", "0.5%", "1.0%"), activeIndex = 1)
            Spacer(modifier = Modifier.height(10.dp))
            KpiRow(
                listOf(
                    "价格影响" to "0.19%",
                    "最小到账" to "82.10 SOL",
                    "网络费" to "0.0012 SOL",
                ),
            )
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
