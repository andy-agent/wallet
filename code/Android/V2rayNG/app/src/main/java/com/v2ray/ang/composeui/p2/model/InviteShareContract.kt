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
    val primaryActionLabel: String = "复制链接",
    val secondaryActionLabel: String? = "复制邀请码",
    val heroAccent: String = "invite_share",
    val metrics: List<FeatureMetric> = listOf(
        FeatureMetric(label = "链接", value = "--"),
        FeatureMetric(label = "邀请码", value = "--"),
        FeatureMetric(label = "渠道", value = "系统分享"),
    ),
    val fields: List<FeatureField> = emptyList(),
    val highlights: List<FeatureListItem> = emptyList(),
    val checklist: List<FeatureBullet> = emptyList(),
    val note: String = "正在从真实分享服务同步推广链接。",
    val banner: P2SurfaceBanner = p2LoadingBanner(),
    val feedbackMessage: String? = null,
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
