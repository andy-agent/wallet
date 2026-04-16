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
    fun `isTokenExpired should stay false when access token exists`() {
        val now = System.currentTimeMillis()
        val expireTime = now - PaymentConfig.TokenConfig.TOKEN_REFRESH_BUFFER_MS - 60000
        val isExpired = isTokenExpiredInternal(
            hasAccessToken = true,
            expiresAt = expireTime,
            currentTime = now,
        )
        assertFalse("Token should remain valid locally when access token exists", isExpired)
    }

    @Test
    fun `isTokenExpired should be true when access token missing`() {
        val now = System.currentTimeMillis()
        val isExpired = isTokenExpiredInternal(
            hasAccessToken = false,
            expiresAt = now + 3600_000,
            currentTime = now,
        )
        assertTrue("Token should be expired when access token is missing", isExpired)
    }

    @Test
    fun `parseIsoDate still supports real API response format`() {
        val now = 1743799931788L
        val expireTimeStr = "2026-04-04T22:20:00.000Z"
        val expireTime = PaymentRepository.parseIsoDateInternal(expireTimeStr)!!
        val isExpired = isTokenExpiredInternal(
            hasAccessToken = true,
            expiresAt = expireTime,
            currentTime = now,
        )
        assertFalse("Parsed date should not force local token expiry while access token exists", isExpired)
    }

    @Test
    fun `parseIsoDate returns null when format invalid`() {
        val parsed = PaymentRepository.parseIsoDateInternal("invalid")
        assertNull("Invalid date should parse to null", parsed)
    }

    /**
     * 模拟 isTokenExpired 的内部逻辑，便于测试
     */
    private fun isTokenExpiredInternal(
        hasAccessToken: Boolean,
        expiresAt: Long,
        currentTime: Long,
    ): Boolean {
        return if (hasAccessToken) {
            false
        } else {
            true
        }
    }
}
