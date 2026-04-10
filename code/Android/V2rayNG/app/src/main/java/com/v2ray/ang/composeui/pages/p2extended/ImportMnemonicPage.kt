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
        P2Card(title = "助记词恢复", subtitle = "你的助记词仅在本地解析，不会上传真服务器。") {
            FieldRow("助记词", "ocean brick velvet lamp maple vivid orbit coral charge laptop anchor glow")
            Spacer(modifier = Modifier.height(12.dp))
            ChipRow(items = listOf("自动识别链", "仅恢复 Solana", "仅恢复 TRON", "高级"), activeIndex = 0)
            Spacer(modifier = Modifier.height(12.dp))
            NoteCard(title = "已识别链", text = "Solana · TRON · Base")
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
