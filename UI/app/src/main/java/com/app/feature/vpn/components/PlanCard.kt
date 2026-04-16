package com.app.feature.vpn.components

import androidx.compose.runtime.Composable
import com.app.common.components.GradientCard
import com.app.common.components.InfoRow
import com.app.common.components.StatusChip
import com.app.data.model.Plan
import com.app.core.utils.Formatters

@Composable
fun PlanCard(plan: Plan, onSelect: () -> Unit) {
    GradientCard(title = plan.title, subtitle = plan.durationLabel) {
        if (plan.recommended) StatusChip("推荐")
        InfoRow("价格", Formatters.money(plan.priceUsd))
        InfoRow("流量", "${plan.trafficGb} GB")
        InfoRow("设备数", plan.deviceLimit.toString())
        androidx.compose.material3.TextButton(onClick = onSelect) { androidx.compose.material3.Text("立即购买") }
    }
}
