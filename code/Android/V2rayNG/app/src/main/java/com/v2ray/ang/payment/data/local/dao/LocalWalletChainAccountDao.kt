package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.LocalWalletChainAccountEntity

@Dao
interface LocalWalletChainAccountDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<LocalWalletChainAccountEntity>)

    @Query("SELECT * FROM local_wallet_chain_accounts WHERE walletId = :walletId ORDER BY networkCode ASC")
    suspend fun getByWalletId(walletId: String): List<LocalWalletChainAccountEntity>

    @Query("SELECT * FROM local_wallet_chain_accounts WHERE userId = :userId ORDER BY networkCode ASC")
    suspend fun getByUserId(userId: String): List<LocalWalletChainAccountEntity>

    @Query("DELETE FROM local_wallet_chain_accounts WHERE walletId = :walletId")
    suspend fun deleteByWalletId(walletId: String)

    @Query("DELETE FROM local_wallet_chain_accounts WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM local_wallet_chain_accounts")
    suspend fun deleteAll()
}
