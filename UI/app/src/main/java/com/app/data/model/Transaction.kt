package com.app.data.model

enum class TransactionDirection { Send, Receive, Swap, Bridge, Payment }
enum class TransactionStatus { Pending, Confirmed, Failed }

data class Transaction(
    val id: String,
    val symbol: String,
    val chainName: String,
    val amount: Double,
    val fiatValue: Double,
    val direction: TransactionDirection,
    val status: TransactionStatus,
    val timestamp: Long,
    val address: String,
    val hash: String,
)
