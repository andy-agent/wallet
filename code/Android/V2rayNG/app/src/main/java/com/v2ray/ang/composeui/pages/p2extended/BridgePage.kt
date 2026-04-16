package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.BridgeEvent
import com.v2ray.ang.composeui.p2extended.model.BridgeUiState
import com.v2ray.ang.composeui.p2extended.model.bridgePreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.BridgeViewModel

@Composable
fun BridgeRoute(
    viewModel: BridgeViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    BridgeScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                BridgeEvent.PrimaryActionClicked -> onPrimaryAction()
                BridgeEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun BridgeScreen(
    uiState: BridgeUiState,
    onEvent: (BridgeEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val sourceChain = uiState.metrics.getOrNull(0)?.value.takeMeaningfulBridgeText() ?: "未接入"
    val targetChain = uiState.metrics.getOrNull(1)?.value.takeMeaningfulBridgeText() ?: "未接入"
    val eta = uiState.metrics.getOrNull(2)?.value.takeMeaningfulBridgeText() ?: "--"
    val amountField = uiState.fields.firstOrNull { it.key == "amount" }
    val amount = amountField?.value.takeMeaningfulBridgeText() ?: "--"
    val targetAddress = uiState.fields.firstOrNull { it.key == "to" }?.value.takeMeaningfulBridgeText()
        ?: "未接入"
    val parameterItems = uiState.checklist
        .mapNotNull { bullet ->
            val title = bullet.title.takeMeaningfulBridgeText()
            val detail = bullet.detail.takeMeaningfulBridgeText()
            if (title == null || detail == null) null else title to detail
        }
        .take(3)
        .ifEmpty {
            listOf(
                amountField?.let { it.label to amount } ?: ("桥接数量" to amount),
                "目标地址" to targetAddress,
                "状态" to (uiState.badge.takeMeaningfulBridgeText() ?: "未接入"),
            )
        }
    P2ExtendedPageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = "",
        currentRoute = "bridge",
        onBottomNav = onBottomNav,
        hubLabel = uiState.badge.takeMeaningfulBridgeText() ?: "未接入",
        onHubClick = { onEvent(BridgeEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(BridgeEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel ?: "返回",
        onSecondaryAction = { onEvent(BridgeEvent.SecondaryActionClicked) },
    ) {
        P2Card(title = "桥接流程") {
            P2FlowStepCard(
                step = "01",
                title = "来源链",
                detail = sourceChain,
                emphasized = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            P2FlowStepCard(
                step = "02",
                title = "目标链",
                detail = targetChain,
            )
            Spacer(modifier = Modifier.height(8.dp))
            P2FlowStepCard(
                step = "03",
                title = "桥接数量",
                detail = amount,
            )
            Spacer(modifier = Modifier.height(10.dp))
            KpiRow(
                items = listOf(
                    "预计耗时" to eta,
                    "目标地址" to targetAddress,
                    "状态" to (uiState.badge.takeMeaningfulBridgeText() ?: "未接入"),
                ),
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "桥接参数") {
            KpiRow(items = parameterItems)
        }
    }
}

private fun String?.takeMeaningfulBridgeText(): String? {
    val normalized = this?.trim().orEmpty()
    return normalized.takeUnless { it.isBlank() || it.isBridgePlaceholderText() }
}

private fun String.isBridgePlaceholderText(): Boolean {
    val lower = lowercase()
    val markers = listOf(
        "mock",
        "preview",
        "stub",
        "drop-in",
        "repository",
        "navigation",
        "route",
        "viewmodel",
        "占位",
        "默认演示",
    )
    return markers.any(lower::contains)
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun BridgePreview() {
    CryptoVpnTheme {
        BridgeScreen(
            uiState = bridgePreviewState(),
            onEvent = {},
        )
    }
}
