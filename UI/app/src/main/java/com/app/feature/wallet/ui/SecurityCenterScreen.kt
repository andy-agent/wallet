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
fun SecurityCenterScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "安全中心", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { GradientCard(title = "安全设置", subtitle = "轻玻璃卡片 + 高圆角布局") {} }
            items(state.securityItems.keys.toList()) { key -> SecurityEntryItem(title = key, checked = state.securityItems[key] == true, onCheckedChange = { viewModel.toggleSecurity(key) }) }
        }
    }
}
