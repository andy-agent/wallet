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
    val summary: String = "在 Compose 内完成真实发码与密码重置。",
    val primaryActionLabel: String = "提交并重置密码",
    val secondaryActionLabel: String? = "返回登录",
    val heroAccent: String = "reset_password",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "验证码", value = "6 位"),
        FeatureMetric(label = "接口状态", value = "已接通"),
        FeatureMetric(label = "密码规则", value = "至少 8 位"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "email", label = "邮箱", value = "", supportingText = "接收重置验证码"),
        FeatureField(key = "code", label = "验证码", value = "", supportingText = "当前环境验证码由服务端统一校验"),
        FeatureField(key = "password", label = "新密码", value = "", supportingText = "重置后立即生效"),
        FeatureField(key = "confirm", label = "确认密码", value = "", supportingText = "保持两次输入一致"),
    ),
    val highlights: List<FeatureListItem> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "当前页已切到真实密码找回接口。",
    val isLoading: Boolean = false,
    val isRequestingCode: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
)

sealed interface ResetPasswordEvent {
    data object Refresh : ResetPasswordEvent
    data object PrimaryActionClicked : ResetPasswordEvent
    data object SecondaryActionClicked : ResetPasswordEvent
    data object SendCodeClicked : ResetPasswordEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : ResetPasswordEvent
}

val resetPasswordNavigation: RouteDefinition = CryptoVpnRouteSpec.resetPassword

fun resetPasswordPreviewState(): ResetPasswordUiState = ResetPasswordUiState()
