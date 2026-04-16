package com.app.data.remote.mock

import com.app.data.model.CommissionRecord
import com.app.data.model.CommissionStatus
import com.app.data.model.OrderStatus
import com.app.data.remote.api.VpnApi
import com.app.data.remote.dto.OrderDto
import com.app.data.remote.dto.PlanDto
import com.app.data.remote.dto.ReferralDto
import com.app.data.remote.dto.SubscriptionDto
import com.app.core.utils.Constants

class MockVpnDataSource : VpnApi {
    override suspend fun getPlans(): List<PlanDto> = listOf(
        PlanDto("plan_month", "月付基础", "30 天", 8.9, 300, 2, false),
        PlanDto("plan_quarter", "季度旗舰", "90 天", 24.9, 1200, 3, true),
        PlanDto("plan_year", "年度畅连", "365 天", 58.0, 6000, 5, false),
    )

    override suspend fun getOrders(): List<OrderDto> = listOf(
        OrderDto("ord_01", "plan_quarter", "季度旗舰", 24.9, OrderStatus.Active.name, "USDT", System.currentTimeMillis() - 86_400_000L, System.currentTimeMillis() - 85_000_000L),
        OrderDto("ord_02", "plan_month", "月付基础", 8.9, OrderStatus.Paid.name, "SOL", System.currentTimeMillis() - 3_600_000L, System.currentTimeMillis() - 3_000_000L),
    )

    override suspend fun getSubscription(): SubscriptionDto = SubscriptionDto(
        "sub_01",
        "mock://subscription/vpn01-main",
        System.currentTimeMillis() - 3_600_000L,
        System.currentTimeMillis() + 29L * 24 * 60 * 60 * 1000,
        124.0,
        1024.0,
        true,
    )

    override suspend fun getReferral(): ReferralDto = ReferralDto("VPN01-88A", 48, 13, 580.0, 320.0)

    fun commissions(): List<CommissionRecord> = listOf(
        CommissionRecord("c1", "季度旗舰邀请返佣", 88.0, CommissionStatus.Settled, System.currentTimeMillis() - 7_200_000L),
        CommissionRecord("c2", "月付基础邀请返佣", 12.0, CommissionStatus.Pending, System.currentTimeMillis() - 36_000_000L),
        CommissionRecord("c3", "年度畅连提现", 150.0, CommissionStatus.Withdrawn, System.currentTimeMillis() - 120_000_000L),
    )

    fun subscriptionPayload(): String = Constants.MOCK_SUBSCRIPTION_PAYLOAD
}
