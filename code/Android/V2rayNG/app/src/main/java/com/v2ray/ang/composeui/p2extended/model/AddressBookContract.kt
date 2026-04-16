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
        val badge: String = "待同步",
        val summary: String = "等待地址簿数据与可用地址返回。",
        val primaryActionLabel: String = "用该地址发起转账",
        val secondaryActionLabel: String? = "返回钱包管理",
        val heroAccent: String = "address_book",
        val metrics: List<FeatureMetric> = listOf(
    FeatureMetric(label = "已保存", value = "待同步"),
    FeatureMetric(label = "最近使用", value = "待同步"),
    FeatureMetric(label = "白名单", value = "待同步"),
),
        val fields: List<FeatureField> = listOf(
    FeatureField(key = "name", label = "联系人名称", value = "", supportingText = "等待地址簿返回联系人名称"),
    FeatureField(key = "address", label = "钱包地址", value = "", supportingText = "等待地址簿返回可用地址"),
),
        val highlights: List<FeatureListItem> = listOf(
    FeatureListItem(title = "地址簿状态", subtitle = "等待地址簿记录与白名单返回。", trailing = "待同步", badge = "State"),
    FeatureListItem(title = "导航参数", subtitle = "mode", trailing = "1 个", badge = "Nav"),
    FeatureListItem(title = "表单状态", subtitle = "联系人名称与钱包地址待返回", trailing = "2 项", badge = "Form"),
    FeatureListItem(title = "数据来源", subtitle = "由地址簿接口实时返回。", trailing = "Runtime", badge = "Source"),
),
        val checklist: List<FeatureBullet> = listOf(
    FeatureBullet(title = "地址簿数据", detail = "未返回真实地址前，不展示伪造联系人与地址。"),
    FeatureBullet(title = "发送入口", detail = "选择地址后再进入真实发送流程。"),
    FeatureBullet(title = "导航参数", detail = "根据 mode 决定选择或管理行为。"),
),
        val note: String = "当前未返回地址簿数据。",
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
