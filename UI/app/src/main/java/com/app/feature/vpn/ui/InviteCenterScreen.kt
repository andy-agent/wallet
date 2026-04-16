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
fun InviteCenterScreen(
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
    onOpenLedger: () -> Unit = {},
    onOpenWithdraw: () -> Unit = {},
) {
    val summary = viewModel.uiState.collectAsState().value.referralSummary ?: return
    AppScaffold(title = "邀请中心", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            item { GradientCard(title = "邀请码 $${summary.inviteCode}", subtitle = "以订阅返佣作为增长主链路") {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) { MetricPill("邀请人数", summary.invitedUsers.toString()); MetricPill("付费人数", summary.paidUsers.toString()) }
                Spacer(Modifier.height(12.dp))
                InfoRow("累计佣金", Formatters.money(summary.totalCommissionUsd))
                InfoRow("可提现", Formatters.money(summary.withdrawableUsd))
            } }
            item { PrimaryButton(text = "查看账本", onClick = onOpenLedger) }
            item { SecondaryButton(text = "申请提现", onClick = onOpenWithdraw) }
        }
    }
}
