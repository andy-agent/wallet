package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.WalletReceiveContextCacheEntity

@Dao
interface WalletReceiveContextCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WalletReceiveContextCacheEntity)

    @Query(
        """
        SELECT * FROM wallet_receive_context_cache
        WHERE userId = :userId
          AND requestNetworkCode = :requestNetworkCode
          AND requestAssetCode = :requestAssetCode
        LIMIT 1
        """
    )
    suspend fun get(
        userId: String,
        requestNetworkCode: String,
        requestAssetCode: String,
    ): WalletReceiveContextCacheEntity?

    @Query("DELETE FROM wallet_receive_context_cache WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM wallet_receive_context_cache")
    suspend fun deleteAll()
}
