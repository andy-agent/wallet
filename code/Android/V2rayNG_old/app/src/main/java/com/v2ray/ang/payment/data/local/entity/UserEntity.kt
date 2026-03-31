package com.v2ray.ang.payment.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * 用户实体类
 */
@Entity(
    tableName = "users",
    indices = [Index(value = ["username"], unique = true)]
)
data class UserEntity(
    @PrimaryKey
    val userId: String,
    val username: String,
    val email: String?,
    val accessToken: String,
    val refreshToken: String?,
    val loginAt: Long = System.currentTimeMillis()
)
