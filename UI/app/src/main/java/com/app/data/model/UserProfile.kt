package com.app.data.model

data class UserProfile(
    val userId: String,
    val displayName: String,
    val email: String,
    val levelLabel: String,
    val walletCount: Int,
    val totalAssetsUsd: Double,
    val inviteCode: String,
)
