package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_token_icon_cache",
    indices = [
        Index(value = ["updatedAt"]),
        Index(value = ["lastFetchSucceeded"]),
    ],
)
data class LocalTokenIconCacheEntity(
    @PrimaryKey
    val tokenKey: String,
    val iconUrl: String?,
    val localPath: String?,
    val updatedAt: Long,
    val lastFetchSucceeded: Boolean,
)
