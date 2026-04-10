package com.cryptovpn.ui.p2.model

import com.cryptovpn.navigation.CryptoVpnRouteSpec
import com.cryptovpn.navigation.RouteDefinition
import com.cryptovpn.ui.common.model.FeatureBullet
import com.cryptovpn.ui.common.model.FeatureField
import com.cryptovpn.ui.common.model.FeatureListItem
import com.cryptovpn.ui.common.model.FeatureMetric

data class WithdrawUiState(
        val title: String = "提现佣金",
        val subtitle: String = "WITHDRAW",
        val badge: String = "P2 · BASE",
        val summary: String = "佣金提现页校验提现地址、金额与网络信息，再提交结算申请。",
        val primaryActionLabel: String = "提交提现申请",
        val secondaryActionLabel: String? = "返回账本",
        val heroAccent: String = "withdraw",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "可提佣金", value = "$580.00"),
    FeatureMetric(label = "最小提现", value = "50 USDT"),
    FeatureMetric(label = "网络", value = "TRON"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "address", label = "提现地址", value = "TQx...ahmUKaE3Mb24Q2vG", supportingText = "支持粘贴或从地址簿选择"),
    FeatureField(key = "amount", label = "提现金额", value = "580.00", supportingText = "默认提取全部可用佣金"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "佣金提现页校验提现地址、金额与网络信息，再提交结算申请。", trailing = "withdraw", badge = "P2 基础文档页"),
    FeatureListItem(title = "导航参数", subtitle = "当前页面无必填导航参数", trailing = "0 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "提现地址、提现金额", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "提现佣金 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 WithdrawPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "当前页面无必填参数，但已纳入统一 RouteSpec 台账。"),
),
        val note: String = "提现佣金 已按 P2 基础文档页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface WithdrawEvent {
        data object Refresh : WithdrawEvent
        data object PrimaryActionClicked : WithdrawEvent
        data object SecondaryActionClicked : WithdrawEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : WithdrawEvent
    }

    val withdrawNavigation: RouteDefinition = CryptoVpnRouteSpec.withdraw

    fun withdrawPreviewState(): WithdrawUiState = WithdrawUiState()
