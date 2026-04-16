package com.app.data.local.dao

import com.app.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.StateFlow

interface TransactionDao {
    val items: StateFlow<List<TransactionEntity>>
    fun replaceAll(items: List<TransactionEntity>)
    fun prepend(item: TransactionEntity)
    fun find(id: String): TransactionEntity?
}
