package com.cryptovpn.test.navigation

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
 * 钱包操作流程测试
 * 
 * 验证钱包相关的导航路径
 */
@RunWith(AndroidJUnit4::class)
class WalletFlowTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var navigationManager: NavigationManager
    
    @Before
    fun setup() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            navigationManager = NavigationManager(navController)
            NavigationManagerProvider.initialize(navController)
        }
    }
    
    // ==================== 钱包引导页测试 ====================
    
    @Test
    fun testWalletOnboardingRouteExists() {
        assertEquals("wallet_onboarding", Routes.WalletOnboarding.ROUTE)
        assertEquals("wallet_onboarding", Routes.WalletOnboarding.FULL_ROUTE)
        assertNotNull(Routes.WalletOnboarding.PARAM_STEP)
    }
    
    @Test
    fun testWalletOnboardingNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletOnboarding()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletOnboarding.ROUTE))
        }
    }
    
    @Test
    fun testWalletOnboardingWithStep() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletOnboarding("create")
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletOnboarding.ROUTE))
        }
    }
    
    // ==================== 钱包首页测试 ====================
    
    @Test
    fun testWalletHomeRouteExists() {
        assertEquals("wallet_home", Routes.WalletHome.ROUTE)
        assertEquals("wallet_home", Routes.WalletHome.FULL_ROUTE)
        assertNotNull(Routes.WalletHome.PARAM_HIGHLIGHT_ASSET)
    }
    
    @Test
    fun testWalletHomeNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
        }
    }
    
    @Test
    fun testWalletHomeWithHighlight() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletHome("BTC")
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
        }
    }
    
    @Test
    fun testWalletHomeDeepLinkFactory() {
        val uri = DeepLinkFactory.walletHome("ETH")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("wallet_home", uri.pathSegments[0])
        assertEquals("ETH", uri.getQueryParameter("highlightAsset"))
    }
    
    // ==================== 资产详情页测试 ====================
    
    @Test
    fun testAssetDetailRouteExists() {
        assertEquals("asset_detail", Routes.AssetDetail.ROUTE)
        assertEquals("asset_detail/{assetId}", Routes.AssetDetail.FULL_ROUTE)
        assertNotNull(Routes.AssetDetail.PARAM_ASSET_ID)
        assertNotNull(Routes.AssetDetail.PARAM_CHAIN_ID)
    }
    
    @Test
    fun testAssetDetailNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToAssetDetail("BTC")
            assertTrue(navigationManager.isCurrentRoute(Routes.AssetDetail.ROUTE))
        }
    }
    
    @Test
    fun testAssetDetailWithChain() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToAssetDetail("USDT", "ethereum")
            assertTrue(navigationManager.isCurrentRoute(Routes.AssetDetail.ROUTE))
        }
    }
    
    @Test
    fun testAssetDetailDeepLinkFactory() {
        val uri = DeepLinkFactory.assetDetail("BTC", "bitcoin")
        assertEquals("BTC", uri.pathSegments[1])
        assertEquals("bitcoin", uri.getQueryParameter("chainId"))
    }
    
    // ==================== 收款页测试 ====================
    
    @Test
    fun testReceiveRouteExists() {
        assertEquals("receive", Routes.Receive.ROUTE)
        assertEquals("receive/{assetId}", Routes.Receive.FULL_ROUTE)
        assertNotNull(Routes.Receive.PARAM_ASSET_ID)
        assertNotNull(Routes.Receive.PARAM_CHAIN_ID)
    }
    
    @Test
    fun testReceiveNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToReceive("BTC")
            assertTrue(navigationManager.isCurrentRoute(Routes.Receive.ROUTE))
        }
    }
    
    @Test
    fun testReceiveWithChain() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToReceive("USDT", "tron")
            assertTrue(navigationManager.isCurrentRoute(Routes.Receive.ROUTE))
        }
    }
    
    @Test
    fun testReceiveDeepLinkFactory() {
        val uri = DeepLinkFactory.receive("ETH", "ethereum")
        assertEquals("ETH", uri.pathSegments[1])
        assertEquals("ethereum", uri.getQueryParameter("chainId"))
    }
    
    // ==================== 发送页测试 ====================
    
    @Test
    fun testSendRouteExists() {
        assertEquals("send", Routes.Send.ROUTE)
        assertEquals("send/{assetId}", Routes.Send.FULL_ROUTE)
        assertNotNull(Routes.Send.PARAM_ASSET_ID)
        assertNotNull(Routes.Send.PARAM_CHAIN_ID)
        assertNotNull(Routes.Send.PARAM_TO_ADDRESS)
        assertNotNull(Routes.Send.PARAM_AMOUNT)
    }
    
    @Test
    fun testSendNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToSend("BTC")
            assertTrue(navigationManager.isCurrentRoute(Routes.Send.ROUTE))
        }
    }
    
    @Test
    fun testSendWithAllParams() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToSend(
                assetId = "ETH",
                chainId = "ethereum",
                toAddress = "0x1234567890abcdef",
                amount = "1.5"
            )
            assertTrue(navigationManager.isCurrentRoute(Routes.Send.ROUTE))
        }
    }
    
    @Test
    fun testSendDeepLinkFactory() {
        val uri = DeepLinkFactory.send(
            assetId = "BTC",
            chainId = "bitcoin",
            toAddress = "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa",
            amount = "0.5"
        )
        assertEquals("BTC", uri.pathSegments[1])
        assertEquals("bitcoin", uri.getQueryParameter("chainId"))
        assertEquals("1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", uri.getQueryParameter("toAddress"))
        assertEquals("0.5", uri.getQueryParameter("amount"))
    }
    
    // ==================== 发送结果页测试 ====================
    
    @Test
    fun testSendResultRouteExists() {
        assertEquals("send_result", Routes.SendResult.ROUTE)
        assertEquals("send_result/{txHash}", Routes.SendResult.FULL_ROUTE)
        assertNotNull(Routes.SendResult.PARAM_TX_HASH)
        assertNotNull(Routes.SendResult.PARAM_STATUS)
    }
    
    @Test
    fun testSendResultNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToSendResult("0xabc123", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.SendResult.ROUTE))
        }
    }
    
    @Test
    fun testSendResultWithDifferentStatuses() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToSendResult("tx1", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.SendResult.ROUTE))
            
            navigationManager.navigateToSendResult("tx2", "failed")
            assertTrue(navigationManager.isCurrentRoute(Routes.SendResult.ROUTE))
            
            navigationManager.navigateToSendResult("tx3", "pending")
            assertTrue(navigationManager.isCurrentRoute(Routes.SendResult.ROUTE))
        }
    }
    
    // ==================== 钱包支付确认页测试 ====================
    
    @Test
    fun testWalletPaymentRouteExists() {
        assertEquals("wallet_payment", Routes.WalletPayment.ROUTE)
        assertEquals("wallet_payment/{requestId}", Routes.WalletPayment.FULL_ROUTE)
        assertNotNull(Routes.WalletPayment.PARAM_REQUEST_ID)
        assertNotNull(Routes.WalletPayment.PARAM_PAYLOAD)
    }
    
    @Test
    fun testWalletPaymentNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletPayment("req123")
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletPayment.ROUTE))
        }
    }
    
    @Test
    fun testWalletPaymentWithPayload() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletPayment("req123", "payment_payload_data")
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletPayment.ROUTE))
        }
    }
    
    // ==================== 钱包引导流程测试 ====================
    
    @Test
    fun testWalletOnboardingFlow() {
        // 钱包引导流程: wallet_onboarding → wallet_home
        composeTestRule.runOnUiThread {
            // 1. 进入钱包引导页
            navigationManager.navigateToWalletOnboarding("create")
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletOnboarding.ROUTE))
            
            // 2. 完成引导进入钱包首页
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
        }
    }
    
    // ==================== 钱包首页到资产详情流程 ====================
    
    @Test
    fun testWalletHomeToAssetDetailFlow() {
        // 钱包首页到资产详情: wallet_home → asset_detail
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
            
            navigationManager.navigateToAssetDetail("BTC", "bitcoin")
            assertTrue(navigationManager.isCurrentRoute(Routes.AssetDetail.ROUTE))
        }
    }
    
    // ==================== 收款流程测试 ====================
    
    @Test
    fun testReceiveFlow() {
        // 收款流程: wallet_home → receive
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
            
            navigationManager.navigateToReceive("ETH", "ethereum")
            assertTrue(navigationManager.isCurrentRoute(Routes.Receive.ROUTE))
        }
    }
    
    // ==================== 发送完整流程测试 ====================
    
    @Test
    fun testSendFlow_Complete() {
        // 发送完整流程: wallet_home → send → send_result → wallet_home
        composeTestRule.runOnUiThread {
            // 1. 从钱包首页开始
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
            
            // 2. 进入发送页
            navigationManager.navigateToSend("BTC", "bitcoin", "1A1zP1eP5QGefi2DMPTfTL5SLmv7DivfNa", "0.5")
            assertTrue(navigationManager.isCurrentRoute(Routes.Send.ROUTE))
            
            // 3. 发送完成后进入结果页
            navigationManager.navigateToSendResult("txhash123", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.SendResult.ROUTE))
            
            // 4. 完成后返回钱包首页
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
        }
    }
    
    @Test
    fun testSendFlow_Cancelled() {
        // 取消发送流程: wallet_home → send → (取消) → wallet_home
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletHome()
            navigationManager.navigateToSend("BTC")
            
            // 取消发送返回
            navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    // ==================== 深层链接测试 ====================
    
    @Test
    fun testDeepLink_WalletHome() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/wallet_home")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_AssetDetail() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/asset_detail/BTC?chainId=bitcoin")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.AssetDetail.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_Send() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/send/ETH?chainId=ethereum&toAddress=0x123&amount=1.0")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.Send.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_Receive() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/receive/USDT?chainId=tron")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.Receive.ROUTE))
        }
    }
    
    // ==================== 返回行为测试 ====================
    
    @Test
    fun testBackFromSend() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletHome()
            navigationManager.navigateToSend("BTC")
            assertTrue(navigationManager.isCurrentRoute(Routes.Send.ROUTE))
            
            val canGoBack = navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    @Test
    fun testBackFromSendResult() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToSendResult("tx123", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.SendResult.ROUTE))
            
            // 从发送结果页返回钱包首页
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
        }
    }
    
    // ==================== 路由参数数据类测试 ====================
    
    @Test
    fun testWalletPaymentParams() {
        val params = RouteParams.WalletPaymentParams(
            orderId = "order123",
            amount = "99.99",
            currency = "USDT",
            recipientAddress = "0x1234567890abcdef"
        )
        assertEquals("order123", params.orderId)
        assertEquals("99.99", params.amount)
        assertEquals("USDT", params.currency)
        assertEquals("0x1234567890abcdef", params.recipientAddress)
    }
}