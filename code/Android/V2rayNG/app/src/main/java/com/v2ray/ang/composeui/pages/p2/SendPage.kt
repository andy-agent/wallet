package com.v2ray.ang.composeui.pages.p2

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.p2.model.SendEvent
import com.v2ray.ang.composeui.p2.model.SendUiState
import com.v2ray.ang.composeui.p2.model.sendPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.SendViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun SendRoute(
    viewModel: SendViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SendScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SendEvent.PrimaryActionClicked -> onPrimaryAction()
                SendEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SendScreen(
    uiState: SendUiState,
    onEvent: (SendEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val amount = uiState.fields.firstOrNull { it.key == "amount" }?.value ?: uiState.metrics.getOrNull(1)?.value.orEmpty()
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(SendEvent.PrimaryActionClicked) },
    ) {
        P2CoreCard {
            uiState.fields.take(2).forEach { field ->
                P2CoreField(
                    label = field.label,
                    value = field.value,
                    supportingText = field.supportingText,
                )
            }
            P2CoreChipRow(items = listOf("TRON · Fee 更低", "Solana"), activeIndex = 0)
        }
        P2CoreTrendCard(
            title = "发送概览",
            value = amount,
            caption = "≈ $amount USDT · 广播后不可撤回",
            accent = Color(0xFF19B78C),
        )
        P2CoreMetricGrid(
            items = listOf(
                "网络费" to (uiState.metrics.getOrNull(2)?.value ?: "1.24 USDT"),
                "预计到账" to (uiState.metrics.getOrNull(3)?.value ?: "~ 38 秒"),
            ),
        )
        P2CoreCard {
            P2CoreCardHeader(title = "安全检查", trailing = "通过 3/4", trailingColor = Color(0xFFEAF6FF))
            uiState.highlights.forEach { item ->
                P2CoreListRow(title = item.title, subtitle = item.subtitle)
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun SendPreview() {
    CryptoVpnTheme {
        SendScreen(
            uiState = sendPreviewState(),
            onEvent = {},
        )
    }
}
