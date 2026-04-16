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
fun InputMnemonicScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onContinue: () -> Unit = {},
) {
    var mnemonic by rememberSaveable { mutableStateOf("") }
    AppScaffold(title = "输入助记词", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "恢复钱包", subtitle = "请输入 12 或 24 个助记词") {
                OutlinedTextField(value = mnemonic, onValueChange = { mnemonic = it }, modifier = Modifier.fillMaxWidth().height(180.dp), label = { Text("助记词") })
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "继续", onClick = { viewModel.importWallet(mnemonic) { if (it) onContinue() } })
            }
        }
    }
}
