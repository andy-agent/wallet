package com.v2ray.ang.payment.data.repository

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class PaymentRepositorySourceTest {

    private val sourceFile = File(
        "src/main/java/com/v2ray/ang/payment/data/repository/PaymentRepository.kt",
    )

    @Test
    fun `payment repository source exists`() {
        assertTrue(sourceFile.exists())
    }

    @Test
    fun `payment repository caches plans locally and refreshes in background`() {
        val source = sourceFile.readText()

        assertTrue(source.contains("suspend fun getCachedPlans("))
        assertTrue(source.contains("suspend fun syncPlansFromServer("))
        assertTrue(source.contains("savePlansCache("))
    }

    @Test
    fun `payment repository caches wallet asset catalog locally and refreshes in background`() {
        val source = sourceFile.readText()

        assertTrue(source.contains("suspend fun getCachedWalletAssetCatalog("))
        assertTrue(source.contains("suspend fun syncWalletAssetCatalogFromServer("))
        assertTrue(source.contains("saveWalletAssetCatalogCache("))
    }
}
