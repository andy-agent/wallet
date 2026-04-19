package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "local_token_visibility_entries",
    primaryKeys = ["userId", "walletId", "chainId", "tokenKey"],
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
        Index(value = ["visibilityState"]),
    ],
)
data class LocalTokenVisibilityEntryEntity(
    val userId: String,
    val walletId: String,
    val chainId: String,
    val tokenKey: String,
    val visibilityState: String,
    val updatedAt: Long,
)
