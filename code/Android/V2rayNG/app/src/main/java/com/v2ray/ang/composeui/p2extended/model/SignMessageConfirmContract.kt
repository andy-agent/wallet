package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class SignMessageConfirmRouteArgs(val requestId: String = "req_001")

data class SignMessageConfirmUiState(
        val title: String = "签名确认",
        val subtitle: String = "SIGN MESSAGE CONFIRM",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "签名确认页展示 DApp 请求、链、金额与风险提示。",
        val primaryActionLabel: String? = "确认签名",
        val secondaryActionLabel: String? = "拒绝并返回",
        val heroAccent: String = "sign_message_confirm",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "请求来源", value = "Jupiter"),
    FeatureMetric(label = "所在链", value = "Solana"),
    FeatureMetric(label = "请求类型", value = "Swap Exact In"),
),
        val fields: List<FeatureField> = emptyList(),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "签名确认页展示 DApp 请求、链、金额与风险提示。", trailing = "sign_message_confirm", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "requestId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "当前页面以信息展示与确认动作为主", trailing = "0 项", badge = "Info"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "签名确认 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 SignMessageConfirmPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "签名确认 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface SignMessageConfirmEvent {
        data object Refresh : SignMessageConfirmEvent
        data object PrimaryActionClicked : SignMessageConfirmEvent
        data object SecondaryActionClicked : SignMessageConfirmEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : SignMessageConfirmEvent
    }

    val signMessageConfirmNavigation: RouteDefinition = CryptoVpnRouteSpec.signMessageConfirm

    fun signMessageConfirmPreviewState(): SignMessageConfirmUiState = SignMessageConfirmUiState()
