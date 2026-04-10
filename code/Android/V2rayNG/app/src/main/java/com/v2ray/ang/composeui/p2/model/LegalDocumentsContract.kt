package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class LegalDocumentsUiState(
        val title: String = "法务文档",
        val subtitle: String = "LEGAL DOCUMENTS",
        val badge: String = "P2 · BASE",
        val summary: String = "法务文档列表页提供服务协议、隐私政策与风险披露等入口。",
        val primaryActionLabel: String = "查看服务协议",
        val secondaryActionLabel: String? = "返回个人中心",
        val heroAccent: String = "legal_documents",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "文档总数", value = "6"),
    FeatureMetric(label = "最近更新", value = "2025-04"),
    FeatureMetric(label = "语言", value = "EN / CN"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "search", label = "搜索文档", value = "服务 / 风险 / 隐私", supportingText = "可按标题关键字检索"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "法务文档列表页提供服务协议、隐私政策与风险披露等入口。", trailing = "legal_documents", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "搜索文档", trailing = "1 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "法务文档 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 LegalDocumentsPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "法务文档 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
