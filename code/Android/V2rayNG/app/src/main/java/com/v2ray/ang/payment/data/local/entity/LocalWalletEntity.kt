package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "local_wallets",
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
        Index(value = ["userId", "isDefault"]),
    ],
)
data class LocalWalletEntity(
    @PrimaryKey
    val walletId: String,
    val userId: String,
    val walletName: String,
    val walletKind: String,
    val sourceType: String,
    val isDefault: Boolean,
    val isArchived: Boolean,
    val updatedAt: Long,
)
