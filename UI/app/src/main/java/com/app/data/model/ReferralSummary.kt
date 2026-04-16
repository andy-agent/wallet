package com.app.data.model

data class ReferralSummary(
    val inviteCode: String,
    val invitedUsers: Int,
    val paidUsers: Int,
    val totalCommissionUsd: Double,
    val withdrawableUsd: Double,
)
