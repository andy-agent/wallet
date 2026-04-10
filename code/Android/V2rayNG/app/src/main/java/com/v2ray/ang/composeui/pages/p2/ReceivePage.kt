package com.v2ray.ang.composeui.pages.p2

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import com.v2ray.ang.composeui.p2.model.ReceiveEvent
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import com.v2ray.ang.composeui.p2.model.receivePreviewState
import com.v2ray.ang.composeui.p2.viewmodel.ReceiveViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun ReceiveRoute(
    viewModel: ReceiveViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ReceiveScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ReceiveEvent.PrimaryActionClicked -> onPrimaryAction()
                ReceiveEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ReceiveScreen(
    uiState: ReceiveUiState,
    onEvent: (ReceiveEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val chips = uiState.metrics.take(3).map { it.value }
    val address = uiState.fields.firstOrNull()?.value ?: "--"
    val status = uiState.metrics.getOrNull(3)?.value ?: "已校验"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = uiState.badge,
        activeSection = CoreNavSection.Wallet,
        onBottomNav = onBottomNav,
    ) {
        if (chips.isNotEmpty()) {
            P2CoreChipRow(items = chips, activeIndex = 0)
        }
        P2CoreCard {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.Center,
            ) {
                P2CoreQrPlaceholder()
            }
        }
        P2CoreCard {
            P2CoreCardHeader(title = "收款地址", trailing = status, trailingColor = Color(0xFFE6FFF6))
            Text(address, style = MaterialTheme.typography.bodyLarge, color = Color(0xFF182345), fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.height(4.dp))
            CoreActionRow(
                primaryActionLabel = uiState.secondaryActionLabel ?: "分享二维码",
                onPrimaryAction = { onEvent(ReceiveEvent.SecondaryActionClicked) },
                secondaryActionLabel = uiState.primaryActionLabel,
                onSecondaryAction = { onEvent(ReceiveEvent.PrimaryActionClicked) },
            )
            P2CoreNoteCard(title = "请确认链一致", text = uiState.note)
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ReceivePreview() {
    CryptoVpnTheme {
        ReceiveScreen(
            uiState = receivePreviewState(),
            onEvent = {},
        )
    }
}
