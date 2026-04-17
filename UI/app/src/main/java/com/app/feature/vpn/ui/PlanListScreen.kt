package com.app.feature.vpn.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.app.common.components.*
import com.app.core.theme.TextSecondary
import com.app.core.ui.AppScaffold
import com.app.feature.vpn.components.*
import com.app.feature.vpn.viewmodel.VpnViewModel

@Composable
fun PlanListScreen(
    viewModel: VpnViewModel = viewModel(),
    onBack: () -> Unit = {},
    onSelectPlan: (String) -> Unit = {},
) {
    val state by viewModel.uiState.collectAsState()
    AppScaffold(title = "套餐页", onBack = onBack) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 18.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                GradientCard(title = "选择套餐", subtitle = "根据 P1 原稿，这里强调推荐卡、价格层级和直接下单") {
                    Text("当前套餐统一支持钱包直付和订阅更新。", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
                }
            }
            items(state.plans) { plan -> PlanCard(plan = plan, onSelect = { onSelectPlan(plan.id) }) }
        }
    }
}
