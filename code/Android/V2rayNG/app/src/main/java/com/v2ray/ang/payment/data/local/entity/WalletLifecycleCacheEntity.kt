package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "wallet_lifecycle_cache",
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
data class WalletLifecycleCacheEntity(
    val userId: String,
    val accountId: String,
    val walletExists: Boolean,
    val receiveState: String,
    val lifecycleStatus: String,
    val sourceType: String?,
    val walletId: String?,
    val displayName: String?,
    val status: String?,
    val origin: String?,
    val nextAction: String?,
    val walletName: String?,
    val configuredAddressCount: Int,
    val createdAt: String?,
    val remoteUpdatedAt: String?,
    val backupAcknowledgedAt: String?,
    val activatedAt: String?,
    val updatedAt: Long,
)
