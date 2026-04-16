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
fun SendResultScreen(
    txId: String,
    onBack: () -> Unit = {},
    onDone: () -> Unit = {},
) {
    AppScaffold(title = "发送结果", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.Center) {
            GradientCard(title = "交易已提交", subtitle = txId) {
                Text("链上交易已广播，请稍后在资产详情页查看确认状态。")
                Spacer(Modifier.height(16.dp))
                PrimaryButton(text = "返回钱包", onClick = onDone)
            }
        }
    }
}
