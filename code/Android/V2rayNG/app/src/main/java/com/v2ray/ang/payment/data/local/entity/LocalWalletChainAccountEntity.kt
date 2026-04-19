package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "local_wallet_chain_accounts",
    primaryKeys = ["walletId", "chainAccountId"],
    foreignKeys = [
        ForeignKey(
            entity = LocalWalletEntity::class,
            parentColumns = ["walletId"],
            childColumns = ["walletId"],
            onDelete = ForeignKey.CASCADE,
        ),
    ],
    indices = [
        Index(value = ["walletId"]),
        Index(value = ["userId"]),
        Index(value = ["networkCode"]),
    ],
)
data class LocalWalletChainAccountEntity(
    val walletId: String,
    val chainAccountId: String,
    val userId: String,
    val keySlotId: String?,
    val chainFamily: String,
    val networkCode: String,
    val address: String,
    val capability: String,
    val isEnabled: Boolean,
    val isDefaultReceive: Boolean,
    val updatedAt: Long,
)
