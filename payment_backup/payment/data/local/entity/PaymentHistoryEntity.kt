package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 支付历史实体类
 */
@Entity(
    tableName = "payment_history",
    indices = [Index(value = ["orderNo"])]
)
data class PaymentHistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNo: String,
    val amount: String,
    val assetCode: String,
    val txHash: String? = null,
    val paidAt: Long = System.currentTimeMillis()
)
