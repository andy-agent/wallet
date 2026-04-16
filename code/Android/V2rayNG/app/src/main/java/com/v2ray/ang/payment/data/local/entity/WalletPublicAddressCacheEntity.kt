package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wallet_public_address_cache",
    primaryKeys = ["userId", "addressId"],
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
        Index(value = ["networkCode"]),
        Index(value = ["assetCode"]),
    ],
)
data class WalletPublicAddressCacheEntity(
    val userId: String,
    val addressId: String,
    val accountId: String,
    val networkCode: String,
    val assetCode: String,
    val address: String,
    val isDefault: Boolean,
    val createdAt: String,
    val updatedAt: Long,
)
