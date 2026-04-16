package com.app.data.remote.dto

import com.app.data.model.Order
import com.app.data.model.OrderStatus

data class OrderDto(
    val id: String,
    val planId: String,
    val planName: String,
    val amountUsd: Double,
    val status: String,
    val paySymbol: String,
    val createdAt: Long,
    val activatedAt: Long?,
)

fun OrderDto.toModel() = Order(id, planId, planName, amountUsd, OrderStatus.valueOf(status), paySymbol, createdAt, activatedAt)
