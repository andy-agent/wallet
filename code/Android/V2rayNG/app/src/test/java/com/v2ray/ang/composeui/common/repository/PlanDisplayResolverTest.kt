package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.model.Plan
import org.junit.Assert.assertEquals
import org.junit.Test

class PlanDisplayResolverTest {

    @Test
    fun `normalize plan display name removes legacy real prefix`() {
        assertEquals("基础线路套餐", normalizePlanDisplayName("真实基础线路套餐"))
    }

    @Test
    fun `resolve current plan status uses configured plan name when subscription exists`() {
        val status = resolveCurrentPlanStatus(
            subscription = subscription(planCode = "BASIC_1M", daysRemaining = 30),
            plans = listOf(plan(planCode = "BASIC_1M", name = "真实基础线路套餐")),
        )

        assertEquals("基础线路套餐", status.planName)
        assertEquals("支持日本基础线路", status.planDescription)
        assertEquals("剩余 30 天", status.statusText)
    }

    @Test
    fun `resolve current plan status falls back to cached order name when plan config missing`() {
        val status = resolveCurrentPlanStatus(
            subscription = subscription(planCode = "BASIC_1M"),
            plans = emptyList(),
            cachedOrders = listOf(order(planId = "BASIC_1M", planName = "真实基础线路套餐")),
        )

        assertEquals("基础线路套餐", status.planName)
        assertEquals("当前已购套餐", status.planDescription)
        assertEquals("已开通", status.statusText)
    }

    @Test
    fun `resolve current plan status returns not purchased copy when subscription missing`() {
        val status = resolveCurrentPlanStatus(
            subscription = null,
            plans = listOf(plan()),
        )

        assertEquals("套餐尚未购买", status.planName)
        assertEquals("购买后将在这里显示当前计划", status.planDescription)
        assertEquals("尚未购买", status.statusText)
    }

    private fun subscription(
        planCode: String? = "BASIC_1M",
        daysRemaining: Int? = null,
        status: String = "ACTIVE",
    ) = CurrentSubscriptionData(
        subscriptionId = "sub_123",
        planCode = planCode,
        status = status,
        startedAt = "2026-04-01T00:00:00Z",
        expireAt = "2026-05-01T00:00:00Z",
        daysRemaining = daysRemaining,
        isUnlimitedTraffic = true,
        maxActiveSessions = 1,
        subscriptionUrl = null,
        marzbanUsername = null,
    )

    private fun plan(
        planCode: String = "BASIC_1M",
        name: String = "基础线路套餐",
    ) = Plan(
        id = "plan_123",
        planCode = planCode,
        name = name,
        description = "支持日本基础线路",
        billingCycleMonths = 1,
        priceUsd = "3.00",
        maxActiveSessions = 1,
        regionAccessPolicy = "BASIC_ONLY",
        includesAdvancedRegions = false,
        allowedRegionIds = emptyList(),
        displayOrder = 1,
        status = "ACTIVE",
    )

    private fun order(
        planId: String = "BASIC_1M",
        planName: String = "基础线路套餐",
    ) = OrderEntity(
        orderNo = "order_123",
        planName = planName,
        planId = planId,
        amount = "3.00",
        usdAmount = "3.00",
        assetCode = "USDT",
        networkCode = "TRON",
        status = "COMPLETED",
        createdAt = 0L,
        paidAt = 0L,
        fulfilledAt = 0L,
        expiredAt = null,
        subscriptionUrl = null,
        marzbanUsername = null,
        userId = "user_123",
    )
}
