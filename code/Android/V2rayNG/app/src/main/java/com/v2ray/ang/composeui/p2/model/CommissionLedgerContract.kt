package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class CommissionLedgerUiState(
        val title: String = "佣金账本",
        val subtitle: String = "COMMISSION LEDGER",
        val badge: String = "• 本月 +$820",
        val summary: String = "查看佣金趋势、来源明细和已结算 / 待结算金额。",
        val primaryActionLabel: String? = "去提现",
        val secondaryActionLabel: String? = "返回邀请中心",
        val heroAccent: String = "commission_ledger",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "累计佣金", value = "$3,481.22"),
    FeatureMetric(label = "走势", value = "收入趋势"),
    FeatureMetric(label = "账目", value = "已结算 / 待结算"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "东京 Pro 年费转化", subtitle = "节点升级与续费分成。", trailing = "+$37.25"),
    FeatureListItem(title = "新加坡月费转化", subtitle = "多链钱包高级功能开通返佣。", trailing = "+$2.20"),
    FeatureListItem(title = "年费 Pro 二级佣金", subtitle = "等待用户订单完成后的返佣。", trailing = "+$7.45"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "收入走势", detail = "查看每一笔订阅转化、结算状态与链上提现记录。"),
    FeatureBullet(title = "结算维度", detail = "按订单来源拆分返佣。"),
    FeatureBullet(title = "转化明细", detail = "支持结合邀请链路追踪。"),
    FeatureBullet(title = "提现联动", detail = "佣金可直接进入提现申请。"),
),
        val note: String = "收入走势",
        val isLoading: Boolean = false,
        val errorMessage: String? = null,
        val emptyMessage: String? = null,
        val blockerTitle: String? = null,
        val blockerMessage: String? = null,
    )

    sealed interface CommissionLedgerEvent {
        data object Refresh : CommissionLedgerEvent
        data object PrimaryActionClicked : CommissionLedgerEvent
        data object SecondaryActionClicked : CommissionLedgerEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : CommissionLedgerEvent
    }

    val commissionLedgerNavigation: RouteDefinition = CryptoVpnRouteSpec.commissionLedger

    fun commissionLedgerPreviewState(): CommissionLedgerUiState = CommissionLedgerUiState()
