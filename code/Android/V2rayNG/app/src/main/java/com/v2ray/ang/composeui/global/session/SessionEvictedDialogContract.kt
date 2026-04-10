package com.v2ray.ang.composeui.global.session

import com.v2ray.ang.composeui.common.model.FeatureBullet

data class SessionEvictedDialogUiState(
    val title: String = "会话已失效",
    val message: String = "检测到账号在其他设备刷新了登录态，请重新验证当前设备后继续访问 VPN 与钱包能力。",
    val primaryActionLabel: String = "重新登录",
    val secondaryActionLabel: String = "暂时关闭",
    val impacts: List<FeatureBullet> = listOf(
        FeatureBullet(
            title = "影响范围",
            detail = "当前账号下的 VPN、订单、钱包与邀请入口都应回到登录态。",
        ),
        FeatureBullet(
            title = "安全建议",
            detail = "建议同步检查设备列表、2FA 与最近授权记录。",
        ),
        FeatureBullet(
            title = "交付说明",
            detail = "该 Dialog 独立于 51 个 routed page 之外，不与页面路由合并。",
        ),
    ),
)

sealed interface SessionEvictedDialogEvent {
    data object Refresh : SessionEvictedDialogEvent
    data object Confirm : SessionEvictedDialogEvent
    data object Dismiss : SessionEvictedDialogEvent
}

fun sessionEvictedDialogPreviewState(): SessionEvictedDialogUiState = SessionEvictedDialogUiState()
