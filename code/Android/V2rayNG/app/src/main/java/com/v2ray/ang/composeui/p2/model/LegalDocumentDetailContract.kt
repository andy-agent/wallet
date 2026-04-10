package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class LegalDocumentDetailRouteArgs(val documentId: String = "terms_of_service")

data class LegalDocumentDetailUiState(
        val title: String = "服务条款",
        val subtitle: String = "LEGAL DETAIL",
        val badge: String = "• v2025.04",
        val summary: String = "用于展示 Markdown 法务正文、版本时间与风险提示。",
        val primaryActionLabel: String = "返回文档列表",
        val secondaryActionLabel: String? = "完成阅读",
        val heroAccent: String = "legal_document_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "版本", value = "v2025.04"),
    FeatureMetric(label = "生效日期", value = "2025-04-01"),
    FeatureMetric(label = "文档", value = "服务条款"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "1. 服务范围", subtitle = "CryptoVPN 提供基于订阅的 VPN 网络接入服务，以及配套的非托管钱包支付能力。", trailing = ""),
    FeatureListItem(title = "2. 付款与退款", subtitle = "链上支付一经广播不可撤回；若出现技术故障，平台按退款政策处理。", trailing = ""),
    FeatureListItem(title = "3. 钱包责任", subtitle = "助记词与私钥由用户自持；如遗失，平台无法帮助恢复资产。", trailing = ""),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "章节", detail = "1. 服务范围"),
    FeatureBullet(title = "章节", detail = "2. 付款与退款"),
    FeatureBullet(title = "章节", detail = "3. 钱包责任"),
    FeatureBullet(title = "生效", detail = "2025-04-01"),
),
        val note: String = "生效日期：2025-04-01",
    )

    sealed interface LegalDocumentDetailEvent {
        data object Refresh : LegalDocumentDetailEvent
        data object PrimaryActionClicked : LegalDocumentDetailEvent
        data object SecondaryActionClicked : LegalDocumentDetailEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : LegalDocumentDetailEvent
    }

    val legalDocumentDetailNavigation: RouteDefinition = CryptoVpnRouteSpec.legalDocumentDetail

    fun legalDocumentDetailPreviewState(): LegalDocumentDetailUiState = LegalDocumentDetailUiState()
