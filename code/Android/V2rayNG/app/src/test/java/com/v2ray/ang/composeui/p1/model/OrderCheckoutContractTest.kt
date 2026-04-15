package com.v2ray.ang.composeui.p1.model

import org.junit.Assert.assertEquals
import org.junit.Test

class OrderCheckoutContractTest {

    @Test
    fun `checkoutPaymentLabel formats Solana and Tron variants as dotted labels`() {
        assertEquals("sol.solana", checkoutPaymentLabel("SOL", "SOLANA"))
        assertEquals("USDT.solana", checkoutPaymentLabel("USDT", "SOLANA"))
        assertEquals("USDT.tron", checkoutPaymentLabel("USDT", "TRON"))
    }
}
