package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class ImportPrivateKeyRouteArgs(val chainId: String = "ethereum")

data class ImportPrivateKeyUiState(
        val title: String = "输入私钥",
        val subtitle: String = "IMPORT PRIVATE KEY",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "私钥导入页展示链选择、私钥输入与本地加密提示。",
        val primaryActionLabel: String? = "校验并导入",
        val secondaryActionLabel: String? = "回到导入方式",
        val heroAccent: String = "import_private_key",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "默认链", value = "Ethereum"),
    FeatureMetric(label = "格式", value = "Hex"),
    FeatureMetric(label = "显示方式", value = "已隐藏"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "privateKey", label = "私钥", value = "0x••••••••••••••••••••••••", supportingText = "预留真实输入框与密文存储逻辑"),
    FeatureField(key = "walletName", label = "钱包名称", value = "Trading Wallet", supportingText = "导入后在钱包管理中展示"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "私钥导入页展示链选择、私钥输入与本地加密提示。", trailing = "import_private_key", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "chainId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "私钥、钱包名称", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "输入私钥 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 ImportPrivateKeyPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "输入私钥 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
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
