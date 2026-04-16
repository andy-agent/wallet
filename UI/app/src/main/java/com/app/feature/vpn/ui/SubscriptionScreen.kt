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


@Composable
fun SubscriptionScreen(
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    val subscription = state.subscription ?: return
    AppScaffold(title = "订阅链接", onBack = onBack) { padding ->
        Column(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            GradientCard(title = "当前订阅", subtitle = subscription.url) {
                InfoRow("上次刷新", Formatters.dateTime(subscription.lastUpdatedAt))
                InfoRow("到期时间", Formatters.dateTime(subscription.expiresAt))
                InfoRow("流量", "$${subscription.trafficUsedGb} / $${subscription.trafficTotalGb} GB")
                Spacer(Modifier.height(12.dp))
                PrimaryButton(text = "刷新订阅", onClick = viewModel::refreshSubscription)
            }
        }
    }
}
