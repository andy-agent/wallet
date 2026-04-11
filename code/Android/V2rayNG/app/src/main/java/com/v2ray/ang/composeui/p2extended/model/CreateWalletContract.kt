package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class CreateWalletRouteArgs(val mode: String = "create")

data class CreateWalletUiState(
    val title: String = "创建钱包",
    val subtitle: String = "CREATE WALLET",
    val badge: String = "同步中",
    val summary: String = "正在检查钱包创建能力；若本地密钥引擎未接通，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "create_wallet",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "创建模式", value = "读取中"),
        FeatureMetric(label = "账户状态", value = "读取中"),
        FeatureMetric(label = "钱包引擎", value = "读取中"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "name", label = "钱包名称", value = "", supportingText = "等待真实钱包引擎或阻塞说明。"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在检查创建钱包能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示钱包名和假创建入口。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
)

sealed interface CreateWalletEvent {
    data object Refresh : CreateWalletEvent
    data object PrimaryActionClicked : CreateWalletEvent
    data object SecondaryActionClicked : CreateWalletEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : CreateWalletEvent
}

val createWalletNavigation: RouteDefinition = CryptoVpnRouteSpec.createWallet

fun createWalletPreviewState(): CreateWalletUiState = CreateWalletUiState()
