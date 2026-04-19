package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.LocalWalletEntity

@Dao
interface LocalWalletDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LocalWalletEntity>)

    @Query("SELECT * FROM local_wallets WHERE userId = :userId ORDER BY isDefault DESC, walletName ASC")
    suspend fun getByUserId(userId: String): List<LocalWalletEntity>

    @Query("SELECT * FROM local_wallets WHERE walletId = :walletId LIMIT 1")
    suspend fun getByWalletId(walletId: String): LocalWalletEntity?

    @Query(
        """
        UPDATE local_wallets
        SET isDefault = CASE WHEN walletId = :walletId THEN 1 ELSE 0 END
        WHERE userId = :userId
        """,
    )
    suspend fun setDefaultWallet(userId: String, walletId: String)

    @Query("DELETE FROM local_wallets WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM local_wallets")
    suspend fun deleteAll()
}
