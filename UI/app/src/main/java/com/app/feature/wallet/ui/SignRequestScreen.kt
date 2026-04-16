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
fun SignRequestScreen(
    requestId: String,
    onBack: () -> Unit = {},
    onApprove: () -> Unit = {},
) {
    AppScaffold(title = "签名确认", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "Sign Request", subtitle = requestId) {
                InfoRow("来源", "Jupiter Aggregator")
                InfoRow("地址", "0x49bF…dEm0")
                InfoRow("网络", "Solana / Ethereum")
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "批准签名", onClick = onApprove)
            }
        }
    }
}
