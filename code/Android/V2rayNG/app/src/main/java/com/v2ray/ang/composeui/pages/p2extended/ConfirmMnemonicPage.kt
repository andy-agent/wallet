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
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.ConfirmMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.confirmMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ConfirmMnemonicViewModel

@Composable
fun ConfirmMnemonicRoute(
    viewModel: ConfirmMnemonicViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ConfirmMnemonicScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ConfirmMnemonicEvent.PrimaryActionClicked -> onPrimaryAction()
                ConfirmMnemonicEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ConfirmMnemonicScreen(
    uiState: ConfirmMnemonicUiState,
    onEvent: (ConfirmMnemonicEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "Verify Backup",
        title = "确认助记词",
        subtitle = "通过抽查验证确保你已经正确备份。",
        hubLabel = "验证中",
        onHubClick = { onEvent(ConfirmMnemonicEvent.Refresh) },
        primaryActionLabel = "完成校验并进入钱包",
        onPrimaryAction = { onEvent(ConfirmMnemonicEvent.PrimaryActionClicked) },
        secondaryActionLabel = "返回备份页",
        onSecondaryAction = { onEvent(ConfirmMnemonicEvent.SecondaryActionClicked) },
    ) {
        P2Card(title = "按顺序选择缺失的单词", subtitle = "系统会随机抽查3个位置。") {
            MnemonicCheckpointRow(
                label = "第 2 个单词",
                answer = "brick",
                verified = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            MnemonicCheckpointRow(
                label = "第 7 个单词",
                answer = "orbit",
                verified = true,
            )
            Spacer(modifier = Modifier.height(8.dp))
            MnemonicCheckpointRow(
                label = "第 11 个单词",
                answer = "anchor",
                verified = false,
            )
            Spacer(modifier = Modifier.height(12.dp))
            ChipRow(items = listOf("brick", "orbit", "anchor", "velvet", "glow", "coral"), activeIndex = 0)
        }
        Spacer(modifier = Modifier.height(12.dp))
        P2Card(title = "校验状态", subtitle = "逐项通过后才会放行进入钱包。") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                P2FlowStepCard(
                    step = "STATE 1",
                    title = "随机位置抽查",
                    detail = "至少 3 个位置命中正确单词",
                    emphasized = true,
                )
                P2FlowStepCard(
                    step = "STATE 2",
                    title = "顺序一致性检查",
                    detail = "防止单词正确但顺序错误导致恢复失败",
                )
            }
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ConfirmMnemonicPreview() {
    CryptoVpnTheme {
        ConfirmMnemonicScreen(
            uiState = confirmMnemonicPreviewState(),
            onEvent = {},
        )
    }
}
