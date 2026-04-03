package com.cryptovpn.test.navigation

import android.content.Intent
import android.net.Uri
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.navigation.compose.rememberNavController
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.cryptovpn.navigation.*
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * 深层链接测试
 * 
 * 验证深层链接相关功能
 */
@RunWith(AndroidJUnit4::class)
class DeepLinkTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var navigationManager: NavigationManager
    private lateinit var deepLinkHandler: DeepLinkHandler
    
    @Before
    fun setup() {
        deepLinkHandler = DeepLinkHandler()
        composeTestRule.setContent {
            val navController = rememberNavController()
            navigationManager = NavigationManager(navController)
            NavigationManagerProvider.initialize(navController)
        }
    }
    
    // ==================== Scheme和Host验证 ====================
    
    @Test
    fun testDeepLinkSchemes() {
        assertEquals("cryptovpn", DeepLinkHandler.SCHEME_APP)
        assertEquals("https", DeepLinkHandler.SCHEME_HTTPS)
    }
    
    @Test
    fun testDeepLinkHosts() {
        assertEquals("app", DeepLinkHandler.HOST_APP)
        assertEquals("cryptovpn.com", DeepLinkHandler.HOST_WEB)
    }
    
    // ==================== 深层链接基础URI测试 ====================
    
    @Test
    fun testDeepLinkBaseUri() {
        assertEquals("cryptovpn://app", Routes.DEEP_LINK_BASE)
        assertEquals("https://cryptovpn.com", Routes.DEEP_LINK_HTTPS)
    }
    
    // ==================== 启动相关深层链接测试 ====================
    
    @Test
    fun testDeepLink_Splash() {
        val uri = Uri.parse("cryptovpn://app/splash")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.Splash.FULL_ROUTE, navResult.route)
    }
    
    @Test
    fun testDeepLinkFactory_Splash() {
        val uri = DeepLinkFactory.splash()
        assertEquals("cryptovpn://app/splash", uri.toString())
    }
    
    // ==================== 认证相关深层链接测试 ====================
    
    @Test
    fun testDeepLink_EmailLogin() {
        val uri = Uri.parse("cryptovpn://app/email_login?redirect=vpn_home")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.EmailLogin.FULL_ROUTE, navResult.route)
        assertEquals("vpn_home", navResult.params[Routes.EmailLogin.PARAM_REDIRECT])
    }
    
    @Test
    fun testDeepLink_EmailLogin_NoRedirect() {
        val uri = Uri.parse("cryptovpn://app/email_login")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
    }
    
    @Test
    fun testDeepLink_EmailRegister() {
        val uri = Uri.parse("cryptovpn://app/email_register?inviteCode=ABC123")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.EmailRegister.FULL_ROUTE, navResult.route)
        assertEquals("ABC123", navResult.params[Routes.EmailRegister.PARAM_INVITE_CODE])
    }
    
    @Test
    fun testDeepLink_ResetPassword() {
        val uri = Uri.parse("cryptovpn://app/reset_password?email=user@example.com&token=reset123")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.ResetPassword.FULL_ROUTE, navResult.route)
        assertEquals("user@example.com", navResult.params[Routes.ResetPassword.PARAM_EMAIL])
        assertEquals("reset123", navResult.params[Routes.ResetPassword.PARAM_TOKEN])
    }
    
    // ==================== VPN相关深层链接测试 ====================
    
    @Test
    fun testDeepLink_VpnHome() {
        val uri = Uri.parse("cryptovpn://app/vpn_home")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.VpnHome.FULL_ROUTE, navResult.route)
    }
    
    @Test
    fun testDeepLink_VpnHome_AutoConnect() {
        val uri = Uri.parse("cryptovpn://app/vpn_home?autoConnect=true")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("true", navResult.params[Routes.VpnHome.PARAM_AUTO_CONNECT])
    }
    
    @Test
    fun testDeepLink_Plans() {
        val uri = Uri.parse("cryptovpn://app/plans?selectedPlan=premium")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.Plans.FULL_ROUTE, navResult.route)
        assertEquals("premium", navResult.params[Routes.Plans.PARAM_SELECTED_PLAN])
    }
    
    @Test
    fun testDeepLink_OrderDetail() {
        val uri = Uri.parse("cryptovpn://app/order_detail/order123")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("order_detail/order123", navResult.route)
    }
    
    @Test
    fun testDeepLink_OrderList() {
        val uri = Uri.parse("cryptovpn://app/order_list?statusFilter=completed")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.OrderList.FULL_ROUTE, navResult.route)
        assertEquals("completed", navResult.params[Routes.OrderList.PARAM_STATUS_FILTER])
    }
    
    // ==================== 钱包相关深层链接测试 ====================
    
    @Test
    fun testDeepLink_WalletHome() {
        val uri = Uri.parse("cryptovpn://app/wallet_home")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.WalletHome.FULL_ROUTE, navResult.route)
    }
    
    @Test
    fun testDeepLink_WalletHome_Highlight() {
        val uri = Uri.parse("cryptovpn://app/wallet_home?highlightAsset=BTC")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("BTC", navResult.params[Routes.WalletHome.PARAM_HIGHLIGHT_ASSET])
    }
    
    @Test
    fun testDeepLink_AssetDetail() {
        val uri = Uri.parse("cryptovpn://app/asset_detail/BTC?chainId=bitcoin")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("asset_detail/BTC", navResult.route)
        assertEquals("bitcoin", navResult.params[Routes.AssetDetail.PARAM_CHAIN_ID])
    }
    
    @Test
    fun testDeepLink_Send() {
        val uri = Uri.parse("cryptovpn://app/send/ETH?chainId=ethereum&toAddress=0x123&amount=1.0")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("send/ETH", navResult.route)
        assertEquals("ethereum", navResult.params[Routes.Send.PARAM_CHAIN_ID])
        assertEquals("0x123", navResult.params[Routes.Send.PARAM_TO_ADDRESS])
        assertEquals("1.0", navResult.params[Routes.Send.PARAM_AMOUNT])
    }
    
    @Test
    fun testDeepLink_Receive() {
        val uri = Uri.parse("cryptovpn://app/receive/USDT?chainId=tron")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("receive/USDT", navResult.route)
        assertEquals("tron", navResult.params[Routes.Receive.PARAM_CHAIN_ID])
    }
    
    // ==================== 增长相关深层链接测试 ====================
    
    @Test
    fun testDeepLink_InviteCenter() {
        val uri = Uri.parse("cryptovpn://app/invite_center")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.InviteCenter.FULL_ROUTE, navResult.route)
    }
    
    @Test
    fun testDeepLink_InviteCenter_Tab() {
        val uri = Uri.parse("cryptovpn://app/invite_center?highlightTab=statistics")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("statistics", navResult.params[Routes.InviteCenter.PARAM_HIGHLIGHT_TAB])
    }
    
    @Test
    fun testDeepLink_CommissionLedger() {
        val uri = Uri.parse("cryptovpn://app/commission_ledger?period=2024-01")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.CommissionLedger.FULL_ROUTE, navResult.route)
        assertEquals("2024-01", navResult.params[Routes.CommissionLedger.PARAM_PERIOD])
    }
    
    @Test
    fun testDeepLink_Withdraw() {
        val uri = Uri.parse("cryptovpn://app/withdraw?currency=USDT&maxAmount=1000")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.Withdraw.FULL_ROUTE, navResult.route)
        assertEquals("USDT", navResult.params[Routes.Withdraw.PARAM_CURRENCY])
        assertEquals("1000", navResult.params[Routes.Withdraw.PARAM_MAX_AMOUNT])
    }
    
    // ==================== 法务相关深层链接测试 ====================
    
    @Test
    fun testDeepLink_Profile() {
        val uri = Uri.parse("cryptovpn://app/profile")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.Profile.FULL_ROUTE, navResult.route)
    }
    
    @Test
    fun testDeepLink_LegalDocuments() {
        val uri = Uri.parse("cryptovpn://app/legal_documents?category=terms")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.LegalDocuments.FULL_ROUTE, navResult.route)
        assertEquals("terms", navResult.params[Routes.LegalDocuments.PARAM_CATEGORY])
    }
    
    @Test
    fun testDeepLink_LegalDocumentDetail() {
        val uri = Uri.parse("cryptovpn://app/legal_document_detail/terms_of_service?docType=terms")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("legal_document_detail/terms_of_service", navResult.route)
        assertEquals("terms", navResult.params[Routes.LegalDocumentDetail.PARAM_DOC_TYPE])
    }
    
    // ==================== 特殊动作深层链接测试 ====================
    
    @Test
    fun testDeepLink_ConnectVpn() {
        val uri = Uri.parse("cryptovpn://app/connect")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Action)
        val actionResult = result as DeepLinkHandler.DeepLinkType.Action
        assertEquals("connect_vpn", actionResult.action)
    }
    
    @Test
    fun testDeepLink_DisconnectVpn() {
        val uri = Uri.parse("cryptovpn://app/disconnect")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Action)
        val actionResult = result as DeepLinkHandler.DeepLinkType.Action
        assertEquals("disconnect_vpn", actionResult.action)
    }
    
    @Test
    fun testDeepLink_Payment() {
        val uri = Uri.parse("cryptovpn://app/payment?orderId=order123&amount=100")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Action)
        val actionResult = result as DeepLinkHandler.DeepLinkType.Action
        assertEquals("process_payment", actionResult.action)
        assertEquals("order123", actionResult.data["orderId"])
        assertEquals("100", actionResult.data["amount"])
    }
    
    // ==================== 无效深层链接测试 ====================
    
    @Test
    fun testDeepLink_InvalidScheme() {
        val uri = Uri.parse("invalid://app/splash")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Invalid)
    }
    
    @Test
    fun testDeepLink_InvalidHost() {
        val uri = Uri.parse("cryptovpn://invalid/splash")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Invalid)
    }
    
    @Test
    fun testDeepLink_EmptyPath() {
        val uri = Uri.parse("cryptovpn://app")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Invalid)
    }
    
    @Test
    fun testDeepLink_UnknownRoute() {
        val uri = Uri.parse("cryptovpn://app/unknown_route")
        val result = deepLinkHandler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Invalid)
    }
    
    // ==================== 深层链接工厂测试 ====================
    
    @Test
    fun testDeepLinkFactory_AllMethods() {
        // 测试所有工厂方法
        assertNotNull(DeepLinkFactory.splash())
        assertNotNull(DeepLinkFactory.forceUpdate("1.0", "https://example.com"))
        assertNotNull(DeepLinkFactory.emailLogin())
        assertNotNull(DeepLinkFactory.emailLogin("vpn_home"))
        assertNotNull(DeepLinkFactory.emailRegister())
        assertNotNull(DeepLinkFactory.emailRegister("INVITE123"))
        assertNotNull(DeepLinkFactory.resetPassword("email@test.com", "token123"))
        assertNotNull(DeepLinkFactory.vpnHome())
        assertNotNull(DeepLinkFactory.vpnHome(true))
        assertNotNull(DeepLinkFactory.plans())
        assertNotNull(DeepLinkFactory.plans("premium"))
        assertNotNull(DeepLinkFactory.orderDetail("order123"))
        assertNotNull(DeepLinkFactory.orderList())
        assertNotNull(DeepLinkFactory.orderList("completed"))
        assertNotNull(DeepLinkFactory.walletHome())
        assertNotNull(DeepLinkFactory.walletHome("BTC"))
        assertNotNull(DeepLinkFactory.assetDetail("ETH"))
        assertNotNull(DeepLinkFactory.assetDetail("USDT", "ethereum"))
        assertNotNull(DeepLinkFactory.send("BTC"))
        assertNotNull(DeepLinkFactory.send("ETH", "ethereum", "0x123", "1.0"))
        assertNotNull(DeepLinkFactory.receive("BTC"))
        assertNotNull(DeepLinkFactory.receive("USDT", "tron"))
        assertNotNull(DeepLinkFactory.inviteCenter())
        assertNotNull(DeepLinkFactory.inviteCenter("statistics"))
        assertNotNull(DeepLinkFactory.commissionLedger())
        assertNotNull(DeepLinkFactory.commissionLedger("2024-01"))
        assertNotNull(DeepLinkFactory.withdraw("USDT"))
        assertNotNull(DeepLinkFactory.withdraw("BTC", "5.0"))
        assertNotNull(DeepLinkFactory.profile())
        assertNotNull(DeepLinkFactory.legalDocuments())
        assertNotNull(DeepLinkFactory.legalDocuments("terms"))
        assertNotNull(DeepLinkFactory.legalDocumentDetail("doc123"))
        assertNotNull(DeepLinkFactory.legalDocumentDetail("doc123", "privacy"))
    }
    
    // ==================== 构建深层链接测试 ====================
    
    @Test
    fun testBuildDeepLink() {
        val uri = deepLinkHandler.buildDeepLink("vpn_home", mapOf("autoConnect" to "true"))
        
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("vpn_home", uri.pathSegments[0])
        assertEquals("true", uri.getQueryParameter("autoConnect"))
    }
    
    @Test
    fun testBuildHttpsDeepLink() {
        val uri = deepLinkHandler.buildHttpsDeepLink("terms", mapOf("lang" to "zh"))
        
        assertEquals("https", uri.scheme)
        assertEquals("cryptovpn.com", uri.host)
        assertEquals("terms", uri.pathSegments[0])
        assertEquals("zh", uri.getQueryParameter("lang"))
    }
    
    // ==================== Intent深层链接测试 ====================
    
    @Test
    fun testParseFromIntent() {
        val intent = Intent().apply {
            data = Uri.parse("cryptovpn://app/vpn_home")
        }
        
        val result = deepLinkHandler.parse(intent)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
    }
    
    @Test
    fun testParseFromIntent_NoData() {
        val intent = Intent()
        
        val result = deepLinkHandler.parse(intent)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Invalid)
    }
    
    // ==================== 导航管理器深层链接处理测试 ====================
    
    @Test
    fun testNavigationManager_HandleDeepLink() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/vpn_home")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testNavigationManager_HandleDeepLink_Invalid() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/unknown_route")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertFalse(handled)
        }
    }
}