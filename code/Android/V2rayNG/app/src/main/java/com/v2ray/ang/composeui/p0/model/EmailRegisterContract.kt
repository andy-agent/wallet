package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class EmailRegisterUiState(
        val title: String = "创建你的账户",
        val subtitle: String = "EMAIL REGISTER",
        val badge: String = "P0 · CORE",
        val summary: String = "注册页补齐邮箱、密码与验证码输入，为后续登录与恢复流程打底。",
        val primaryActionLabel: String = "创建账户并进入",
        val secondaryActionLabel: String? = "已有账户？去登录",
        val heroAccent: String = "email_register",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "注册步骤", value = "3 / 4"),
    FeatureMetric(label = "密码规则", value = "最少 8 位"),
    FeatureMetric(label = "二次验证", value = "可选"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "email", label = "邮箱", value = "hello@cryptovpn.app", supportingText = "将作为登录与找回凭据"),
    FeatureField(key = "code", label = "验证码", value = "820151", supportingText = "默认演示验证码"),
    FeatureField(key = "password", label = "登录密码", value = "", supportingText = "需包含字母与数字"),
    FeatureField(key = "invite", label = "邀请码", value = "PARTT-SLOW", supportingText = "选填"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "注册页补齐邮箱、密码与验证码输入，为后续登录与恢复流程打底。", trailing = "email_register", badge = "P0"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "邮箱、验证码、登录密码、邀请码", trailing = "4 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "创建你的账户 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 EmailRegisterPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "创建你的账户 已按 P0 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface EmailRegisterEvent {
        data object Refresh : EmailRegisterEvent
        data object PrimaryActionClicked : EmailRegisterEvent
        data object SecondaryActionClicked : EmailRegisterEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : EmailRegisterEvent
    }

    val emailRegisterNavigation: RouteDefinition = CryptoVpnRouteSpec.emailRegister

    fun emailRegisterPreviewState(): EmailRegisterUiState = EmailRegisterUiState()
