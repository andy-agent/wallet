package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.VpnNodeCacheEntity

@Dao
interface VpnNodeCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(nodes: List<VpnNodeCacheEntity>)

    @Query("SELECT * FROM vpn_node_cache WHERE userId = :userId ORDER BY regionName ASC, lineName ASC, nodeName ASC")
    suspend fun getAllByUserId(userId: String): List<VpnNodeCacheEntity>

    @Query("SELECT * FROM vpn_node_cache WHERE userId = :userId AND lineCode = :lineCode ORDER BY nodeName ASC")
    suspend fun getAllByUserIdAndLineCode(userId: String, lineCode: String): List<VpnNodeCacheEntity>

    @Query("DELETE FROM vpn_node_cache WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM vpn_node_cache")
    suspend fun deleteAll()

    @Query("DELETE FROM vpn_node_cache WHERE userId = :userId AND nodeId NOT IN (:nodeIds)")
    suspend fun deleteMissingByUserId(userId: String, nodeIds: List<String>)
}
