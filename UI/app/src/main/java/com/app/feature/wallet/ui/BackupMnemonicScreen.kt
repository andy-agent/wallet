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
fun BackupMnemonicScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "备份助记词", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { GradientCard(title = "请离线保存", subtitle = "按顺序记录下所有单词") {} }
            items(state.mnemonicWords.chunked(3)) { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { word -> Box(modifier = Modifier.weight(1f)) { MnemonicChip(word, selected = false) } }
                }
            }
            item { PrimaryButton(text = "我已备份完成", onClick = onContinue) }
        }
    }
}
