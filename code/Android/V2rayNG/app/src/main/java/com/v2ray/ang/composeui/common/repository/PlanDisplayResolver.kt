package com.v2ray.ang.composeui.common.repository

import com.v2ray.ang.payment.data.api.CurrentSubscriptionData
import com.v2ray.ang.payment.data.local.entity.OrderEntity
import com.v2ray.ang.payment.data.model.Plan

internal data class CurrentPlanStatusUi(
    val planName: String,
    val planDescription: String,
    val statusText: String,
)

private const val LEGACY_BASIC_PLAN_NAME = "真实基础线路套餐"
internal const val PLAN_NOT_PURCHASED_TEXT = "套餐尚未购买"

internal fun normalizePlanDisplayName(name: String): String {
    return when (name.trim()) {
        LEGACY_BASIC_PLAN_NAME -> "基础线路套餐"
        else -> name.trim()
    }
}

internal fun resolveCurrentPlanStatus(
    subscription: CurrentSubscriptionData?,
    plans: List<Plan>,
    cachedOrders: List<OrderEntity> = emptyList(),
): CurrentPlanStatusUi {
    if (subscription == null) {
        return CurrentPlanStatusUi(
            planName = PLAN_NOT_PURCHASED_TEXT,
            planDescription = "购买后将在这里显示当前计划",
            statusText = "尚未购买",
        )
    }

    val matchedPlan = subscription.planCode?.let { code ->
        plans.firstOrNull { it.planCode == code }
    }
    val matchedOrder = subscription.planCode?.let { code ->
        cachedOrders.lastOrNull { it.planId == code }
    }
    val resolvedPlanName = matchedPlan?.name
        ?: subscription.planName
        ?: matchedOrder?.planName
        ?: subscription.planCode
        ?: PLAN_NOT_PURCHASED_TEXT
    val resolvedPlanDescription = matchedPlan?.description
        ?.takeIf { it.isNotBlank() }
        ?: "当前已购套餐"

    return CurrentPlanStatusUi(
        planName = normalizePlanDisplayName(resolvedPlanName),
        planDescription = resolvedPlanDescription,
        statusText = subscription.daysRemaining?.let { "剩余 $it 天" }
            ?: subscriptionStatusText(subscription.status),
    )
}

private fun subscriptionStatusText(status: String): String {
    return when (status.uppercase()) {
        "ACTIVE" -> "已开通"
        "EXPIRED" -> "已过期"
        "CANCELED", "CANCELLED" -> "已取消"
        else -> "已开通"
    }
}
