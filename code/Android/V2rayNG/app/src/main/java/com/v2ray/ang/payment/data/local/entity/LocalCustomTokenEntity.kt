package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "local_custom_tokens",
    primaryKeys = ["userId", "customTokenId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["walletId"]),
        Index(value = ["chainId"]),
        Index(value = ["tokenKey"]),
    ],
)
data class LocalCustomTokenEntity(
    val userId: String,
    val customTokenId: String,
    val walletId: String,
    val chainId: String,
    val tokenAddress: String,
    val tokenKey: String,
    val name: String,
    val symbol: String,
    val decimals: Int,
    val iconUrl: String?,
    val createdAt: String,
    val updatedAt: String,
    val cachedAt: Long,
)
