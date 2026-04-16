package com.app.data.model

enum class CommissionStatus { Pending, Settled, Withdrawn }

data class CommissionRecord(
    val id: String,
    val sourceTitle: String,
    val amountUsd: Double,
    val status: CommissionStatus,
    val timestamp: Long,
)
