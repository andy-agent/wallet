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
import com.v2ray.ang.composeui.p2extended.model.ChainManagerEvent
import com.v2ray.ang.composeui.p2extended.model.ChainManagerUiState
import com.v2ray.ang.composeui.p2extended.model.chainManagerPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ChainManagerViewModel

@Composable
fun ChainManagerRoute(
    viewModel: ChainManagerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ChainManagerScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ChainManagerEvent.PrimaryActionClicked -> onPrimaryAction()
                ChainManagerEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ChainManagerScreen(
    uiState: ChainManagerUiState,
    onEvent: (ChainManagerEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "Chain Manager",
        title = "链管理",
        subtitle = "为完整多链钱包补齐网络启用、排序与默认链切换。",
        hubLabel = "多链扩展",
        onHubClick = { onEvent(ChainManagerEvent.Refresh) },
        primaryActionLabel = "添加自定义代币",
        onPrimaryAction = { onEvent(ChainManagerEvent.PrimaryActionClicked) },
        secondaryActionLabel = "返回钱包首页",
        onSecondaryAction = { onEvent(ChainManagerEvent.SecondaryActionClicked) },
    ) {
        ChipRow(items = listOf("已启用", "可添加", "测试网"), activeIndex = 0)
        Spacer(modifier = Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            ListRow("Solana", "已启用 · 主链", "Healthy")
            ListRow("TRON", "已启用 · 主链", "Healthy")
            ListRow("Ethereum", "已启用 · EVM", "Healthy")
            ListRow("Base", "已启用 · EVM L2", "Healthy")
            ListRow("BNB Chain", "可添加", "Optional")
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ChainManagerPreview() {
    CryptoVpnTheme {
        ChainManagerScreen(
            uiState = chainManagerPreviewState(),
            onEvent = {},
        )
    }
}
