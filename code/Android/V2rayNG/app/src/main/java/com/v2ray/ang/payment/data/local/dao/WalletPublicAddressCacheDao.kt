package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.WalletPublicAddressCacheEntity

@Dao
interface WalletPublicAddressCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<WalletPublicAddressCacheEntity>)

    @Query(
        """
        SELECT * FROM wallet_public_address_cache
        WHERE userId = :userId
          AND (:networkCode IS NULL OR networkCode = :networkCode)
          AND (:assetCode IS NULL OR assetCode = :assetCode)
        ORDER BY isDefault DESC, createdAt ASC
        """
    )
    suspend fun getByUserId(
        userId: String,
        networkCode: String? = null,
        assetCode: String? = null,
    ): List<WalletPublicAddressCacheEntity>

    @Query(
        """
        DELETE FROM wallet_public_address_cache
        WHERE userId = :userId
          AND networkCode = :networkCode
          AND assetCode = :assetCode
          AND addressId NOT IN (:addressIds)
        """
    )
    suspend fun deleteMissingByScope(
        userId: String,
        networkCode: String,
        assetCode: String,
        addressIds: List<String>,
    )

    @Query(
        """
        DELETE FROM wallet_public_address_cache
        WHERE userId = :userId
          AND networkCode = :networkCode
          AND assetCode = :assetCode
        """
    )
    suspend fun deleteByScope(
        userId: String,
        networkCode: String,
        assetCode: String,
    )

    @Query("DELETE FROM wallet_public_address_cache WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM wallet_public_address_cache")
    suspend fun deleteAll()
}
