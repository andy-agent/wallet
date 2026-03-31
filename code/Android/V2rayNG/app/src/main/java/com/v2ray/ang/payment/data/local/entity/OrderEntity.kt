package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 订单实体类
 */
@Entity(
    tableName = "orders",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["orderNo"], unique = true),
        Index(value = ["userId"])
    ]
)
data class OrderEntity(
    @PrimaryKey
    val orderNo: String,
    val planName: String,
    val planId: String,
    val amount: String,
    val assetCode: String,
    val status: String,
    val createdAt: Long,
    val paidAt: Long? = null,
    val fulfilledAt: Long? = null,
    val expiredAt: Long? = null,
    val subscriptionUrl: String? = null,
    val marzbanUsername: String? = null,
    val userId: String
)
