package com.app.feature.wallet.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.GradientCard
import com.app.common.widgets.TokenIcon
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.feature.wallet.components.SecurityEntryItem
import com.app.feature.wallet.viewmodel.WalletViewModel

@Composable
fun ChainManagerScreen(
    viewModel: WalletViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "链管理", onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            items(state.chains) { chain ->
                GradientCard {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        TokenIcon(symbol = chain.id, size = 42.dp)
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(chain.name, style = MaterialTheme.typography.titleMedium)
                            Text(chain.symbol, style = MaterialTheme.typography.bodySmall, color = TextSecondary)
                        }
                    }
                    Spacer(Modifier.height(12.dp))
                    SecurityEntryItem(
                        title = "启用",
                        checked = chain.enabled,
                        onCheckedChange = { viewModel.toggleChain(chain.id) },
                    )
                }
            }
        }
    }
}
