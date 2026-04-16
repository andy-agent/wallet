package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class LegalDocumentsUiState(
        val title: String = "法务文档",
        val subtitle: String = "LEGAL",
        val badge: String = "法务",
        val summary: String = "法务文档列表页提供服务协议、隐私政策与风险披露等入口。",
        val primaryActionLabel: String = "查看服务协议",
        val secondaryActionLabel: String? = "返回个人中心",
        val heroAccent: String = "legal_documents",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "协议", value = "服务协议"),
    FeatureMetric(label = "隐私", value = "隐私政策"),
    FeatureMetric(label = "风险", value = "VPN 服务说明"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "服务协议", subtitle = "最后更新 2025-04-01", trailing = "协议"),
    FeatureListItem(title = "隐私政策", subtitle = "最后更新 2025-04-01", trailing = "PDF"),
    FeatureListItem(title = "VPN 服务说明", subtitle = "节点、线路与可用性说明", trailing = "High"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "法务覆盖", detail = "服务协议、隐私与免责声明。"),
    FeatureBullet(title = "更新日期", detail = "2025-04-01"),
    FeatureBullet(title = "阅读入口", detail = "支持进入单文档详情页。"),
    FeatureBullet(title = "风险提示", detail = "链上支付免责声明在文档中统一维护。"),
),
        val note: String = "所有协议、隐私、风险说明与链上支付免责声明。",
        val banner: P2SurfaceBanner = p2ReadyBanner(),
        val feedbackMessage: String? = null,
    )

    sealed interface LegalDocumentsEvent {
        data object Refresh : LegalDocumentsEvent
        data object PrimaryActionClicked : LegalDocumentsEvent
        data object SecondaryActionClicked : LegalDocumentsEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : LegalDocumentsEvent
    }

    val legalDocumentsNavigation: RouteDefinition = CryptoVpnRouteSpec.legalDocuments

    fun legalDocumentsPreviewState(): LegalDocumentsUiState = LegalDocumentsUiState()
