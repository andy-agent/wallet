package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wallet_overview_cache",
    primaryKeys = ["userId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [Index(value = ["userId"])],
)
data class WalletOverviewCacheEntity(
    val userId: String,
    val accountId: String,
    val accountEmail: String,
    val selectedNetworkCode: String,
    val chainItemsJson: String,
    val assetItemsJson: String,
    val totalPortfolioValueUsd: String?,
    val priceUpdatedAt: String?,
    val alertsJson: String,
    val updatedAt: Long,
)
