package com.app.data.local.dao

import com.app.data.local.entity.VpnNodeEntity
import kotlinx.coroutines.flow.StateFlow

interface VpnNodeDao {
    val items: StateFlow<List<VpnNodeEntity>>
    fun replaceAll(items: List<VpnNodeEntity>)
    fun find(id: String): VpnNodeEntity?
}
