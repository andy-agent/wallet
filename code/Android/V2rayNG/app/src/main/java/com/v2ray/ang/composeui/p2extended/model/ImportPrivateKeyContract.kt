package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric
import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition

data class ImportPrivateKeyRouteArgs(val chainId: String = "ethereum")

data class ImportPrivateKeyUiState(
    val title: String = "输入私钥",
    val subtitle: String = "IMPORT PRIVATE KEY",
    val badge: String = "同步中",
    val summary: String = "正在检查私钥解析与本地加密能力；若未接通，将显示阻塞说明。",
    val primaryActionLabel: String? = null,
    val secondaryActionLabel: String? = null,
    val heroAccent: String = "import_private_key",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "目标链", value = "读取中"),
        FeatureMetric(label = "校验状态", value = "读取中"),
        FeatureMetric(label = "加密存储", value = "读取中"),
    ),
    val fields: List<FeatureField> = listOf(
        FeatureField(key = "privateKey", label = "私钥", value = "", supportingText = "等待真实解析能力或阻塞说明。"),
        FeatureField(key = "walletName", label = "钱包名称", value = "", supportingText = "仅在真实导入流程接通后生效。"),
    ),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "当前状态", subtitle = "正在检查私钥导入能力", trailing = "", badge = "LOADING"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "说明", detail = "默认态不再展示演示私钥、假钱包名和假导入按钮。"),
    ),
    val note: String = "刷新后会替换为真实状态或明确阻塞说明。",
)

sealed interface ImportPrivateKeyEvent {
    data object Refresh : ImportPrivateKeyEvent
    data object PrimaryActionClicked : ImportPrivateKeyEvent
    data object SecondaryActionClicked : ImportPrivateKeyEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : ImportPrivateKeyEvent
}

val importPrivateKeyNavigation: RouteDefinition = CryptoVpnRouteSpec.importPrivateKey

fun importPrivateKeyPreviewState(): ImportPrivateKeyUiState = ImportPrivateKeyUiState()
