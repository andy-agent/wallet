package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.CommissionLedgerEvent
import com.v2ray.ang.composeui.p2.model.CommissionLedgerUiState
import com.v2ray.ang.composeui.p2.model.commissionLedgerPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.CommissionLedgerViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun CommissionLedgerRoute(
    viewModel: CommissionLedgerViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    CommissionLedgerScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                CommissionLedgerEvent.PrimaryActionClicked -> onPrimaryAction()
                CommissionLedgerEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun CommissionLedgerScreen(
    uiState: CommissionLedgerUiState,
    onEvent: (CommissionLedgerEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val total = uiState.metrics.firstOrNull()?.value ?: "$3,481.22"
    val trendFocus = rememberCoreLoopingIndex(itemCount = 3, durationMillis = 4200)
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Growth,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(CommissionLedgerEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(CommissionLedgerEvent.SecondaryActionClicked) },
    ) {
        P2CoreHeroValueCard(
            label = uiState.note,
            value = total,
            supportingText = uiState.checklist.firstOrNull()?.detail ?: uiState.summary,
            highlight = uiState.badge,
            stats = uiState.metrics.drop(1).take(2).map { it.label to it.value },
        )
        P2CoreChartInfoBlock(
            title = "佣金趋势",
            subtitle = "按来源拆分统计",
            chips = listOf("今日", "本周", "本月"),
            infoItems = uiState.metrics.take(3).map { it.label to it.value },
            highlight = "更新中",
            accent = Color(0xFF22C3A0),
            activeChipIndex = trendFocus,
        )
        P2CoreActionValueRow(
            label = "月度新增",
            value = uiState.badge,
            actionLabel = uiState.secondaryActionLabel,
            onAction = { onEvent(CommissionLedgerEvent.SecondaryActionClicked) },
            valueColor = Color(0xFF16B889),
        )
        P2CoreCard {
            P2CoreCardHeader(title = uiState.note)
            uiState.highlights.forEachIndexed { index, item ->
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    emphasis = if (index == trendFocus % maxOf(uiState.highlights.size, 1)) {
                        P2CoreRowEmphasis.Success
                    } else if (item.trailing.contains("+")) {
                        P2CoreRowEmphasis.Success
                    } else {
                        P2CoreRowEmphasis.Warning
                    },
                    trailingColor = Color(0xFF16B889),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun CommissionLedgerPreview() {
    CryptoVpnTheme {
        CommissionLedgerScreen(
            uiState = commissionLedgerPreviewState(),
            onEvent = {},
        )
    }
}
