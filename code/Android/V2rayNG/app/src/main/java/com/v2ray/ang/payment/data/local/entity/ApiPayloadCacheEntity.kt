package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "api_payload_cache",
    primaryKeys = ["cacheKey"],
    indices = [Index(value = ["userId"]), Index(value = ["updatedAt"])],
)
data class ApiPayloadCacheEntity(
    val cacheKey: String,
    val userId: String? = null,
    val payloadJson: String,
    val updatedAt: Long,
)
