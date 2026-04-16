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
fun DappBrowserScreen(
    onBack: () -> Unit = {},
) {
    var url by rememberSaveable { mutableStateOf("https://app.uniswap.org") }
    val sites = listOf("Jupiter", "Uniswap", "PancakeSwap", "OpenSea")
    AppScaffold(title = "DApp Browser", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { SearchBar(value = url, onValueChange = { url = it }, placeholder = "输入 DApp 地址") }
            items(sites) { site -> GradientCard(title = site, subtitle = url) { PrimaryButton(text = "打开", onClick = { }) } }
        }
    }
}
