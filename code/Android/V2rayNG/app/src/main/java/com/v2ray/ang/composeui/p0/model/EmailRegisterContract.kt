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
    val summary: String = "在 Compose 内完成真实发码与注册，不再跳旧登录 Activity。",
    val primaryActionLabel: String = "创建账户并进入",
    val secondaryActionLabel: String? = "已有账户？去登录",
    val heroAccent: String = "email_register",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "注册步骤", value = "3 / 3"),
        FeatureMetric(label = "密码规则", value = "大小写 + 数字"),
        FeatureMetric(label = "验证码", value = "真实接口"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "email", label = "邮箱", value = "", supportingText = "将作为登录与找回凭据"),
        FeatureField(key = "code", label = "验证码", value = "", supportingText = "当前环境验证码由服务端统一校验"),
        FeatureField(key = "password", label = "登录密码", value = "", supportingText = "至少 8 位，需包含大小写字母和数字"),
    ),
    val highlights: List<FeatureListItem> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "当前页已切到真实注册接口。",
    val isLoading: Boolean = false,
    val isRequestingCode: Boolean = false,
    val statusMessage: String? = null,
    val errorMessage: String? = null,
)

sealed interface EmailRegisterEvent {
    data object Refresh : EmailRegisterEvent
    data object PrimaryActionClicked : EmailRegisterEvent
    data object SecondaryActionClicked : EmailRegisterEvent
    data object SendCodeClicked : EmailRegisterEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : EmailRegisterEvent
}

val emailRegisterNavigation: RouteDefinition = CryptoVpnRouteSpec.emailRegister

fun emailRegisterPreviewState(): EmailRegisterUiState = EmailRegisterUiState()
