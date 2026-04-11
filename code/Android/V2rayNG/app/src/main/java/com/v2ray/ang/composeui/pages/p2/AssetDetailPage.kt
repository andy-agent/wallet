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
    val changeColor = if (change.startsWith("-")) Color(0xFFFFEEE9) else Color(0xFFE6FFF6)
    val chartAccent = if (change.startsWith("-")) Color(0xFFE86767) else Color(0xFF23C8A8)
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
        P2CoreHeroValueCard(
            label = walletTitle,
            value = balance,
            supportingText = "今日 $change · ${uiState.summary}",
            highlight = uiState.badge,
            stats = listOf(
                "24H" to change,
                "常用场景" to uiState.note,
            ),
        )
        P2CoreChartInfoBlock(
            title = "资产走势",
            subtitle = "24H 余额与入账趋势",
            chips = listOf("24H", "7D", "30D", "入账/出账"),
            infoItems = distribution,
            highlight = change,
            highlightColor = changeColor,
            accent = chartAccent,
        )
        P2CoreCard {
            P2CoreCardHeader(title = "最近交易", trailing = "3 笔待确认", trailingColor = Color(0xFFEAF6FF))
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
