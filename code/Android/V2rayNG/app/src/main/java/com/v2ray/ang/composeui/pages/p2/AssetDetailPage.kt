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
    val walletTitle = metricMap["资产"] ?: uiState.metrics.firstOrNull()?.value ?: uiState.title
    val balance = metricMap["余额"] ?: metricMap["持仓"] ?: uiState.metrics.getOrNull(1)?.value ?: "--"
    val change = metricMap["今日"] ?: metricMap["24H"] ?: metricMap["订单数"] ?: ""
    val distribution = uiState.checklist.take(3).map { it.title to it.detail }
    val infoItems = if (distribution.isNotEmpty()) {
        distribution
    } else {
        uiState.metrics.take(3).map { it.label to it.value }
    }
    val changeColor = if (change.startsWith("-")) Color(0xFFFFEEE9) else Color(0xFFE6FFF6)
    val chartAccent = if (change.startsWith("-")) Color(0xFFE86767) else Color(0xFF23C8A8)
    val supportingText = ""
    val chartTitle = if (distribution.isNotEmpty()) "资产分布" else "资产概览"
    val chartSubtitle = uiState.note.takeUnless { it.isBlank() } ?: ""
    val recentTrailing = if (uiState.highlights.isEmpty()) "待接入" else "${uiState.highlights.size} 条记录"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = walletTitle,
        subtitle = uiState.summary,
        badge = null,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(AssetDetailEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(AssetDetailEvent.SecondaryActionClicked) },
    ) {
        P2CoreHeroValueCard(
            label = walletTitle,
            value = balance,
            supportingText = supportingText,
            highlight = null,
            stats = emptyList(),
        )
        P2CoreChartInfoBlock(
            title = chartTitle,
            subtitle = chartSubtitle,
            chips = infoItems.map { it.first }.take(4),
            infoItems = infoItems,
            highlight = change.takeUnless { it.isBlank() },
            highlightColor = changeColor,
            accent = chartAccent,
        )
        P2CoreCard {
            P2CoreCardHeader(title = "最近记录", trailing = recentTrailing, trailingColor = Color(0xFFEAF6FF))
            if (uiState.highlights.isEmpty()) {
                P2CoreListRow(
                    title = "暂无资产记录",
                    subtitle = "",
                )
            } else {
                uiState.highlights.forEach { item ->
                    P2CoreListRow(
                        title = item.title,
                        subtitle = item.subtitle,
                        trailing = item.trailing,
                        emphasis = when {
                            item.trailing.contains("成功") -> P2CoreRowEmphasis.Success
                            item.trailing.contains("确认") -> P2CoreRowEmphasis.Warning
                            else -> P2CoreRowEmphasis.Brand
                        },
                        trailingColor = when {
                            item.trailing.contains("成功") -> Color(0xFF17B48A)
                            item.trailing.contains("确认") -> Color(0xFFE39B22)
                            else -> Color(0xFF66739D)
                        },
                    )
                }
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
