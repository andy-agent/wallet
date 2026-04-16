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
        val summary: String = "自定义代币页用于录入真实合约信息；未接入自动识别时保持空态。",
        val primaryActionLabel: String = "添加到资产列表",
        val secondaryActionLabel: String? = "返回链管理",
        val heroAccent: String = "add_custom_token",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "默认链", value = "待选择"),
    FeatureMetric(label = "小数位", value = "待识别"),
    FeatureMetric(label = "校验状态", value = "待接入"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "contract", label = "合约地址", value = "", supportingText = "输入真实合约地址后再做元数据查询"),
    FeatureField(key = "symbol", label = "代币符号", value = "", supportingText = "符号由真实合约元数据返回"),
    FeatureField(key = "decimals", label = "精度", value = "", supportingText = "精度由真实合约元数据返回"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "目标链", subtitle = "chainId 决定合约查询网络。", trailing = "1 个", badge = "参数"),
    FeatureListItem(title = "元数据查询", subtitle = "符号与精度以后端或链上查询结果为准。", trailing = "待接入", badge = "查询"),
    FeatureListItem(title = "写入资产列表", subtitle = "真实保存能力未接入前不展示默认成功态。", trailing = "阻塞", badge = "保存"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "地址校验", detail = "仅在真实链规则接入后校验格式。"),
    FeatureBullet(title = "元数据填充", detail = "未接入时保持空字段，不注入默认币种。"),
    FeatureBullet(title = "资产保存", detail = "保存能力未接入前应明确提示阻塞。"),
),
        val note: String = "当前不再预置默认代币信息；仅保留真实输入和阻塞态说明。",
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
