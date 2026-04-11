package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class SignMessageConfirmRouteArgs(val requestId: String = "req_001")

data class SignMessageConfirmUiState(
    val title: String = "签名确认",
    val subtitle: String = "SIGN MESSAGE CONFIRM",
    val badge: String = "同步中",
    val summary: String = "正在读取签名请求上下文；若未接真实请求源和批准动作，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "sign_message_confirm",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "请求标识", value = "--"),
        FeatureMetric(label = "账户状态", value = "读取中"),
        FeatureMetric(label = "风险校验", value = "读取中"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在读取签名请求", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示来源、金额和确认签名假动作。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
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
