package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.VpnNodeRuntimeEntity

@Dao
interface VpnNodeRuntimeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<VpnNodeRuntimeEntity>)

    @Query("SELECT * FROM vpn_node_runtime WHERE userId = :userId")
    suspend fun getAllByUserId(userId: String): List<VpnNodeRuntimeEntity>

    @Query("DELETE FROM vpn_node_runtime WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM vpn_node_runtime")
    suspend fun deleteAll()

    @Query("DELETE FROM vpn_node_runtime WHERE userId = :userId AND nodeId NOT IN (:nodeIds)")
    suspend fun deleteMissingByUserId(userId: String, nodeIds: List<String>)

    @Query("UPDATE vpn_node_runtime SET selected = CASE WHEN nodeId = :nodeId THEN 1 ELSE 0 END WHERE userId = :userId")
    suspend fun markSelected(userId: String, nodeId: String)
}
