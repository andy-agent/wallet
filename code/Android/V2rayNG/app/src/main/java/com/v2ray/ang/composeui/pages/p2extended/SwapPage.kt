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
import com.v2ray.ang.composeui.p2extended.model.SwapEvent
import com.v2ray.ang.composeui.p2extended.model.SwapUiState
import com.v2ray.ang.composeui.p2extended.model.swapPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.SwapViewModel

@Composable
fun SwapRoute(
    viewModel: SwapViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    SwapScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                SwapEvent.PrimaryActionClicked -> onPrimaryAction()
                SwapEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun SwapScreen(
    uiState: SwapUiState,
    onEvent: (SwapEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val routeStates = uiState.checklist
        .mapNotNull { it.title.takeMeaningfulSwapText() }
        .ifEmpty { listOf("未接入") }
    val routeDetails = uiState.checklist
        .mapNotNull { it.detail.takeMeaningfulSwapText() }
        .ifEmpty { listOf(routeStates.firstOrNull() ?: "未接入") }
    val routeFocus = rememberLoopingIndex(itemCount = maxOf(routeStates.size, 1), durationMillis = 4200)
    val headlineMetrics = uiState.metrics.take(3).map { it.label to it.value }.ifEmpty {
        listOf("状态" to "未接入")
    }
    val metricFocus = if (headlineMetrics.isNotEmpty()) routeFocus % headlineMetrics.size else -1
    val sourceMetric = uiState.metrics.getOrNull(0)?.value.orEmpty()
    val targetMetric = uiState.metrics.getOrNull(1)?.value.orEmpty()
    val sourceParts = sourceMetric.split(" ").filter { it.isNotBlank() }
    val targetParts = targetMetric.split(" ").filter { it.isNotBlank() }
    val payToken = sourceParts.firstOrNull() ?: "--"
    val payAmount = sourceParts.drop(1).joinToString(" ").ifBlank { "--" }
    val receiveToken = targetParts.firstOrNull() ?: "--"
    val receiveAmount = targetParts.drop(1).joinToString(" ").ifBlank { "--" }
    val slippage = uiState.fields.firstOrNull { it.key == "slippage" }?.value?.let { "$it%" } ?: "--"
    val routeState = uiState.badge.takeMeaningfulSwapText() ?: "未接入"
    val chipItems = uiState.fields
        .mapNotNull { it.label.takeMeaningfulSwapText() }
        .take(3)
        .ifEmpty { listOf("未接入") }
    val controlItems = uiState.checklist
        .mapNotNull { bullet ->
            val title = bullet.title.takeMeaningfulSwapText()
            val detail = bullet.detail.takeMeaningfulSwapText()
            if (title == null || detail == null) null else title to detail
        }
        .take(3)
        .ifEmpty {
            listOf(
                headlineMetrics.getOrElse(2) { "状态" to routeState },
                uiState.fields.firstOrNull { it.key == "amount" }?.let { it.label to it.value } ?: ("兑换数量" to "--"),
                uiState.fields.firstOrNull { it.key == "slippage" }?.let { it.label to slippage } ?: ("滑点" to slippage),
            )
        }
    P2ExtendedPageScaffold(
        kicker = uiState.subtitle,
        title = uiState.title,
        subtitle = "",
        currentRoute = "swap",
        onBottomNav = onBottomNav,
        hubLabel = routeState,
        onHubClick = { onEvent(SwapEvent.Refresh) },
        primaryActionLabel = uiState.primaryActionLabel,
        onPrimaryAction = { onEvent(SwapEvent.PrimaryActionClicked) },
        secondaryActionLabel = uiState.secondaryActionLabel ?: "返回",
        onSecondaryAction = { onEvent(SwapEvent.SecondaryActionClicked) },
    ) {
        KpiRow(items = headlineMetrics, activeIndex = metricFocus)
        Spacer(modifier = Modifier.height(12.dp))
        P2SwapPairCard(
            payToken = payToken,
            payChain = "",
            payAmount = payAmount,
            receiveToken = receiveToken,
            receiveChain = "",
            receiveAmount = receiveAmount,
            routeDetail = routeDetails[routeFocus % routeDetails.size],
            routeStateLabel = routeStates[routeFocus % routeStates.size],
        )
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "兑换控制") {
            ChipRow(
                items = chipItems,
                activeIndex = routeFocus % chipItems.size,
                animated = true,
            )
            Spacer(modifier = Modifier.height(10.dp))
            KpiRow(
                items = controlItems,
                activeIndex = metricFocus.takeIf { it >= 0 } ?: 0,
            )
        }
    }
}

private fun String?.takeMeaningfulSwapText(): String? {
    val normalized = this?.trim().orEmpty()
    return normalized.takeUnless { it.isBlank() || it.isSwapPlaceholderText() }
}

private fun String.isSwapPlaceholderText(): Boolean {
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
private fun SwapPreview() {
    CryptoVpnTheme {
        SwapScreen(
            uiState = swapPreviewState(),
            onEvent = {},
        )
    }
}
