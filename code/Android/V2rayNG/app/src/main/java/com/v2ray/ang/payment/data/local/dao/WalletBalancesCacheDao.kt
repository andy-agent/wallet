package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.WalletBalancesCacheEntity

@Dao
interface WalletBalancesCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: WalletBalancesCacheEntity)

    @Query(
        """
        SELECT * FROM wallet_balances_cache
        WHERE userId = :userId AND walletId = :walletId
        LIMIT 1
        """
    )
    suspend fun getByUserIdAndWalletId(
        userId: String,
        walletId: String,
    ): WalletBalancesCacheEntity?

    @Query(
        """
        SELECT * FROM wallet_balances_cache
        WHERE userId = :userId
        ORDER BY updatedAt DESC
        LIMIT 1
        """
    )
    suspend fun getLatestByUserId(userId: String): WalletBalancesCacheEntity?

    @Query("DELETE FROM wallet_balances_cache WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM wallet_balances_cache")
    suspend fun deleteAll()
}
