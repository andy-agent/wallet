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
fun ReceiveScreen(
    symbol: String,
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    var address by remember { mutableStateOf("") }
    LaunchedEffect(symbol) { viewModel.receiveAddress(symbol) { address = it } }
    AppScaffold(title = "收款页", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
            GradientCard(title = "接收 $symbol", subtitle = "二维码与链上地址") {
                Box(modifier = Modifier.fillMaxWidth().height(220.dp), contentAlignment = Alignment.Center) { Text("QR") }
                Spacer(Modifier.height(12.dp))
                Text(address)
            }
        }
    }
}
