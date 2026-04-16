package com.app.data.model

enum class OrderStatus { Pending, Paid, Active, Expired, Failed }

data class Order(
    val id: String,
    val planId: String,
    val planName: String,
    val amountUsd: Double,
    val status: OrderStatus,
    val paySymbol: String,
    val createdAt: Long,
    val activatedAt: Long? = null,
)
