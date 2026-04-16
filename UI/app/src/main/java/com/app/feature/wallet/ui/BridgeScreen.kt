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
fun BridgeScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    var symbol by rememberSaveable { mutableStateOf("USDT") }
    var targetChain by rememberSaveable { mutableStateOf("Base") }
    var amount by rememberSaveable { mutableStateOf("80") }
    AppScaffold(title = "跨链桥接", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "Bridge", subtitle = "从钱包资产直接发起桥接") {
                OutlinedTextField(value = symbol, onValueChange = { symbol = it }, label = { Text("资产") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = targetChain, onValueChange = { targetChain = it }, label = { Text("目标链") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("数量") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "发起桥接", onClick = { viewModel.bridge(symbol, targetChain, amount.toDoubleOrNull() ?: 0.0) { } })
            }
        }
    }
}
