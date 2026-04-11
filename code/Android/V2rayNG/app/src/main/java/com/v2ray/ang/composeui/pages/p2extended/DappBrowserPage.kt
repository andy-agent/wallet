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
import com.v2ray.ang.composeui.p2extended.model.DappBrowserEvent
import com.v2ray.ang.composeui.p2extended.model.DappBrowserUiState
import com.v2ray.ang.composeui.p2extended.model.dappBrowserPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.DappBrowserViewModel

@Composable
fun DappBrowserRoute(
    viewModel: DappBrowserViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    DappBrowserScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                DappBrowserEvent.PrimaryActionClicked -> onPrimaryAction()
                DappBrowserEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun DappBrowserScreen(
    uiState: DappBrowserUiState,
    onEvent: (DappBrowserEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "DApp Browser",
        title = "DApp 浏览器",
        subtitle = "补齐链上应用入口，并统一签名与风险提示体验。",
        hubLabel = "内置浏览器",
        onHubClick = { onEvent(DappBrowserEvent.Refresh) },
        primaryActionLabel = "访问",
        onPrimaryAction = { onEvent(DappBrowserEvent.PrimaryActionClicked) },
        secondaryActionLabel = "返回",
        onSecondaryAction = { onEvent(DappBrowserEvent.SecondaryActionClicked) },
    ) {
        SearchShell(
            placeholder = "输入 URL / 搜索 DApp / 输入 ENS",
            hint = "支持 ENS / Lens / Solana Name Service",
        )
        Spacer(modifier = Modifier.height(12.dp))
        ChipRow(items = listOf("精选", "DeFi", "支付", "NFT", "工具"), activeIndex = 0)
        Spacer(modifier = Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            DappBadgeRow("Jupiter", "Solana 聚合兑换", "92K users", verified = true)
            DappBadgeRow("Sunswap", "TRON 稳定币兑换", "31K users", verified = true)
            DappBadgeRow("Aave", "借贷与收益", "120K users", verified = true)
            DappBadgeRow("OpenOcean", "跨链路由", "58K users", verified = false)
            DappBadgeRow("Magic Eden", "NFT 市场", "63K users", verified = true)
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun DappBrowserPreview() {
    CryptoVpnTheme {
        DappBrowserScreen(
            uiState = dappBrowserPreviewState(),
            onEvent = {},
        )
    }
}
