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
        val badge: String = "能力未接入",
        val summary: String = "当前未接入私钥导入能力。",
        val primaryActionLabel: String = "校验并导入",
        val secondaryActionLabel: String? = "回到导入方式",
        val heroAccent: String = "import_private_key",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "默认链", value = "待返回"),
    FeatureMetric(label = "导入能力", value = "未接入"),
    FeatureMetric(label = "存储状态", value = "未启用"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "privateKey", label = "私钥", value = "", supportingText = "等待私钥导入能力接入"),
    FeatureField(key = "walletName", label = "钱包名称", value = "", supportingText = "导入成功后用于本地展示"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "能力状态", subtitle = "私钥导入能力尚未接入。", trailing = "未接入", badge = "Blocked"),
    FeatureListItem(title = "导航参数", subtitle = "chainId", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单状态", subtitle = "私钥与钱包名称待输入", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "数据来源", subtitle = "接入后由私钥导入流程返回。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "能力接入", detail = "接入前不展示伪造私钥、链和钱包名。"),
    FeatureBullet(title = "本地加密", detail = "导入成功后再执行本地加密存储。"),
    FeatureBullet(title = "导航参数", detail = "根据 chainId 选择目标链。"),
),
        val note: String = "私钥导入能力未接入。",
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
