package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.LocalTokenVisibilityEntryEntity

@Dao
interface LocalTokenVisibilityEntryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: LocalTokenVisibilityEntryEntity)

    @Query(
        """
        SELECT * FROM local_token_visibility_entries
        WHERE userId = :userId AND walletId = :walletId AND chainId = :chainId
        ORDER BY updatedAt DESC
        """,
    )
    suspend fun getByScope(
        userId: String,
        walletId: String,
        chainId: String,
    ): List<LocalTokenVisibilityEntryEntity>

    @Query(
        """
        DELETE FROM local_token_visibility_entries
        WHERE userId = :userId AND walletId = :walletId AND chainId = :chainId AND tokenKey = :tokenKey
        """,
    )
    suspend fun deleteByTokenKey(
        userId: String,
        walletId: String,
        chainId: String,
        tokenKey: String,
    )

    @Query("DELETE FROM local_token_visibility_entries WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM local_token_visibility_entries")
    suspend fun deleteAll()
}
