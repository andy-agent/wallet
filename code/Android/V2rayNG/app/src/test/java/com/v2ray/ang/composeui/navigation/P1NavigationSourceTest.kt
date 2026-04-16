package com.v2ray.ang.composeui.navigation

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

class P1NavigationSourceTest {

    private val p1NavGraph = File(
        "src/main/java/com/v2ray/ang/composeui/navigation/P1NavGraph.kt",
    )
    private val vpnHomePage = File(
        "src/main/java/com/v2ray/ang/composeui/pages/p0/VpnHomePage.kt",
    )

    @Test
    fun `plans primary action goes directly to order checkout`() {
        val source = p1NavGraph.readText()

        assertTrue(source.contains("navController.navigateSingleTop(CryptoVpnRouteSpec.orderCheckoutRoute(planCode))"))
        assertFalse(source.contains("navController.navigateSingleTop(CryptoVpnRouteSpec.regionSelectionRoute(planCode))"))
    }

    @Test
    fun `vpn home region navigation uses concrete route helper instead of route pattern`() {
        val source = vpnHomePage.readText()

        assertTrue(source.contains("CryptoVpnRouteSpec.regionSelectionRoute()"))
        assertFalse(source.contains("CryptoVpnRouteSpec.regionSelection.pattern"))
    }
}
