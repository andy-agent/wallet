package com.v2ray.ang.composeui.pages.p2extended

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.v2ray.ang.composeui.theme.CryptoVpnTheme
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ImportMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.importMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportMnemonicViewModel

@Composable
fun ImportMnemonicRoute(
    viewModel: ImportMnemonicViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ImportMnemonicScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ImportMnemonicEvent.PrimaryActionClicked -> onPrimaryAction()
                ImportMnemonicEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ImportMnemonicScreen(
    uiState: ImportMnemonicUiState,
    onEvent: (ImportMnemonicEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    val wordFocus = rememberLoopingIndex(itemCount = 12, durationMillis = 7200)
    val chainFocus = rememberLoopingIndex(itemCount = 3, durationMillis = 3600)
    val modeFocus = rememberLoopingIndex(itemCount = 4, durationMillis = 4200)
    val validationFocus = rememberLoopingIndex(itemCount = 2, durationMillis = 3800)
    val importMetrics = uiState.metrics.take(3).map { it.label to it.value }
    val metricFocus = if (importMetrics.isNotEmpty()) chainFocus % importMetrics.size else -1
    P2ExtendedPageScaffold(
        kicker = "Mnemonic Import",
        title = "输入助记词",
        subtitle = "支持12/18/24个单词，并自动识别可恢复的链。",
        hubLabel = "离线解析",
        onHubClick = { onEvent(ImportMnemonicEvent.Refresh) },
        primaryActionLabel = "解析并继续",
        onPrimaryAction = { onEvent(ImportMnemonicEvent.PrimaryActionClicked) },
        secondaryActionLabel = "返回导入方式",
        onSecondaryAction = { onEvent(ImportMnemonicEvent.SecondaryActionClicked) },
    ) {
        KpiRow(items = importMetrics, activeIndex = metricFocus)
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "助记词恢复", subtitle = "你的助记词仅在本地解析，不会上传真服务器。") {
            MnemonicGrid(
                words = listOf(
                    "ocean", "brick", "velvet",
                    "lamp", "maple", "vivid",
                    "orbit", "coral", "charge",
                    "laptop", "anchor", "glow",
                ),
                focusIndex = wordFocus,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ChipRow(
                items = listOf("自动识别链", "仅恢复 Solana", "仅恢复 TRON", "高级"),
                activeIndex = modeFocus,
                animated = true,
            )
            Spacer(modifier = Modifier.height(12.dp))
            DetectedChainList(chains = listOf("Solana", "TRON", "Base"), activeIndex = chainFocus)
            Spacer(modifier = Modifier.height(10.dp))
            SecurityStatusPill(
                label = "本地解析模式",
                healthy = true,
                animated = true,
            )
        }
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "导入安全校验", subtitle = "完成下列检查后再进入下一步。") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                P2FlowStepCard(
                    step = "CHECK 1",
                    title = "词序与校验和验证",
                    detail = "自动检查长度、词表合法性与顺序完整性",
                    emphasized = validationFocus == 0,
                    animated = true,
                )
                P2FlowStepCard(
                    step = "CHECK 2",
                    title = "链账户派生预览",
                    detail = "仅本地预览 Solana、TRON 与 EVM 地址",
                    emphasized = validationFocus == 1,
                    animated = true,
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ImportMnemonicPreview() {
    CryptoVpnTheme {
        ImportMnemonicScreen(
            uiState = importMnemonicPreviewState(),
            onEvent = {},
        )
    }
}
