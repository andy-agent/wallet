package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "vpn_node_runtime",
    primaryKeys = ["userId", "nodeId"],
    indices = [
        Index(value = ["userId"]),
        Index(value = ["lineCode"]),
    ]
)
data class VpnNodeRuntimeEntity(
    val userId: String,
    val nodeId: String,
    val lineCode: String,
    val healthStatus: String,
    val pingMs: Int? = null,
    val selected: Boolean = false,
    val lastSeenAt: Long,
)
