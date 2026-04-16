package com.app.data.local.dao

import com.app.data.local.entity.OrderEntity
import kotlinx.coroutines.flow.StateFlow

interface OrderDao {
    val items: StateFlow<List<OrderEntity>>
    fun replaceAll(items: List<OrderEntity>)
    fun upsert(item: OrderEntity)
    fun find(id: String): OrderEntity?
}
