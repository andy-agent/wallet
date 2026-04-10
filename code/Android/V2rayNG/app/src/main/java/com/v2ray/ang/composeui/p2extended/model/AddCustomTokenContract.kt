package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class AddCustomTokenRouteArgs(val chainId: String = "base")

data class AddCustomTokenUiState(
        val title: String = "添加自定义代币",
        val subtitle: String = "ADD CUSTOM TOKEN",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "自定义代币页填写合约、符号与精度，补齐链上资产扩展能力。",
        val primaryActionLabel: String = "添加到资产列表",
        val secondaryActionLabel: String? = "返回链管理",
        val heroAccent: String = "add_custom_token",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "默认链", value = "Base"),
    FeatureMetric(label = "小数位", value = "6"),
    FeatureMetric(label = "校验状态", value = "已开启"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "contract", label = "合约地址", value = "0xbase...c1f6", supportingText = "用于查询代币元数据"),
    FeatureField(key = "symbol", label = "代币符号", value = "USDC.e", supportingText = "展示给用户的代币简称"),
    FeatureField(key = "decimals", label = "精度", value = "6", supportingText = "与链上代币保持一致"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "自定义代币页填写合约、符号与精度，补齐链上资产扩展能力。", trailing = "add_custom_token", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "chainId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "合约地址、代币符号、精度", trailing = "3 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "添加自定义代币 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 AddCustomTokenPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "添加自定义代币 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface AddCustomTokenEvent {
        data object Refresh : AddCustomTokenEvent
        data object PrimaryActionClicked : AddCustomTokenEvent
        data object SecondaryActionClicked : AddCustomTokenEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : AddCustomTokenEvent
    }

    val addCustomTokenNavigation: RouteDefinition = CryptoVpnRouteSpec.addCustomToken

    fun addCustomTokenPreviewState(): AddCustomTokenUiState = AddCustomTokenUiState()
