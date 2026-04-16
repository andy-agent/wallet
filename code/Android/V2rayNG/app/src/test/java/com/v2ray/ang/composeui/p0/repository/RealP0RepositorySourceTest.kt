package com.v2ray.ang.composeui.p0.repository

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class RealP0RepositorySourceTest {

    private val sourceFile = File(
        "src/main/java/com/v2ray/ang/composeui/p0/repository/RealP0Repository.kt",
    )

    @Test
    fun `p0 repository source exists`() {
        assertTrue(sourceFile.exists())
    }

    @Test
    fun `p0 repository resyncs derived wallet addresses before forcing wallet overview refresh`() {
        val source = sourceFile.readText()

        assertTrue(source.contains("syncDerivedWalletAddressesIfAvailable(currentUserId)"))
        assertTrue(source.contains("syncWalletOverviewFromServer(force = true, userId = currentUserId)"))
    }
}
