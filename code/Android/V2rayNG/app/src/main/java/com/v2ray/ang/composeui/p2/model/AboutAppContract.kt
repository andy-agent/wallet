package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class AboutAppUiState(
    val title: String = "关于应用",
    val subtitle: String = "ABOUT APP",
    val badge: String = "最新",
    val summary: String = "版本、更新记录、支持入口与产品信息。",
    val primaryActionLabel: String = "查看更新记录",
    val secondaryActionLabel: String? = "帮助与支持",
    val heroAccent: String = "about_app",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "应用", value = "CryptoVPN"),
        FeatureMetric(label = "版本", value = "2.8.0"),
        FeatureMetric(label = "状态", value = "最新"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "CryptoVPN", subtitle = "版本 2.8.0", trailing = "最新"),
        FeatureListItem(title = "更新记录", subtitle = "本次更新包含节点稳定性、钱包安全与支付流程修复。", trailing = "查看"),
        FeatureListItem(title = "帮助与支持", subtitle = "提交问题、查看帮助文档与反馈。", trailing = "进入"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "版本", detail = "2.8.0"),
        FeatureBullet(title = "更新", detail = "节点稳定性、钱包安全与支付流程修复"),
        FeatureBullet(title = "支持", detail = "帮助文档与反馈入口"),
        FeatureBullet(title = "信息", detail = "关于应用"),
    ),
    val note: String = "版本、更新记录、支持入口与产品信息。",
)

sealed interface AboutAppEvent {
    data object Refresh : AboutAppEvent
    data object PrimaryActionClicked : AboutAppEvent
    data object SecondaryActionClicked : AboutAppEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : AboutAppEvent
}

fun aboutAppPreviewState(): AboutAppUiState = AboutAppUiState()
