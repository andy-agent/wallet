package com.v2ray.ang.composeui.common.repository

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class RealCryptoVpnRepositorySourceTest {

    private val sourceFile = File(
        "src/main/java/com/v2ray/ang/composeui/common/repository/RealCryptoVpnRepository.kt",
    )

    @Test
    fun `real repository source exists`() {
        assertTrue(sourceFile.exists())
    }

    @Test
    fun `real repository should not directly fallback to mock repository`() {
        val source = sourceFile.readText()

        assertFalse(
            "RealCryptoVpnRepository still contains direct fallback.get calls",
            source.contains("fallback.get"),
        )
    }
}
