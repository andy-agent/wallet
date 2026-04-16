package com.app.data.remote.dto

import com.app.data.model.ReferralSummary

data class ReferralDto(
    val inviteCode: String,
    val invitedUsers: Int,
    val paidUsers: Int,
    val totalCommissionUsd: Double,
    val withdrawableUsd: Double,
)

fun ReferralDto.toModel() = ReferralSummary(inviteCode, invitedUsers, paidUsers, totalCommissionUsd, withdrawableUsd)
