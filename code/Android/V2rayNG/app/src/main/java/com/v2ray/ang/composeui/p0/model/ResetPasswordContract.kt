package com.v2ray.ang.composeui.p0.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ResetPasswordUiState(
        val title: String = "重置密码",
        val subtitle: String = "RESET PASSWORD",
        val badge: String = "P0 · CORE",
        val summary: String = "通过邮箱验证码重置登录密码，恢复当前设备的访问权限。",
        val primaryActionLabel: String = "提交并重置密码",
        val secondaryActionLabel: String? = "返回登录",
        val heroAccent: String = "reset_password",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "验证码", value = "6 位"),
    FeatureMetric(label = "有效期", value = "10 分钟"),
    FeatureMetric(label = "设备限制", value = "2 台"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "email", label = "邮箱", value = "hello@cryptovpn.app", supportingText = "接收重置邮件"),
    FeatureField(key = "code", label = "验证码", value = "820151", supportingText = "输入邮箱收到的验证码"),
    FeatureField(key = "password", label = "新密码", value = "", supportingText = "重置后立即生效"),
    FeatureField(key = "confirm", label = "确认密码", value = "", supportingText = "保持两次输入一致"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "通过邮箱验证码重置登录密码，恢复当前设备的访问权限。", trailing = "reset_password", badge = "P0"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "邮箱、验证码、新密码、确认密码", trailing = "4 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "重置密码 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ResetPasswordPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "重置密码 已按 P0 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface ResetPasswordEvent {
        data object Refresh : ResetPasswordEvent
        data object PrimaryActionClicked : ResetPasswordEvent
        data object SecondaryActionClicked : ResetPasswordEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : ResetPasswordEvent
    }

    val resetPasswordNavigation: RouteDefinition = CryptoVpnRouteSpec.resetPassword

    fun resetPasswordPreviewState(): ResetPasswordUiState = ResetPasswordUiState()
