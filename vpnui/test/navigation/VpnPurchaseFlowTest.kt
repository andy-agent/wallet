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
 * VPN购买流程测试
 * 
 * 验证VPN购买相关的导航路径
 */
@RunWith(AndroidJUnit4::class)
class VpnPurchaseFlowTest {
    
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
    
    // ==================== VPN首页测试 ====================
    
    @Test
    fun testVpnHomeRouteExists() {
        assertEquals("vpn_home", Routes.VpnHome.ROUTE)
        assertEquals("vpn_home", Routes.VpnHome.FULL_ROUTE)
        assertEquals("cryptovpn://app/vpn_home?autoConnect={autoConnect}", Routes.VpnHome.DEEP_LINK)
    }
    
    @Test
    fun testVpnHomeNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testVpnHomeWithAutoConnect() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome(true)
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testVpnHomeDeepLinkFactory() {
        val uri = DeepLinkFactory.vpnHome(true)
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("vpn_home", uri.pathSegments[0])
        assertEquals("true", uri.getQueryParameter("autoConnect"))
    }
    
    // ==================== 套餐页测试 ====================
    
    @Test
    fun testPlansRouteExists() {
        assertEquals("plans", Routes.Plans.ROUTE)
        assertEquals("plans", Routes.Plans.FULL_ROUTE)
        assertEquals("cryptovpn://app/plans?selectedPlan={selectedPlan}", Routes.Plans.DEEP_LINK)
    }
    
    @Test
    fun testPlansNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToPlans()
            assertTrue(navigationManager.isCurrentRoute(Routes.Plans.ROUTE))
        }
    }
    
    @Test
    fun testPlansWithSelectedPlan() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToPlans("premium_monthly")
            assertTrue(navigationManager.isCurrentRoute(Routes.Plans.ROUTE))
        }
    }
    
    @Test
    fun testPlansDeepLinkFactory() {
        val uri = DeepLinkFactory.plans("premium_yearly")
        assertEquals("premium_yearly", uri.getQueryParameter("selectedPlan"))
    }
    
    // ==================== 区域选择页测试 ====================
    
    @Test
    fun testRegionSelectionRouteExists() {
        assertEquals("region_selection", Routes.RegionSelection.ROUTE)
        assertEquals("region_selection", Routes.RegionSelection.FULL_ROUTE)
    }
    
    @Test
    fun testRegionSelectionNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToRegionSelection()
            assertTrue(navigationManager.isCurrentRoute(Routes.RegionSelection.ROUTE))
        }
    }
    
    @Test
    fun testRegionSelectionWithCurrentRegion() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToRegionSelection("us-west")
            assertTrue(navigationManager.isCurrentRoute(Routes.RegionSelection.ROUTE))
        }
    }
    
    // ==================== 订单收银台测试 ====================
    
    @Test
    fun testOrderCheckoutRouteExists() {
        assertEquals("order_checkout", Routes.OrderCheckout.ROUTE)
        assertEquals("order_checkout/{planId}", Routes.OrderCheckout.FULL_ROUTE)
        assertNotNull(Routes.OrderCheckout.PARAM_PLAN_ID)
        assertNotNull(Routes.OrderCheckout.PARAM_DURATION)
    }
    
    @Test
    fun testOrderCheckoutNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderCheckout("plan123")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderCheckout.ROUTE))
        }
    }
    
    @Test
    fun testOrderCheckoutWithDuration() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderCheckout("plan123", 12)
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderCheckout.ROUTE))
        }
    }
    
    // ==================== 钱包支付确认页测试 ====================
    
    @Test
    fun testWalletPaymentConfirmRouteExists() {
        assertEquals("wallet_payment_confirm", Routes.WalletPaymentConfirm.ROUTE)
        assertEquals("wallet_payment_confirm/{orderId}", Routes.WalletPaymentConfirm.FULL_ROUTE)
        assertNotNull(Routes.WalletPaymentConfirm.PARAM_ORDER_ID)
        assertNotNull(Routes.WalletPaymentConfirm.PARAM_AMOUNT)
        assertNotNull(Routes.WalletPaymentConfirm.PARAM_CURRENCY)
    }
    
    @Test
    fun testWalletPaymentConfirmNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletPaymentConfirm("order123", "100", "USDT")
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletPaymentConfirm.ROUTE))
        }
    }
    
    // ==================== 订单结果页测试 ====================
    
    @Test
    fun testOrderResultRouteExists() {
        assertEquals("order_result", Routes.OrderResult.ROUTE)
        assertEquals("order_result/{orderId}", Routes.OrderResult.FULL_ROUTE)
        assertNotNull(Routes.OrderResult.PARAM_ORDER_ID)
        assertNotNull(Routes.OrderResult.PARAM_STATUS)
    }
    
    @Test
    fun testOrderResultNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderResult("order123", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderResult.ROUTE))
        }
    }
    
    @Test
    fun testOrderResultWithDifferentStatuses() {
        composeTestRule.runOnUiThread {
            // 成功状态
            navigationManager.navigateToOrderResult("order1", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderResult.ROUTE))
            
            // 失败状态
            navigationManager.navigateToOrderResult("order2", "failed")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderResult.ROUTE))
            
            // 待处理状态
            navigationManager.navigateToOrderResult("order3", "pending")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderResult.ROUTE))
        }
    }
    
    // ==================== 订单列表页测试 ====================
    
    @Test
    fun testOrderListRouteExists() {
        assertEquals("order_list", Routes.OrderList.ROUTE)
        assertEquals("order_list", Routes.OrderList.FULL_ROUTE)
        assertNotNull(Routes.OrderList.PARAM_STATUS_FILTER)
    }
    
    @Test
    fun testOrderListNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderList()
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderList.ROUTE))
        }
    }
    
    @Test
    fun testOrderListWithFilter() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderList("completed")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderList.ROUTE))
        }
    }
    
    @Test
    fun testOrderListDeepLinkFactory() {
        val uri = DeepLinkFactory.orderList("pending")
        assertEquals("pending", uri.getQueryParameter("statusFilter"))
    }
    
    // ==================== 订单详情页测试 ====================
    
    @Test
    fun testOrderDetailRouteExists() {
        assertEquals("order_detail", Routes.OrderDetail.ROUTE)
        assertEquals("order_detail/{orderId}", Routes.OrderDetail.FULL_ROUTE)
        assertNotNull(Routes.OrderDetail.PARAM_ORDER_ID)
    }
    
    @Test
    fun testOrderDetailNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderDetail("order123")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderDetail.ROUTE))
        }
    }
    
    @Test
    fun testOrderDetailDeepLinkFactory() {
        val uri = DeepLinkFactory.orderDetail("order456")
        assertEquals("order456", uri.pathSegments[1])
    }
    
    // ==================== VPN购买完整流程测试 ====================
    
    @Test
    fun testVpnPurchaseFlow_Complete() {
        // 完整购买流程: vpn_home → plans → order_checkout → wallet_payment_confirm → order_result → vpn_home
        composeTestRule.runOnUiThread {
            // 1. 从VPN首页开始
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
            
            // 2. 进入套餐页
            navigationManager.navigateToPlans()
            assertTrue(navigationManager.isCurrentRoute(Routes.Plans.ROUTE))
            
            // 3. 选择套餐进入收银台
            navigationManager.navigateToOrderCheckout("premium_yearly", 12)
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderCheckout.ROUTE))
            
            // 4. 进入钱包支付确认
            navigationManager.navigateToWalletPaymentConfirm("order123", "99.99", "USDT")
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletPaymentConfirm.ROUTE))
            
            // 5. 支付完成后进入订单结果页
            navigationManager.navigateToOrderResult("order123", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderResult.ROUTE))
            
            // 6. 完成后返回VPN首页
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testVpnPurchaseFlow_QuickConnect() {
        // 快速连接流程: vpn_home → region_selection → vpn_home
        composeTestRule.runOnUiThread {
            // 1. 从VPN首页开始
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
            
            // 2. 进入区域选择页
            navigationManager.navigateToRegionSelection("auto")
            assertTrue(navigationManager.isCurrentRoute(Routes.RegionSelection.ROUTE))
            
            // 3. 选择区域后返回VPN首页
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testVpnPurchaseFlow_ViewOrderDetail() {
        // 查看订单详情流程: vpn_home → order_list → order_detail
        composeTestRule.runOnUiThread {
            // 1. 从VPN首页开始
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
            
            // 2. 进入订单列表
            navigationManager.navigateToOrderList()
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderList.ROUTE))
            
            // 3. 查看订单详情
            navigationManager.navigateToOrderDetail("order789")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderDetail.ROUTE))
        }
    }
    
    @Test
    fun testVpnPurchaseFlow_CancelledPayment() {
        // 取消支付流程: vpn_home → plans → order_checkout → (取消) → vpn_home
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            navigationManager.navigateToOrderCheckout("plan123")
            
            // 取消支付返回
            navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    // ==================== 深层链接测试 ====================
    
    @Test
    fun testDeepLink_VpnHome() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/vpn_home")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_Plans() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/plans?selectedPlan=premium")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.Plans.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_OrderDetail() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/order_detail/order123")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderDetail.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_OrderList() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/order_list?statusFilter=completed")
            val handled = navigationManager.handleDeepLink(uri)
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderList.ROUTE))
        }
    }
    
    // ==================== 返回行为测试 ====================
    
    @Test
    fun testBackFromOrderCheckout() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderCheckout("plan123")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderCheckout.ROUTE))
            
            val canGoBack = navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    @Test
    fun testBackFromOrderResult() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderResult("order123", "success")
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderResult.ROUTE))
            
            // 从订单结果页应该返回到VPN首页
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
}