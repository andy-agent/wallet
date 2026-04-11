package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.AssetDetailEvent
import com.v2ray.ang.composeui.p2.model.AssetDetailUiState
import com.v2ray.ang.composeui.p2.model.assetDetailPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.AssetDetailViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun AssetDetailRoute(
    viewModel: AssetDetailViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    AssetDetailScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                AssetDetailEvent.PrimaryActionClicked -> onPrimaryAction()
                AssetDetailEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun AssetDetailScreen(
    uiState: AssetDetailUiState,
    onEvent: (AssetDetailEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val metricMap = uiState.metrics.associate { it.label to it.value }
    val walletTitle = metricMap["资产"] ?: uiState.title
    val balance = metricMap["余额"] ?: "12,840 USDT"
    val change = metricMap["今日"] ?: "+0.12%"
    val distribution = uiState.checklist.take(3).map { it.title to it.detail }
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = walletTitle,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(AssetDetailEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(AssetDetailEvent.SecondaryActionClicked) },
    ) {
        P2CoreTrendCard(
            title = "资产走势",
            value = balance,
            caption = "余额 $balance · 今日 $change",
            accent = Color(0xFF2F5BFF),
        )
        P2CoreChipRow(items = listOf("24H", "7D", "30D", "入账/出账"))
        if (distribution.isNotEmpty()) {
            P2CoreMetricGrid(items = distribution, accentIndexes = setOf(2))
        }
        P2CoreCard {
            P2CoreCardHeader(title = "最近交易", trailing = "3 笔待确认", trailingColor = Color(0xFFEAF6FF))
            uiState.highlights.forEach { item ->
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    trailingColor = if (item.trailing.contains("成功")) Color(0xFF17B48A) else Color(0xFF66739D),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun AssetDetailPreview() {
    CryptoVpnTheme {
        AssetDetailScreen(
            uiState = assetDetailPreviewState(),
            onEvent = {},
        )
    }
}
