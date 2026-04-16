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
    val summary: String = "通过邮箱验证码重置当前账号密码。",
    val primaryActionLabel: String = "提交并重置密码",
    val secondaryActionLabel: String? = "返回登录",
    val heroAccent: String = "reset_password",
    val isSubmitting: Boolean = false,
    val isRequestingCode: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val unavailableMessage: String? = null,
    val completed: Boolean = false,
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "验证码", value = "真实后端"),
        FeatureMetric(label = "密码校验", value = "两次输入一致"),
        FeatureMetric(label = "提交动作", value = "当前页完成"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "email", label = "邮箱", value = "", supportingText = "接收重置验证码"),
        FeatureField(key = "code", label = "验证码", value = "", supportingText = "先发送验证码，再填写"),
        FeatureField(key = "password", label = "新密码", value = "", supportingText = "重置后立即生效"),
        FeatureField(key = "confirm", label = "确认密码", value = "", supportingText = "保持两次输入一致"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前流程", subtitle = "真实密码重置", trailing = "Compose", badge = "LIVE"),
        FeatureListItem(title = "动作类型", subtitle = "发送验证码 / 提交新密码", trailing = "2 步", badge = "AUTH"),
        FeatureListItem(title = "成功结果", subtitle = "返回登录页并使用新密码登录", trailing = "真实", badge = "OK"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "发码", detail = " "),
        FeatureBullet(title = "重置", detail = " "),
        FeatureBullet(title = "失败反馈", detail = "网络失败、验证码错误、密码不一致"),
    ),
    val note: String = "当前页不再假跳转；只有后端重置成功后才回登录页。",
)

sealed interface ResetPasswordEvent {
    data object Refresh : ResetPasswordEvent
    data object RequestCodeClicked : ResetPasswordEvent
    data object PrimaryActionClicked : ResetPasswordEvent
    data object SecondaryActionClicked : ResetPasswordEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : ResetPasswordEvent
}

data class ResetPasswordActionResult(
    val success: Boolean,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val unavailable: Boolean = false,
    val completed: Boolean = false,
)

val resetPasswordNavigation: RouteDefinition = CryptoVpnRouteSpec.resetPassword

fun resetPasswordPreviewState(): ResetPasswordUiState = ResetPasswordUiState()
