package com.app.feature.vpn.components

import androidx.compose.runtime.Composable
import com.app.common.components.StatusChip
import com.app.data.model.OrderStatus

@Composable
fun OrderStatusBadge(status: OrderStatus) {
    val positive = status == OrderStatus.Active || status == OrderStatus.Paid
    StatusChip(text = status.name, positive = positive)
}
