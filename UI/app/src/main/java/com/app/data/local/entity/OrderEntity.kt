package com.app.data.local.entity

import com.app.data.model.Order
import com.app.data.model.OrderStatus

data class OrderEntity(
    val id: String,
    val planId: String,
    val planName: String,
    val amountUsd: Double,
    val status: String,
    val paySymbol: String,
    val createdAt: Long,
    val activatedAt: Long?,
)

fun OrderEntity.toModel() = Order(id, planId, planName, amountUsd, OrderStatus.valueOf(status), paySymbol, createdAt, activatedAt)
fun Order.toEntity() = OrderEntity(id, planId, planName, amountUsd, status.name, paySymbol, createdAt, activatedAt)
