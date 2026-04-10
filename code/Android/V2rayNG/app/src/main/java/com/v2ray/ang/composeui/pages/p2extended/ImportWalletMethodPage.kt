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
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodEvent
import com.v2ray.ang.composeui.p2extended.model.ImportWalletMethodUiState
import com.v2ray.ang.composeui.p2extended.model.importWalletMethodPreviewState
import com.v2ray.ang.composeui.p2extended.viewmodel.ImportWalletMethodViewModel

@Composable
fun ImportWalletMethodRoute(
    viewModel: ImportWalletMethodViewModel,
    onPrimaryAction: () -> Unit = {},
    onSecondaryAction: (() -> Unit)? = null,
    onBottomNav: (String) -> Unit = {},
) {
    val uiState by viewModel.uiState.collectAsState()
    ImportWalletMethodScreen(
        uiState = uiState,
        onEvent = { event ->
            viewModel.onEvent(event)
            when (event) {
                ImportWalletMethodEvent.PrimaryActionClicked -> onPrimaryAction()
                ImportWalletMethodEvent.SecondaryActionClicked -> onSecondaryAction?.invoke()
                else -> Unit
            }
        },
        onBottomNav = onBottomNav,
    )
}

@Composable
fun ImportWalletMethodScreen(
    uiState: ImportWalletMethodUiState,
    onEvent: (ImportWalletMethodEvent) -> Unit,
    onBottomNav: (String) -> Unit = {},
) {
    P2ExtendedPageScaffold(
        kicker = "WALLET IMPORT",
        title = "导入多链钱包",
        subtitle = "补齐完整钱包能力：创建、导入、恢复与地址隔离。",
        hubLabel = "新增能力",
        onHubClick = { onEvent(ImportWalletMethodEvent.Refresh) },
        primaryActionLabel = "继续导入",
        onPrimaryAction = { onEvent(ImportWalletMethodEvent.PrimaryActionClicked) },
        secondaryActionLabel = "返回钱包引导",
        onSecondaryAction = { onEvent(ImportWalletMethodEvent.SecondaryActionClicked) },
    ) {
        P2Card(title = "选择导入方式", subtitle = "支持助记词、私钥、Keystore 与观察钱包。") {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                ListRow("助记词导入", "适合完整恢复多链资产")
                ListRow("私钥导入", "适合单地址快速恢复")
                ListRow("Keystore / JSON", "适合 EVM 系资产")
            }
        }
        Spacer(modifier = Modifier.height(12.dp))
        ListRow("观察钱包", "只读地址，不可签名")
    }
}

@Preview(showBackground = true, widthDp = 393, heightDp = 852)
@Composable
private fun ImportWalletMethodPreview() {
    CryptoVpnTheme {
        ImportWalletMethodScreen(
            uiState = importWalletMethodPreviewState(),
            onEvent = {},
        )
    }
}
