package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.*
import com.app.core.theme.AppDimens
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.components.*
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.core.utils.Formatters


@Composable
fun WalletGuideScreen(
    viewModel: WalletViewModel = viewModel(),
    onCreateWallet: () -> Unit = {},
    onImportWallet: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "钱包引导") { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item {
                GradientCard(title = "建立你的多链钱包", subtitle = "围绕资产、支付与 VPN 订阅的一体化入口") {
                    Text("根据 UI 原稿，这里承接创建、导入和只读观察模式。")
                }
            }
            items(state.setupOptions) { option ->
                GradientCard(title = option.title, subtitle = option.description) {
                    Text(option.highlight)
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(text = "继续", onClick = if (option.id == "create") onCreateWallet else onImportWallet)
                }
            }
        }
    }
}
