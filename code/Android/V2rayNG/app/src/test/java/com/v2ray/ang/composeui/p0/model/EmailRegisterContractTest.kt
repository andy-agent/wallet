package com.v2ray.ang.composeui.p0.model

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailRegisterContractTest {

    @Test
    fun `default register ui state should not require verification code field`() {
        val state = EmailRegisterUiState()

        assertFalse(state.fields.any { it.key == "code" })
        assertTrue(state.primaryActionLabel.contains("创建账户"))
    }
}
