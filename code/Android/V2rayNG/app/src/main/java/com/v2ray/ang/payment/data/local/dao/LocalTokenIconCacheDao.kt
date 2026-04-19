package com.v2ray.ang.payment.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.v2ray.ang.payment.data.local.entity.LocalTokenIconCacheEntity

@Dao
interface LocalTokenIconCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(item: LocalTokenIconCacheEntity)

    @Query("SELECT * FROM local_token_icon_cache WHERE tokenKey = :tokenKey LIMIT 1")
    suspend fun getByTokenKey(tokenKey: String): LocalTokenIconCacheEntity?

    @Query("DELETE FROM local_token_icon_cache WHERE tokenKey = :tokenKey")
    suspend fun deleteByTokenKey(tokenKey: String)

    @Query("DELETE FROM local_token_icon_cache")
    suspend fun deleteAll()
}
