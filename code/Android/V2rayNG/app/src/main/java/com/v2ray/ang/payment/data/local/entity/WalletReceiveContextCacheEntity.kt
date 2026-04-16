package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wallet_receive_context_cache",
    primaryKeys = ["userId", "requestNetworkCode", "requestAssetCode"],
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
        Index(value = ["selectedNetworkCode"]),
        Index(value = ["selectedAssetCode"]),
    ],
)
data class WalletReceiveContextCacheEntity(
    val userId: String,
    val requestNetworkCode: String,
    val requestAssetCode: String,
    val selectedNetworkCode: String,
    val selectedAssetCode: String,
    val chainItemsJson: String,
    val assetItemsJson: String,
    val defaultAddress: String?,
    val canShare: Boolean,
    val walletExists: Boolean,
    val receiveState: String?,
    val status: String,
    val note: String,
    val shareText: String?,
    val updatedAt: Long,
)
