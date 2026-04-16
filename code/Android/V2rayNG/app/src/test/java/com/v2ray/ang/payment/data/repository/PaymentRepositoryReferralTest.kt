package com.v2ray.ang.payment.data.repository

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PaymentRepositoryReferralTest {

    @Test
    fun `normalizeReferralCodeInternal trims and uppercases invite code`() {
        assertEquals("ABC123", PaymentRepository.normalizeReferralCodeInternal("  aBc123  "))
    }

    @Test
    fun `shouldSkipReferralBindFailure treats invalid referral as non-blocking`() {
        assertTrue(PaymentRepository.shouldSkipReferralBindFailure("Referral code invalid"))
        assertTrue(PaymentRepository.shouldSkipReferralBindFailure("当前账号已绑定邀请码，不能重复绑定"))
        assertTrue(PaymentRepository.shouldSkipReferralBindFailure("邀请码不能为空"))
        assertFalse(PaymentRepository.shouldSkipReferralBindFailure("网络异常"))
    }
}
