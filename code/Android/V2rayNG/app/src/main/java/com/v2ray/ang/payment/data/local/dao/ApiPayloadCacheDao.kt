package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.ApiPayloadCacheEntity

@Dao
interface ApiPayloadCacheDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: ApiPayloadCacheEntity)

    @Query(
        """
        SELECT * FROM api_payload_cache
        WHERE cacheKey = :cacheKey
        LIMIT 1
        """,
    )
    suspend fun getByCacheKey(cacheKey: String): ApiPayloadCacheEntity?

    @Query("DELETE FROM api_payload_cache WHERE userId = :userId")
    suspend fun deleteByUserId(userId: String)

    @Query("DELETE FROM api_payload_cache")
    suspend fun deleteAll()
}
