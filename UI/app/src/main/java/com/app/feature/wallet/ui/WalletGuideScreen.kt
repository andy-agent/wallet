package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AccountBalanceWallet
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.viewmodel.WalletViewModel

@Composable
fun WalletGuideScreen(
    viewModel: WalletViewModel = viewModel(),
    onCreateWallet: () -> Unit = {},
    onImportWallet: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "", showTopBar = false) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 20.dp, vertical = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    StatusChip("P0 钱包引导")
                    Text("建立你的多链钱包", style = MaterialTheme.typography.headlineLarge)
                    Text("按 UI 原稿，这里承接创建、导入和观察模式，并且需要保持轻玻璃感。", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            }
            items(state.setupOptions) { option ->
                val icon = when (option.id) {
                    "create" -> Icons.Outlined.AccountBalanceWallet
                    "import_mnemonic" -> Icons.Outlined.Download
                    else -> Icons.Outlined.Visibility
                }
                GradientCard(title = option.title, subtitle = option.description) {
                    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Text(option.highlight, style = MaterialTheme.typography.bodyLarge)
                    }
                    Spacer(Modifier.height(12.dp))
                    PrimaryButton(text = "继续", onClick = if (option.id == "create") onCreateWallet else onImportWallet)
                }
            }
        }
    }
}
