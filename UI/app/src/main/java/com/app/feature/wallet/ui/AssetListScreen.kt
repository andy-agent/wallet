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
fun AssetListScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenToken: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "钱包首页", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            item { SectionHeader("多链资产") }
            items(state.assets) { asset -> TokenItem(asset = asset, onClick = { onOpenToken(asset.symbol) }) }
        }
    }
}
