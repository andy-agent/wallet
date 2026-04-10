package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import com.v2ray.ang.composeui.p2.model.WithdrawEvent
import com.v2ray.ang.composeui.p2.model.WithdrawUiState
import com.v2ray.ang.composeui.p2.model.withdrawPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.WithdrawViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun WithdrawRoute(
    viewModel: WithdrawViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    WithdrawScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                WithdrawEvent.PrimaryActionClicked -> onPrimaryAction()
                WithdrawEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun WithdrawScreen(
    uiState: WithdrawUiState,
    onEvent: (WithdrawEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.note,
        badge = uiState.badge,
        activeSection = CoreNavSection.Growth,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(WithdrawEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(WithdrawEvent.SecondaryActionClicked) },
    ) {
        P2CoreMetricGrid(
            items = uiState.metrics.take(2).map { it.label to it.value } + listOf("网络手续费" to (uiState.metrics.getOrNull(2)?.value ?: "")),
            accentIndexes = emptySet(),
        )
        P2CoreCard {
            uiState.fields.forEach { field ->
                P2CoreField(
                    label = field.label,
                    value = field.value,
                    supportingText = field.supportingText,
                )
            }
            uiState.metrics.getOrNull(2)?.let { metric ->
                P2CoreField(label = metric.label, value = metric.value)
            }
        }
        P2CoreCard {
            P2CoreCardHeader(title = "Risk Review")
            uiState.highlights.forEach { item ->
                P2CoreListRow(
                    title = item.title,
                    subtitle = item.subtitle,
                    trailing = item.trailing,
                    trailingColor = Color(0xFF18B68B),
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun WithdrawPreview() {
    CryptoVpnTheme {
        WithdrawScreen(
            uiState = withdrawPreviewState(),
            onEvent = {},
        )
    }
}
