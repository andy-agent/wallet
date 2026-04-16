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
fun ImportWalletScreen(
    onBack: () -> Unit = {},
    onInputMnemonic: () -> Unit = {},
) {
    AppScaffold(title = "导入钱包", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { GradientCard(title = "导入多链钱包", subtitle = "助记词 / 私钥 / 观察钱包") {
                PrimaryButton(text = "通过助记词导入", onClick = onInputMnemonic)
                Spacer(Modifier.height(8.dp))
                SecondaryButton(text = "观察钱包", onClick = { })
            } }
        }
    }
}
