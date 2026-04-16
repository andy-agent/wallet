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
    val categoryFocus = rememberLoopingIndex(itemCount = 5, durationMillis = 4600)
    val sessionFocus = rememberLoopingIndex(itemCount = 4, durationMillis = 5600)
    val browserMetrics = uiState.metrics.take(3).map { it.label to it.value }
    val metricFocus = if (browserMetrics.isNotEmpty()) categoryFocus % browserMetrics.size else -1
    val entryHint = uiState.fields.firstOrNull()?.value ?: "待接入入口"
    val sessionCards = uiState.highlights
        .mapNotNull { item ->
            val title = item.title.takeUnless { it.isBlank() }
            val subtitle = item.subtitle.takeUnless { it.isBlank() }
            val network = item.badge.takeUnless { it.isBlank() }
            if (title == null || subtitle == null || network == null) null else Triple(title, subtitle, network)
        }
        .take(4)
    val displayCards = sessionCards.ifEmpty {
        listOf(Triple("站点列表待接入", "当前未返回可访问站点。", "待接入"))
    }
    P2ExtendedPageScaffold(
        kicker = "",
        title = "DApp 浏览器",
        subtitle = "",
        currentRoute = "dapp_browser",
        onBottomNav = onBottomNav,
        hubLabel = "内置浏览器",
        onHubClick = { onEvent(DappBrowserEvent.Refresh) },
        primaryActionLabel = "访问",
        onPrimaryAction = { onEvent(DappBrowserEvent.PrimaryActionClicked) },
        secondaryActionLabel = "返回",
        onSecondaryAction = { onEvent(DappBrowserEvent.SecondaryActionClicked) },
    ) {
        P2SearchShell(
            placeholder = "打开 $entryHint / 搜索 DApp / 输入 ENS",
            quickHint = "",
            animated = true,
            statusLabel = if (sessionFocus == 3) "谨慎域名" else "可访问",
            statusHealthy = sessionFocus != 3,
        )
        Spacer(modifier = Modifier.height(12.dp))
        KpiRow(items = browserMetrics, activeIndex = metricFocus)
        Spacer(modifier = Modifier.height(12.dp))
        ChipRow(
            items = listOf("精选", "DeFi", "支付", "NFT", "工具"),
            activeIndex = categoryFocus,
            animated = true,
        )
        Spacer(modifier = Modifier.height(14.dp))
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            displayCards.forEachIndexed { index, (title, subtitle, network) ->
                val riskFlag = subtitle.contains("未验证") || subtitle.contains("待接入") || subtitle.contains("阻塞")
                P2SessionAppCard(
                    title = title,
                    subtitle = subtitle,
                    network = network,
                    riskFlag = riskFlag,
                    actionLabel = if (riskFlag) "谨慎查看" else "查看",
                    emphasized = sessionFocus % displayCards.size == index,
                )
            }
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
