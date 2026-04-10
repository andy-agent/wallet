package com.cryptovpn.ui.p2.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class LegalDocumentDetailRouteArgs(val documentId: String = "terms_of_service")

data class LegalDocumentDetailUiState(
        val title: String = "文档详情",
        val subtitle: String = "LEGAL DOCUMENT DETAIL",
        val badge: String = "P2 · BASE",
        val summary: String = "法务详情页展示选中文档的条款正文与确认操作。",
        val primaryActionLabel: String = "返回文档列表",
        val secondaryActionLabel: String? = "完成阅读",
        val heroAccent: String = "legal_document_detail",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "文档版本", value = "2025.04"),
    FeatureMetric(label = "当前章节", value = "5"),
    FeatureMetric(label = "发布状态", value = "Published"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "法务详情页展示选中文档的条款正文与确认操作。", trailing = "legal_document_detail", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "documentId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "文档详情 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 LegalDocumentDetailPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "文档详情 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
