package com.v2ray.ang.composeui.p2extended.model

import com.v2ray.ang.composeui.navigation.CryptoVpnRouteSpec
import com.v2ray.ang.composeui.navigation.RouteDefinition
import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class AddressBookRouteArgs(val mode: String = "send")

data class AddressBookUiState(
        val title: String = "地址簿",
        val subtitle: String = "ADDRESS BOOK",
        val badge: String = "P2 · EXTENDED",
        val summary: String = "地址簿页管理常用地址、标签、白名单与快捷发送入口。",
        val primaryActionLabel: String? = "用该地址发起转账",
        val secondaryActionLabel: String? = "返回钱包管理",
        val heroAccent: String = "address_book",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已保存", value = "18"),
    FeatureMetric(label = "最近使用", value = "5"),
    FeatureMetric(label = "白名单", value = "6"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "name", label = "联系人名称", value = "CVPN Ops", supportingText = "地址本地展示名称"),
    FeatureField(key = "address", label = "钱包地址", value = "TQx...uJsv", supportingText = "支持多链地址与 ENS 占位"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "路由标识", subtitle = "地址簿页管理常用地址、标签、白名单与快捷发送入口。", trailing = "address_book", badge = "P2 扩展页"),
    FeatureListItem(title = "导航参数", subtitle = "mode", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单占位", subtitle = "联系人名称、钱包地址", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "交付内容", subtitle = "Composable + UiState + Event + ViewModel + Mock Repository 已补齐", trailing = "Ready", badge = "Drop-in"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "ViewModel Stub", detail = "地址簿 已预留事件分发与 refresh 占位。"),
    FeatureBullet(title = "Mock Repository", detail = "可通过 AddressBookPreviewState / Repository 种子替换真实接口。"),
    FeatureBullet(title = "Preview", detail = "页面已内置 @Preview，可直接在 Android Studio 查看。"),
    FeatureBullet(title = "Navigation Args", detail = "createRoute builder 与 NavGraph 参数解析已补齐。"),
),
        val note: String = "地址簿 已按 P2 扩展页 页面补齐，可继续替换为真实业务逻辑与接口数据。",
    )

    sealed interface AddressBookEvent {
        data object Refresh : AddressBookEvent
        data object PrimaryActionClicked : AddressBookEvent
        data object SecondaryActionClicked : AddressBookEvent
        data class FieldChanged(
            val key: String,
            val value: String,
        ) : AddressBookEvent
    }

    val addressBookNavigation: RouteDefinition = CryptoVpnRouteSpec.addressBook

    fun addressBookPreviewState(): AddressBookUiState = AddressBookUiState()
