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
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicEvent
import com.v2ray.ang.composeui.p2extended.model.BackupMnemonicUiState
import com.v2ray.ang.composeui.p2extended.model.backupMnemonicPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.BackupMnemonicViewModel

@Composable
fun BackupMnemonicRoute(
    viewModel: BackupMnemonicViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    BackupMnemonicScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                BackupMnemonicEvent.PrimaryActionClicked -> onPrimaryAction()
                BackupMnemonicEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun BackupMnemonicScreen(
    uiState: BackupMnemonicUiState,
    onEvent: (BackupMnemonicEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "Backup",
        title = "备份助记词",
        subtitle = "创建成功后立即完成离线备份，这是恢复资产的唯一方式。",
        hubLabel = "高优先级",
        onHubClick = { onEvent(BackupMnemonicEvent.Refresh) },
        primaryActionLabel = "我已安全备份",
        onPrimaryAction = { onEvent(BackupMnemonicEvent.PrimaryActionClicked) },
        secondaryActionLabel = "导出到离线打印模板",
        onSecondaryAction = { onEvent(BackupMnemonicEvent.SecondaryActionClicked) },
    ) {
        P2Card(title = "请抄写以下 12 个单词", subtitle = "不要截屏、不要存云端、不要分享给任何人。") {
            MnemonicGrid(
                words = listOf(
                    "ocean", "brick", "velvet",
                    "lamp", "maple", "vivid",
                    "orbit", "coral", "charge",
                    "laptop", "anchor", "glow",
                ),
            )
            Spacer(modifier = Modifier.height(12.dp))
            SecurityStatusPill(label = "离线抄写确认", healthy = false)
        }
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun BackupMnemonicPreview() {
    CryptoVpnTheme {
        BackupMnemonicScreen(
            uiState = backupMnemonicPreviewState(),
            onEvent = {},
        )
    }
}
