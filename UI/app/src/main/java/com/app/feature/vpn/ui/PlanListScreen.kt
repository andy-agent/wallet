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
fun PlanListScreen(
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSelectPlan: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "套餐页", onBack = onBack) { padding ->
        LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(18.dp), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            items(state.plans) { plan -> PlanCard(plan = plan, onSelect = { onSelectPlan(plan.id) }) }
        }
    }
}
