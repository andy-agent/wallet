package com.v2ray.ang.composeui.pages.p2

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.graphics.Color
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.unit.dp
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailEvent
import com.v2ray.ang.composeui.p2.model.LegalDocumentDetailUiState
import com.v2ray.ang.composeui.p2.model.legalDocumentDetailPreviewState
import com.v2ray.ang.composeui.p2.viewmodel.LegalDocumentDetailViewModel
import com.v2ray.ang.composeui.theme.CryptoVpnTheme

@Composable
fun LegalDocumentDetailRoute(
    viewModel: LegalDocumentDetailViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    LegalDocumentDetailScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                LegalDocumentDetailEvent.PrimaryActionClicked -> onPrimaryAction()
                LegalDocumentDetailEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun LegalDocumentDetailScreen(
    uiState: LegalDocumentDetailUiState,
    onEvent: (LegalDocumentDetailEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val version = uiState.metrics.firstOrNull()?.value ?: "v2025.04"
    val effective = uiState.highlights.firstOrNull()?.trailing?.ifBlank { "2025-04-01" } ?: "2025-04-01"
    P2CorePageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = uiState.summary,
        badge = null,
        activeSection = CoreNavSection.Profile,
        onBottomNav = onBottomNav,
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(LegalDocumentDetailEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel,
        onSecondaryAction = { onEvent(LegalDocumentDetailEvent.SecondaryActionClicked) },
    ) {
        P2CoreCard {
            P2CoreChipRow(items = listOf("• $version", "生效日期：$effective"), activeIndex = 0)
            Text("1. 服务范围", style = MaterialTheme.typography.titleSmall, color = Color(0xFF182345))
            Text("CryptoVPN 提供基于订阅的 VPN 网络接入服务，以及配套的非托管钱包支付能力。", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF56627F))
            Spacer(modifier = Modifier.height(4.dp))
            Text("2. 付款与退款", style = MaterialTheme.typography.titleSmall, color = Color(0xFF182345))
            Text("链上支付一经广播不可撤回；若出现技术故障，平台按退款政策处理。", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF56627F))
            Spacer(modifier = Modifier.height(4.dp))
            Text("3. 钱包责任", style = MaterialTheme.typography.titleSmall, color = Color(0xFF182345))
            Text("助记词与私钥由用户自持；如遗失，平台无法帮助恢复资产。", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF56627F))
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun LegalDocumentDetailPreview() {
    CryptoVpnTheme {
        LegalDocumentDetailScreen(
            uiState = legalDocumentDetailPreviewState(),
            onEvent = {},
        )
    }
}
