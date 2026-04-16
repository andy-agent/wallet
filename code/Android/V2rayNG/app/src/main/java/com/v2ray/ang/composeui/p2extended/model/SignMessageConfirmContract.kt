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
        val summary: String = "签名确认页仅展示真实签名请求与风险提示，不再预置固定请求。",
        val primaryActionLabel: String = "确认签名",
        val secondaryActionLabel: String? = "拒绝并返回",
        val heroAccent: String = "sign_message_confirm",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "请求来源", value = "待接入"),
    FeatureMetric(label = "所在链", value = "待接入"),
    FeatureMetric(label = "请求类型", value = "待接入"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "domain", label = "来源域名", value = "", supportingText = "仅展示真实会话来源"),
    FeatureField(key = "payload", label = "签名摘要", value = "", supportingText = "仅展示真实请求摘要"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "请求上下文", subtitle = "requestId 用于读取真实签名请求。", trailing = "1 个", badge = "参数"),
    FeatureListItem(title = "风险来源", subtitle = "来源域名与授权范围需真实返回。", trailing = "待接入", badge = "风控"),
    FeatureListItem(title = "确认动作", subtitle = "签名前必须核对真实摘要与权限。", trailing = "必校验", badge = "签名"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "来源校验", detail = "不再展示固定 DApp 名称。"),
    FeatureBullet(title = "摘要核对", detail = "仅使用真实 payload，未接入时显示待接入。"),
    FeatureBullet(title = "权限审计", detail = "授权范围与失效时间待真实会话返回。"),
),
        val note: String = "当前不再预置任何固定签名请求；仅展示真实请求或待接入提示。",
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
