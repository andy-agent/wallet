package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index

@Entity(
    tableName = "vpn_node_cache",
    primaryKeys = ["userId", "nodeId"],
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["userId"],
            childColumns = ["userId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lineCode"]),
        Index(value = ["regionCode"]),
    ]
)
data class VpnNodeCacheEntity(
    val userId: String,
    val nodeId: String,
    val nodeName: String,
    val lineCode: String,
    val lineName: String,
    val regionCode: String,
    val regionName: String,
    val host: String,
    val port: Int,
    val status: String,
    val source: String,
    val remark: String? = null,
    val updatedAt: Long,
)
