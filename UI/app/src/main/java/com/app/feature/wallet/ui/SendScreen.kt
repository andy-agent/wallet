package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.common.widgets.MetricPill
import com.app.core.theme.CardGlassStrong
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.viewmodel.WalletViewModel
import com.app.core.utils.Formatters

@Composable
fun SendScreen(
    symbol: String,
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSent: (String) -> Unit = {},
) {
    var address by rememberSaveable { mutableStateOf("") }
    var amount by rememberSaveable { mutableStateOf("10") }
    val asset = viewModel.token(symbol)
    AppScaffold(title = "发送资产", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "发送 $symbol", subtitle = asset?.name ?: "链上转账") {
                Text(asset?.let { Formatters.money(it.balance * it.priceUsd) } ?: "--", style = MaterialTheme.typography.headlineMedium)
                Spacer(Modifier.height(12.dp))
                MetricPill("可用余额", asset?.balance?.toString() ?: "--")
            }
            GradientCard(title = "收款地址", subtitle = "支持后续接扫码与地址簿") {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("收款地址") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardGlassStrong.copy(alpha = 0.76f),
                        focusedContainerColor = CardGlassStrong,
                    ),
                )
            }
            GradientCard(title = "发送数量", subtitle = "预估网络费与到账") {
                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("数量") },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedContainerColor = CardGlassStrong.copy(alpha = 0.76f),
                        focusedContainerColor = CardGlassStrong,
                    ),
                )
                Spacer(Modifier.height(12.dp))
                InfoRow("网络费", "~ $0.54")
                InfoRow("到账网络", asset?.chainId ?: "--")
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "继续发送", onClick = { viewModel.send(symbol, address, amount.toDoubleOrNull() ?: 0.0, onSent) })
            }
        }
    }
}
