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
fun VerifyMnemonicScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val selected = remember { mutableStateListOf<String>() }
    AppScaffold(title = "确认助记词", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { GradientCard(title = "点击正确顺序的单词", subtitle = "至少选择一组用于校验") { Text("已选择：${selected.joinToString(" ")}") } }
            items(state.mnemonicWords.chunked(3)) { rowItems ->
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                    rowItems.forEach { word -> Box(modifier = Modifier.weight(1f)) { MnemonicChip(word, selected.contains(word)) { if (selected.contains(word)) selected.remove(word) else selected.add(word) } } }
                }
            }
            item { PrimaryButton(text = "完成验证", onClick = onDone) }
        }
    }
}
