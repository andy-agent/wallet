package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.LocalCustomTokenEntity

@Dao
interface LocalCustomTokenDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LocalCustomTokenEntity>)

    @Query(
        """
        SELECT * FROM local_custom_tokens
        WHERE userId = :userId AND walletId = :walletId AND chainId = :chainId
        ORDER BY symbol ASC, name ASC
        """,
    )
    suspend fun getByScope(
        userId: String,
        walletId: String,
        chainId: String,
    ): List<LocalCustomTokenEntity>

    @Query(
        """
        DELETE FROM local_custom_tokens
        WHERE userId = :userId AND walletId = :walletId AND chainId = :chainId
        """
    )
    suspend fun deleteByScope(
        userId: String,
        walletId: String,
        chainId: String,
    )

    @Query(
        """
        DELETE FROM local_custom_tokens
        WHERE userId = :userId AND customTokenId = :customTokenId
        """
    )
    suspend fun deleteByCustomTokenId(
        userId: String,
        customTokenId: String,
    )

    @Query("DELETE FROM local_custom_tokens WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM local_custom_tokens")
    suspend fun deleteAll()
}
