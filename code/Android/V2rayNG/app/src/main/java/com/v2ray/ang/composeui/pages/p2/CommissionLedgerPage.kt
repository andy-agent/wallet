package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
        P2CoreCard {
            Text(total, style = MaterialTheme.typography.headlineMedium, color = Color(0xFF182345))
            Text(uiState.checklist.firstOrNull()?.detail ?: uiState.summary, style = MaterialTheme.typography.bodySmall, color = Color(0xFF6D789E))
            P2CoreChartPlaceholder(accent = Color(0xFF22C3A0))
        }
        P2CoreCard {
            P2CoreCardHeader(title = uiState.note)
            uiState.highlights.forEach { item ->
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
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
