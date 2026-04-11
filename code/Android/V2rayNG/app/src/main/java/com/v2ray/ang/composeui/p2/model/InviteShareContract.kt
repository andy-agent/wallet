package com.v2ray.ang.composeui.p2.model

import com.v2ray.ang.composeui.common.model.FeatureBullet
import com.v2ray.ang.composeui.common.model.FeatureField
import com.v2ray.ang.composeui.common.model.FeatureListItem
import com.v2ray.ang.composeui.common.model.FeatureMetric

data class InviteShareUiState(
    val title: String = "分享推广链接",
    val subtitle: String = "SHARE LINK",
    val badge: String = "推广",
    val summary: String = "生成推广链接与二维码，便于复制或转发。",
    val primaryActionLabel: String? = "复制链接",
    val secondaryActionLabel: String? = "复制邀请码",
    val heroAccent: String = "invite_share",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "链接", value = "https://cryptovpn.app/invite/CVPN-2025-GLOW"),
        FeatureMetric(label = "邀请码", value = "CVPN-2025-GLOW"),
        FeatureMetric(label = "渠道", value = "分享"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = listOf(
        FeatureListItem(title = "推广链接", subtitle = "https://cryptovpn.app/invite/CVPN-2025-GLOW", trailing = "复制"),
        FeatureListItem(title = "邀请码", subtitle = "CVPN-2025-GLOW", trailing = "复制"),
        FeatureListItem(title = "二维码", subtitle = "用于转发与扫码进入", trailing = "分享"),
    ),
    val checklist: List<FeatureBullet> = listOf(
        FeatureBullet(title = "推广链接", detail = "https://cryptovpn.app/invite/CVPN-2025-GLOW"),
        FeatureBullet(title = "邀请码", detail = "CVPN-2025-GLOW"),
        FeatureBullet(title = "复制链接", detail = "一键复制"),
        FeatureBullet(title = "复制邀请码", detail = "一键复制"),
    ),
    val note: String = "生成推广链接与二维码，便于复制或转发。",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emptyMessage: String? = null,
    val blockerTitle: String? = null,
    val blockerMessage: String? = null,
)

sealed interface InviteShareEvent {
    data object Refresh : InviteShareEvent
    data object PrimaryActionClicked : InviteShareEvent
    data object SecondaryActionClicked : InviteShareEvent
    data class FieldChanged(
        val key: String,
        val value: String,
    ) : InviteShareEvent
}

fun inviteSharePreviewState(): InviteShareUiState = InviteShareUiState()
