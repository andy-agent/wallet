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
fun AddCustomTokenScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    var symbol by rememberSaveable { mutableStateOf("") }
    var name by rememberSaveable { mutableStateOf("") }
    var chain by rememberSaveable { mutableStateOf("ethereum") }
    AppScaffold(title = "添加自定义代币", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "手动录入 Token", subtitle = "合约地址可在后续真实链路接入") {
                OutlinedTextField(value = symbol, onValueChange = { symbol = it }, label = { Text("代币符号") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("代币名称") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = chain, onValueChange = { chain = it }, label = { Text("所属链") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "保存代币", onClick = { viewModel.addCustomToken(symbol, name, chain) { } })
            }
        }
    }
}
