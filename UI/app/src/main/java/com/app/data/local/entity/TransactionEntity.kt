package com.app.data.local.entity

import com.app.data.model.Transaction
import com.app.data.model.TransactionDirection
import com.app.data.model.TransactionStatus

data class TransactionEntity(
    val id: String,
    val symbol: String,
    val chainName: String,
    val amount: Double,
    val fiatValue: Double,
    val direction: String,
    val status: String,
    val timestamp: Long,
    val address: String,
    val hash: String,
)

fun TransactionEntity.toModel() = Transaction(id, symbol, chainName, amount, fiatValue, TransactionDirection.valueOf(direction), TransactionStatus.valueOf(status), timestamp, address, hash)
fun Transaction.toEntity() = TransactionEntity(id, symbol, chainName, amount, fiatValue, direction.name, status.name, timestamp, address, hash)
