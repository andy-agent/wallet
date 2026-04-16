package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.WalletOverviewCacheEntity

@Dao
interface WalletOverviewCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WalletOverviewCacheEntity)

    @Query("SELECT * FROM wallet_overview_cache WHERE userId = :userId LIMIT 1")
    suspend fun getByUserId(userId: String): WalletOverviewCacheEntity?

    @Query("DELETE FROM wallet_overview_cache WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM wallet_overview_cache")
    suspend fun deleteAll()
}
