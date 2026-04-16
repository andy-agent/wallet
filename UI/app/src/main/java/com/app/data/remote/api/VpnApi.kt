package com.app.data.remote.api

import com.app.data.remote.dto.OrderDto
import com.app.data.remote.dto.PlanDto
import com.app.data.remote.dto.ReferralDto
import com.app.data.remote.dto.SubscriptionDto

interface VpnApi {
    suspend fun getPlans(): List<PlanDto>
    suspend fun getOrders(): List<OrderDto>
    suspend fun getSubscription(): SubscriptionDto
    suspend fun getReferral(): ReferralDto
}
