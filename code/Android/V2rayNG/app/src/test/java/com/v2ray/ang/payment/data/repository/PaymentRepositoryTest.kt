package com.v2ray.ang.payment.data.repository

import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.model.GetOrderResponse
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.TimeZone

/**
 * PaymentRepository 单元测试
 */
class PaymentRepositoryTest {

    init {
        // 确保测试时使用 UTC 时区
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"))
    }

    @Test
    fun `parseIsoDateInternal should parse ISO8601 with milliseconds`() {
        // API 返回的带毫秒格式: 2026-04-04T22:12:11.788Z
        val result = PaymentRepository.parseIsoDateInternal("2026-04-04T22:12:11.788Z")
        assertNotNull("Should parse date with milliseconds", result)
        // 验证解析结果: 2026-04-04 22:12:11.788 UTC -> 1775340731788
        val expected = 1775340731788L
        assertEquals(expected, result)
    }

    @Test
    fun `parseIsoDateInternal should parse ISO8601 without milliseconds`() {
        // 不带毫秒的格式: 2026-04-04T22:12:11Z
        val result = PaymentRepository.parseIsoDateInternal("2026-04-04T22:12:11Z")
        assertNotNull("Should parse date without milliseconds", result)
        // 验证解析结果: 2026-04-04 22:12:11 UTC -> 1775340731000
        val expected = 1775340731000L
        assertEquals(expected, result)
    }

    @Test
    fun `parseIsoDateInternal should return null for invalid format`() {
        val result = PaymentRepository.parseIsoDateInternal("invalid-date")
        assertNull("Should return null for invalid date string", result)
    }

    @Test
    fun `parseIsoDateInternal should return null for null input`() {
        val result = PaymentRepository.parseIsoDateInternal(null)
        assertNull("Should return null for null input", result)
    }

    @Test
    fun `parseIsoDateInternal should return null for blank input`() {
        val result = PaymentRepository.parseIsoDateInternal("   ")
        assertNull("Should return null for blank input", result)
    }

    @Test
    fun `createPaymentApiGson should backfill missing order createdAt from expiresAt`() {
        val payload = """
            {
              "code": "OK",
              "message": "success",
              "data": {
                "orderId": "ord_123",
                "orderNo": "ORD-123",
                "planCode": "vpn-quarter",
                "planName": "季度套餐",
                "orderType": "NEW",
                "quoteAssetCode": "SOL",
                "quoteNetworkCode": "SOLANA",
                "quoteUsdAmount": "26.99",
                "payableAmount": "0.123456",
                "status": "AWAITING_PAYMENT",
                "expiresAt": "2026-04-08T10:00:00Z",
                "createdAt": null
              }
            }
        """.trimIndent()

        val response = PaymentRepository.createPaymentApiGson()
            .fromJson(payload, GetOrderResponse::class.java)

        assertEquals("2026-04-08T10:00:00Z", response.data?.createdAt)
    }

    @Test
    fun `isTokenExpired logic with buffer should be correct`() {
        // 这个测试验证过期判定的 buffer 语义
        // 使用当前时间 + 10 分钟作为过期时间
        val now = System.currentTimeMillis()
        val buffer = PaymentConfig.TokenConfig.TOKEN_REFRESH_BUFFER_MS // 5 分钟

        // 情况 1: 过期时间在 buffer 之后 -> 未过期
        val futureExpireTime = now + buffer + 60000 // buffer + 1 分钟
        val isExpired1 = isTokenExpiredInternal(futureExpireTime, now)
        assertFalse("Token should not be expired when expire time is after buffer", isExpired1)

        // 情况 2: 过期时间在 buffer 内 -> 视为过期（需要刷新）
        val nearExpireTime = now + buffer - 60000 // buffer - 1 分钟
        val isExpired2 = isTokenExpiredInternal(nearExpireTime, now)
        assertTrue("Token should be expired when expire time is within buffer", isExpired2)

        // 情况 3: 已过期 -> 过期
        val pastExpireTime = now - 60000 // 1 分钟前
        val isExpired3 = isTokenExpiredInternal(pastExpireTime, now)
        assertTrue("Token should be expired when expire time is in the past", isExpired3)

        // 情况 4: 刚好在 buffer 边界 -> 视为过期
        val exactBufferTime = now + buffer
        val isExpired4 = isTokenExpiredInternal(exactBufferTime, now)
        assertTrue("Token should be expired when expire time is exactly at buffer boundary", isExpired4)
    }

    @Test
    fun `isTokenExpired logic with real API response format`() {
        // 模拟真实场景：API 返回带毫秒的过期时间
        val now = 1743799931788L // 2026-04-04T22:12:11.788Z
        val expireTimeStr = "2026-04-04T22:20:00.000Z" // 约 8 分钟后过期
        val expireTime = PaymentRepository.parseIsoDateInternal(expireTimeStr)!!

        // 当前时间早于过期时间 -> 未过期
        val isExpired = isTokenExpiredInternal(expireTime, now)
        assertFalse("Token parsed from API should not be expired", isExpired)
    }

    @Test
    fun `isTokenExpired logic when parse fails should treat as expired`() {
        // 当解析失败时，应该视为过期
        val parsed = PaymentRepository.parseIsoDateInternal("invalid")
        assertNull("Invalid date should parse to null", parsed)

        // 模拟 isTokenExpired 中解析失败返回 true 的逻辑
        val isExpired = parsed?.let { expireTime ->
            val now = System.currentTimeMillis()
            now + PaymentConfig.TokenConfig.TOKEN_REFRESH_BUFFER_MS >= expireTime
        } ?: true
        assertTrue("When parse fails, token should be considered expired", isExpired)
    }

    /**
     * 模拟 isTokenExpired 的内部逻辑，便于测试
     */
    private fun isTokenExpiredInternal(expiresAt: Long, currentTime: Long): Boolean {
        return currentTime + PaymentConfig.TokenConfig.TOKEN_REFRESH_BUFFER_MS >= expiresAt
    }
}
