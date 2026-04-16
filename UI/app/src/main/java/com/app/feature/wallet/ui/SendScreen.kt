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
fun SendScreen(
    symbol: String,
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSent: (String) -> Unit = {},
) {
    var address by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("10") }
    AppScaffold(title = "发送资产", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "发送 $symbol", subtitle = "按照原稿保留大卡片表单结构") {
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("收款地址") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("数量") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                InfoRow("网络费", "~ $0.54")
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "继续发送", onClick = { viewModel.send(symbol, address, amount.toDoubleOrNull() ?: 0.0, onSent) })
            }
        }
    }
}
