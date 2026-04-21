package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wallet_balances_cache",
    primaryKeys = ["userId", "walletId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["userId"]), Index(value = ["walletId"])],
)
data class WalletBalancesCacheEntity(
    val userId: String,
    val walletId: String,
    val accountId: String,
    val accountEmail: String,
    val walletName: String?,
    val itemsJson: String,
    val priceUpdatedAt: String?,
    val updatedAt: Long,
)
