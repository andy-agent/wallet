package com.app.feature.vpn.ui

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
import com.app.feature.vpn.components.*
import com.app.feature.vpn.viewmodel.VpnViewModel
import com.app.vpncore.model.VpnState
import com.app.core.utils.Formatters

import androidx.compose.runtime.saveable.rememberSaveable

@Composable
fun WithdrawCommissionScreen(
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    var amount by rememberSaveable { mutableStateOf("80") }
    var address by rememberSaveable { mutableStateOf("TR7NHq…提现地址") }
    AppScaffold(title = "提现佣金", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "提现到钱包", subtitle = "佣金账本余额结算") {
                OutlinedTextField(value = amount, onValueChange = { amount = it }, label = { Text("金额") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(10.dp))
                OutlinedTextField(value = address, onValueChange = { address = it }, label = { Text("收款地址") }, modifier = Modifier.fillMaxWidth())
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "提交提现", onClick = { viewModel.withdraw(amount.toDoubleOrNull() ?: 0.0, address) { } })
            }
        }
    }
}
