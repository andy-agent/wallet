package com.app.feature.vpn.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.StatusChip
import com.app.data.model.Plan
import com.app.core.utils.Formatters
import com.app.common.widgets.MetricPill

@Composable
fun PlanCard(plan: Plan, onSelect: () -> Unit) {
    GradientCard(title = plan.title, subtitle = plan.durationLabel) {
        if (plan.recommended) {
            StatusChip("推荐")
            Spacer(modifier = androidx.compose.ui.Modifier.height(8.dp))
        }
        androidx.compose.material3.Text(
            text = Formatters.money(plan.priceUsd),
            style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
        )
        Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
            MetricPill("流量", "${plan.trafficGb} GB")
            MetricPill("设备", "${plan.deviceLimit}")
        }
        Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
        InfoRow("计费周期", plan.durationLabel)
        Spacer(modifier = androidx.compose.ui.Modifier.height(12.dp))
        androidx.compose.material3.TextButton(onClick = onSelect) {
            androidx.compose.material3.Text("继续下单")
        }
    }
}
