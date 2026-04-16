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
    val summary: String = "使用邮箱和密码直接创建账户。",
    val primaryActionLabel: String = "创建账户并进入",
    val secondaryActionLabel: String? = "已有账户？去登录",
    val heroAccent: String = "email_register",
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val unavailableMessage: String? = null,
    val completed: Boolean = false,
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "注册步骤", value = "邮箱 -> 密码 -> 注册"),
        FeatureMetric(label = "密码规则", value = "至少 8 位"),
        FeatureMetric(label = "邀请码", value = "注册后绑定"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "email", label = "邮箱", value = "", supportingText = "将作为登录与找回凭据"),
        FeatureField(key = "password", label = "登录密码", value = "", supportingText = "需包含字母与数字"),
        FeatureField(key = "invite", label = "邀请码", value = "", supportingText = "选填，注册成功后尝试绑定"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前流程", subtitle = "真实邮箱注册", trailing = "Compose", badge = "LIVE"),
        FeatureListItem(title = "动作类型", subtitle = "填写凭据 / 提交注册", trailing = "1 步", badge = "AUTH"),
        FeatureListItem(title = "成功结果", subtitle = "保存 token 并进入主界面", trailing = "真实", badge = "OK"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "邮箱", detail = " "),
        FeatureBullet(title = "注册", detail = " "),
        FeatureBullet(title = "邀请码", detail = " "),
    ),
    val note: String = "注册成功后进入主界面。",
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

data class EmailRegisterActionResult(
    val success: Boolean,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val unavailable: Boolean = false,
    val completed: Boolean = false,
    val nextRoute: String? = null,
)

val emailRegisterNavigation: RouteDefinition = CryptoVpnRouteSpec.emailRegister

fun emailRegisterPreviewState(): EmailRegisterUiState = EmailRegisterUiState()
