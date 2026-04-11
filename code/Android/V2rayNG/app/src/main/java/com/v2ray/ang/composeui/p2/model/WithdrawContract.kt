package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class WithdrawUiState(
        val title: String = "提现佣金",
        val subtitle: String = "WITHDRAW",
        val badge: String = "提现",
        val summary: String = "佣金提现页校验提现地址、金额与网络信息，再提交结算申请。",
        val primaryActionLabel: String? = "提交提现申请",
        val secondaryActionLabel: String? = "返回账本",
        val heroAccent: String = "withdraw",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "提现余额", value = "$580.00"),
    FeatureMetric(label = "到账数量", value = "579.00"),
    FeatureMetric(label = "网络手续费", value = "TRON · 预计 10 分钟内处理"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "amount", label = "提现金额", value = "$ 500.00", supportingText = "申请金额"),
    FeatureField(key = "address", label = "到账地址", value = "TQ2xP9v7m5aE2sH1cV4Z9Q6wBBLk3N5xY7", supportingText = "钱包地址"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "风控额度正常", subtitle = "本次提现金额未超过当日安全阈值。", trailing = "PASS"),
    FeatureListItem(title = "地址已验证", subtitle = "该地址已通过历史白名单校验。", trailing = "SAFE"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "提现余额", detail = "$580.00"),
    FeatureBullet(title = "到账数量", detail = "579.00"),
    FeatureBullet(title = "风控", detail = "PASS"),
    FeatureBullet(title = "地址校验", detail = "SAFE"),
),
        val note: String = "把已结算收益提到你的 TRON / Solana 自托管钱包。",
        val isLoading: Boolean = false,
        val isSubmitting: Boolean = false,
        val errorMessage: String? = null,
        val emptyMessage: String? = null,
        val blockerTitle: String? = null,
        val blockerMessage: String? = null,
        val feedbackMessage: String? = null,
    )

    sealed interface WithdrawEvent {
        data object Refresh : WithdrawEvent
        data object PrimaryActionClicked : WithdrawEvent
        data object SecondaryActionClicked : WithdrawEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : WithdrawEvent
    }

    val withdrawNavigation: RouteDefinition = CryptoVpnRouteSpec.withdraw

    fun withdrawPreviewState(): WithdrawUiState = WithdrawUiState()
