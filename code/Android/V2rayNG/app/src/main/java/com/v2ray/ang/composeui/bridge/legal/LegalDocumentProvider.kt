package com.v2ray.ang.composeui.bridge.legal

data class LegalDocEntry(
    val id: String,
    val title: String,
    val description: String,
    val lastUpdated: String,
    val content: String,
)

object LegalDocumentProvider {
    private val docs = listOf(
        LegalDocEntry(
            id = "terms",
            title = "用户协议",
            description = "使用服务的条款和条件",
            lastUpdated = "2026-04-01",
            content = TERMS_CONTENT,
        ),
        LegalDocEntry(
            id = "privacy",
            title = "隐私政策",
            description = "数据收集、使用和保护说明",
            lastUpdated = "2026-04-01",
            content = PRIVACY_CONTENT,
        ),
        LegalDocEntry(
            id = "refund",
            title = "退款政策",
            description = "订单退款规则与流程",
            lastUpdated = "2026-04-01",
            content = REFUND_CONTENT,
        ),
        LegalDocEntry(
            id = "affiliate",
            title = "推广协议",
            description = "邀请推广计划规则与佣金说明",
            lastUpdated = "2026-04-01",
            content = AFFILIATE_CONTENT,
        ),
        LegalDocEntry(
            id = "cookies",
            title = "Cookie政策",
            description = "Cookie 与追踪技术说明",
            lastUpdated = "2026-04-01",
            content = COOKIES_CONTENT,
        ),
    )

    fun list(): List<LegalDocEntry> = docs

    fun get(documentId: String): LegalDocEntry? = docs.firstOrNull { it.id == documentId }
}

private const val TERMS_CONTENT = "请遵守适用法律法规，不得将服务用于违法用途。"
private const val PRIVACY_CONTENT = "我们仅在提供服务必要范围内处理账户与订单相关数据。"
private const val REFUND_CONTENT = "符合条件的订单可按平台规则申请退款。"
private const val AFFILIATE_CONTENT = "推广奖励按活动规则结算，异常行为会触发风控审查。"
private const val COOKIES_CONTENT = "我们使用必要 Cookie 维持登录态与基础功能。"
