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

import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun SwapScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    var fromSymbol by rememberSaveable { mutableStateOf("USDT") }
    var toSymbol by rememberSaveable { mutableStateOf("SOL") }
    var amount by rememberSaveable { mutableStateOf("580") }
    AppScaffold(title = "币币兑换", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "Swap", subtitle = "保留原稿的双币输入卡片") {
                OutlinedTextField(value = fromSymbol, onValueChange = { fromSymbol = it }, label = { Text("卖出") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = toSymbol, onValueChange = { toSymbol = it }, label = { Text("买入") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("数量") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "执行兑换", onClick = { viewModel.swap(fromSymbol, toSymbol, amount.toDoubleOrNull() ?: 0.0) { } })
            }
        }
    }
}
