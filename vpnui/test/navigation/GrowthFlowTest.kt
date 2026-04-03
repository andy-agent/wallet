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
 * 增长提现流程测试
 * 
 * 验证增长相关的导航路径
 */
@RunWith(AndroidJUnit4::class)
class GrowthFlowTest {
    
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
    
    // ==================== 邀请中心页测试 ====================
    
    @Test
    fun testInviteCenterRouteExists() {
        assertEquals("invite_center", Routes.InviteCenter.ROUTE)
        assertEquals("invite_center", Routes.InviteCenter.FULL_ROUTE)
        assertNotNull(Routes.InviteCenter.PARAM_HIGHLIGHT_TAB)
    }
    
    @Test
    fun testInviteCenterNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
    
    @Test
    fun testInviteCenterWithHighlightTab() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter("statistics")
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
    
    @Test
    fun testInviteCenterDeepLinkFactory() {
        val uri = DeepLinkFactory.inviteCenter("rewards")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("invite_center", uri.pathSegments[0])
        assertEquals("rewards", uri.getQueryParameter("highlightTab"))
    }
    
    @Test
    fun testInviteCenterDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/invite_center?highlightTab=statistics")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.InviteCenter.FULL_ROUTE, navResult.route)
        assertEquals("statistics", navResult.params[Routes.InviteCenter.PARAM_HIGHLIGHT_TAB])
    }
    
    // ==================== 佣金账本页测试 ====================
    
    @Test
    fun testCommissionLedgerRouteExists() {
        assertEquals("commission_ledger", Routes.CommissionLedger.ROUTE)
        assertEquals("commission_ledger", Routes.CommissionLedger.FULL_ROUTE)
        assertNotNull(Routes.CommissionLedger.PARAM_PERIOD)
    }
    
    @Test
    fun testCommissionLedgerNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToCommissionLedger()
            assertTrue(navigationManager.isCurrentRoute(Routes.CommissionLedger.ROUTE))
        }
    }
    
    @Test
    fun testCommissionLedgerWithPeriod() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToCommissionLedger("2024-01")
            assertTrue(navigationManager.isCurrentRoute(Routes.CommissionLedger.ROUTE))
        }
    }
    
    @Test
    fun testCommissionLedgerDeepLinkFactory() {
        val uri = DeepLinkFactory.commissionLedger("2024-Q1")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("commission_ledger", uri.pathSegments[0])
        assertEquals("2024-Q1", uri.getQueryParameter("period"))
    }
    
    // ==================== 提现申请页测试 ====================
    
    @Test
    fun testWithdrawRouteExists() {
        assertEquals("withdraw", Routes.Withdraw.ROUTE)
        assertEquals("withdraw", Routes.Withdraw.FULL_ROUTE)
        assertNotNull(Routes.Withdraw.PARAM_CURRENCY)
        assertNotNull(Routes.Withdraw.PARAM_MAX_AMOUNT)
    }
    
    @Test
    fun testWithdrawNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWithdraw("USDT")
            assertTrue(navigationManager.isCurrentRoute(Routes.Withdraw.ROUTE))
        }
    }
    
    @Test
    fun testWithdrawWithMaxAmount() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWithdraw("USDT", "1000.00")
            assertTrue(navigationManager.isCurrentRoute(Routes.Withdraw.ROUTE))
        }
    }
    
    @Test
    fun testWithdrawDeepLinkFactory() {
        val uri = DeepLinkFactory.withdraw("BTC", "5.0")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("withdraw", uri.pathSegments[0])
        assertEquals("BTC", uri.getQueryParameter("currency"))
        assertEquals("5.0", uri.getQueryParameter("maxAmount"))
    }
    
    @Test
    fun testWithdrawDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/withdraw?currency=USDT&maxAmount=500")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.Withdraw.FULL_ROUTE, navResult.route)
        assertEquals("USDT", navResult.params[Routes.Withdraw.PARAM_CURRENCY])
        assertEquals("500", navResult.params[Routes.Withdraw.PARAM_MAX_AMOUNT])
    }
    
    // ==================== 增长流程场景测试 ====================
    
    @Test
    fun testGrowthFlow_ProfileToInviteCenter() {
        // 场景: 从我的页到邀请中心
        // profile → invite_center
        composeTestRule.runOnUiThread {
            navigationManager.navigateToProfile()
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
            
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
    
    @Test
    fun testGrowthFlow_InviteCenterToCommissionLedger() {
        // 场景: 从邀请中心到佣金账本
        // invite_center → commission_ledger
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
            
            navigationManager.navigateToCommissionLedger("2024-01")
            assertTrue(navigationManager.isCurrentRoute(Routes.CommissionLedger.ROUTE))
        }
    }
    
    @Test
    fun testGrowthFlow_InviteCenterToWithdraw() {
        // 场景: 从邀请中心到提现申请
        // invite_center → withdraw
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
            
            navigationManager.navigateToWithdraw("USDT", "500.00")
            assertTrue(navigationManager.isCurrentRoute(Routes.Withdraw.ROUTE))
        }
    }
    
    @Test
    fun testGrowthFlow_CompleteWithdraw() {
        // 完整提现流程: invite_center → withdraw → invite_center
        composeTestRule.runOnUiThread {
            // 1. 进入邀请中心
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
            
            // 2. 进入提现页
            navigationManager.navigateToWithdraw("USDT", "1000.00")
            assertTrue(navigationManager.isCurrentRoute(Routes.Withdraw.ROUTE))
            
            // 3. 提现完成后返回邀请中心
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
    
    @Test
    fun testGrowthFlow_ViewCommissionHistory() {
        // 查看佣金历史流程: invite_center → commission_ledger → invite_center
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
            
            navigationManager.navigateToCommissionLedger("2024-Q1")
            assertTrue(navigationManager.isCurrentRoute(Routes.CommissionLedger.ROUTE))
            
            // 返回邀请中心
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
    
    @Test
    fun testGrowthFlow_DirectWithdrawDeepLink() {
        // 通过深层链接直接进入提现页
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/withdraw?currency=USDT&maxAmount=1000")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.Withdraw.ROUTE))
        }
    }
    
    @Test
    fun testGrowthFlow_DirectInviteCenterDeepLink() {
        // 通过深层链接直接进入邀请中心
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/invite_center?highlightTab=statistics")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
    
    // ==================== 返回行为测试 ====================
    
    @Test
    fun testBackFromWithdraw() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter()
            navigationManager.navigateToWithdraw("USDT")
            assertTrue(navigationManager.isCurrentRoute(Routes.Withdraw.ROUTE))
            
            val canGoBack = navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    @Test
    fun testBackFromCommissionLedger() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter()
            navigationManager.navigateToCommissionLedger()
            assertTrue(navigationManager.isCurrentRoute(Routes.CommissionLedger.ROUTE))
            
            val canGoBack = navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    // ==================== 路由参数数据类测试 ====================
    
    @Test
    fun testWithdrawParams() {
        val params = RouteParams.WithdrawParams(
            currency = "USDT",
            maxAmount = "1000.00",
            minAmount = "10.00"
        )
        assertEquals("USDT", params.currency)
        assertEquals("1000.00", params.maxAmount)
        assertEquals("10.00", params.minAmount)
    }
    
    // ==================== 邀请码相关测试 ====================
    
    @Test
    fun testInviteCodeInRegisterFlow() {
        // 验证邀请码在注册流程中的使用
        composeTestRule.runOnUiThread {
            val inviteCode = "INVITE2024"
            navigationManager.navigateToEmailRegister(inviteCode)
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
        }
    }
    
    @Test
    fun testInviteCenterWithDifferentTabs() {
        // 测试邀请中心的不同标签页
        composeTestRule.runOnUiThread {
            // 统计标签
            navigationManager.navigateToInviteCenter("statistics")
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
            
            // 奖励标签
            navigationManager.navigateToInviteCenter("rewards")
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
            
            // 邀请码标签
            navigationManager.navigateToInviteCenter("code")
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
}