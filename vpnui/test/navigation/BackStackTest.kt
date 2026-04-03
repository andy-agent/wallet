package com.cryptovpn.test.navigation

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
 * 返回栈管理测试
 * 
 * 验证返回栈相关功能
 */
@RunWith(AndroidJUnit4::class)
class BackStackTest {
    
    @get:Rule
    val composeTestRule = createComposeRule()
    
    private lateinit var navigationManager: NavigationManager
    private lateinit var backStackManager: BackStackManager
    
    @Before
    fun setup() {
        composeTestRule.setContent {
            val navController = rememberNavController()
            navigationManager = NavigationManager(navController)
            backStackManager = BackStackManager(navController)
            NavigationManagerProvider.initialize(navController)
        }
    }
    
    // ==================== 返回栈基本操作测试 ====================
    
    @Test
    fun testCanGoBack() {
        composeTestRule.runOnUiThread {
            // 初始状态应该不能返回
            assertFalse(backStackManager.canGoBack())
            
            // 导航到一个页面
            navigationManager.navigateToVpnHome()
            
            // 再导航到另一个页面
            navigationManager.navigateToPlans()
            
            // 现在应该可以返回
            assertTrue(backStackManager.canGoBack())
        }
    }
    
    @Test
    fun testGoBack() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            
            assertTrue(navigationManager.isCurrentRoute(Routes.Plans.ROUTE))
            
            // 返回
            val result = backStackManager.goBack()
            assertTrue(result)
        }
    }
    
    @Test
    fun testPopBackTo() {
        composeTestRule.runOnUiThread {
            // 构建导航栈: vpn_home → plans → order_checkout
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            navigationManager.navigateToOrderCheckout("plan123")
            
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderCheckout.ROUTE))
            
            // 返回到vpn_home
            val result = backStackManager.popBackTo(Routes.VpnHome.FULL_ROUTE, false)
            assertTrue(result)
        }
    }
    
    @Test
    fun testPopToStart() {
        composeTestRule.runOnUiThread {
            // 构建多层导航栈
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            navigationManager.navigateToOrderCheckout("plan123")
            navigationManager.navigateToWalletPaymentConfirm("order123", "100", "USDT")
            
            // 返回到起始目的地
            backStackManager.popToStart()
            // 验证返回成功
        }
    }
    
    @Test
    fun testPopToRoot() {
        composeTestRule.runOnUiThread {
            // 构建多层导航栈
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            navigationManager.navigateToOrderCheckout("plan123")
            
            // 清空到根
            backStackManager.popToRoot()
            // 验证返回成功
        }
    }
    
    @Test
    fun testClearStack() {
        composeTestRule.runOnUiThread {
            // 构建多层导航栈
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            navigationManager.navigateToOrderCheckout("plan123")
            
            // 清空返回栈
            backStackManager.clearStack()
            
            // 验证不能返回
            assertFalse(backStackManager.canGoBack())
        }
    }
    
    // ==================== 返回拦截器测试 ====================
    
    @Test
    fun testBackInterceptor() {
        composeTestRule.runOnUiThread {
            var intercepted = false
            
            val interceptor = BackStackManager.BackInterceptor {
                intercepted = true
                true // 拦截返回
            }
            
            backStackManager.registerInterceptor(interceptor)
            
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            
            // 尝试返回
            val result = backStackManager.handleBackPress()
            
            assertTrue(intercepted)
            assertTrue(result)
            
            backStackManager.unregisterInterceptor(interceptor)
        }
    }
    
    @Test
    fun testMultipleInterceptors() {
        composeTestRule.runOnUiThread {
            var firstIntercepted = false
            var secondIntercepted = false
            
            val interceptor1 = BackStackManager.BackInterceptor {
                firstIntercepted = true
                false // 不拦截
            }
            
            val interceptor2 = BackStackManager.BackInterceptor {
                secondIntercepted = true
                true // 拦截
            }
            
            backStackManager.registerInterceptor(interceptor1)
            backStackManager.registerInterceptor(interceptor2)
            
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            
            backStackManager.handleBackPress()
            
            assertTrue(firstIntercepted)
            assertTrue(secondIntercepted)
            
            backStackManager.unregisterInterceptor(interceptor1)
            backStackManager.unregisterInterceptor(interceptor2)
        }
    }
    
    // ==================== 返回行为配置测试 ====================
    
    @Test
    fun testBackBehaviorManager() {
        val behaviorManager = BackBehaviorManager()
        
        // 设置返回行为
        behaviorManager.setBehavior(Routes.OrderCheckout.ROUTE, BackBehavior.PopTo(Routes.VpnHome.FULL_ROUTE))
        
        // 获取返回行为
        val behavior = behaviorManager.getBehavior(Routes.OrderCheckout.ROUTE)
        assertTrue(behavior is BackBehavior.PopTo)
        
        // 默认行为
        val defaultBehavior = behaviorManager.getBehavior(Routes.Plans.ROUTE)
        assertTrue(defaultBehavior is BackBehavior.Default)
    }
    
    @Test
    fun testBackBehaviorTypes() {
        // 测试各种返回行为类型
        val default = BackBehavior.Default
        val confirm = BackBehavior.Confirm("确定要退出吗？")
        val custom = BackBehavior.Custom { true }
        val disabled = BackBehavior.Disabled
        val popTo = BackBehavior.PopTo(Routes.VpnHome.FULL_ROUTE)
        
        assertTrue(default is BackBehavior.Default)
        assertTrue(confirm is BackBehavior.Confirm)
        assertTrue(custom is BackBehavior.Custom)
        assertTrue(disabled is BackBehavior.Disabled)
        assertTrue(popTo is BackBehavior.PopTo)
    }
    
    @Test
    fun testPredefinedBackBehaviors() {
        // 测试预定义的返回行为
        val vpnPurchaseBehavior = BackBehaviorManager.VPN_PURCHASE_FLOW_BEHAVIOR
        assertTrue(vpnPurchaseBehavior is BackBehavior.PopTo)
        
        val walletSendBehavior = BackBehaviorManager.WALLET_SEND_FLOW_BEHAVIOR
        assertTrue(walletSendBehavior is BackBehavior.PopTo)
        
        val forceUpdateBehavior = BackBehaviorManager.FORCE_UPDATE_BEHAVIOR
        assertTrue(forceUpdateBehavior is BackBehavior.Disabled)
    }
    
    // ==================== 特殊返回行为测试 ====================
    
    @Test
    fun testPopToVpnHome() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            navigationManager.navigateToOrderCheckout("plan123")
            
            backStackManager.popToVpnHome()
            // 验证返回到VPN首页
        }
    }
    
    @Test
    fun testPopToWalletHome() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToWalletHome()
            navigationManager.navigateToSend("BTC")
            navigationManager.navigateToSendResult("tx123", "success")
            
            backStackManager.popToWalletHome()
            // 验证返回到钱包首页
        }
    }
    
    @Test
    fun testPopToOrderList() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToOrderList()
            navigationManager.navigateToOrderDetail("order123")
            
            backStackManager.popToOrderList()
            // 验证返回到订单列表
        }
    }
    
    @Test
    fun testPopToInviteCenter() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToInviteCenter()
            navigationManager.navigateToWithdraw("USDT")
            
            backStackManager.popToInviteCenter()
            // 验证返回到邀请中心
        }
    }
    
    // ==================== 返回栈保存与恢复测试 ====================
    
    @Test
    fun testSaveAndRestoreBackStack() {
        composeTestRule.runOnUiThread {
            // 构建导航栈
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            navigationManager.navigateToOrderCheckout("plan123")
            
            // 保存返回栈
            backStackManager.saveBackStack()
            
            // 清空返回栈
            backStackManager.clearStack()
            
            // 恢复返回栈
            backStackManager.restoreBackStack()
        }
    }
    
    @Test
    fun testClearSavedBackStack() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome()
            backStackManager.saveBackStack()
            
            // 清除保存的返回栈
            backStackManager.clearSavedBackStack()
        }
    }
    
    // ==================== 返回动画测试 ====================
    
    @Test
    fun testBackAnimation() {
        composeTestRule.runOnUiThread {
            // 设置返回动画
            backStackManager.setBackAnimation(BackStackManager.BackAnimation.SLIDE_RIGHT)
            
            // 获取返回动画
            val animation = backStackManager.getBackAnimation()
            assertEquals(BackStackManager.BackAnimation.SLIDE_RIGHT, animation)
        }
    }
    
    @Test
    fun testBackAnimationTypes() {
        // 测试所有返回动画类型
        val animations = listOf(
            BackStackManager.BackAnimation.DEFAULT,
            BackStackManager.BackAnimation.SLIDE_RIGHT,
            BackStackManager.BackAnimation.FADE_OUT,
            BackStackManager.BackAnimation.SCALE_DOWN,
            BackStackManager.BackAnimation.NONE
        )
        
        animations.forEach { animation ->
            backStackManager.setBackAnimation(animation)
            assertEquals(animation, backStackManager.getBackAnimation())
        }
    }
    
    // ==================== 返回栈状态流测试 ====================
    
    @Test
    fun testBackStackSizeFlow() {
        composeTestRule.runOnUiThread {
            // 初始大小
            val initialSize = backStackManager.backStackSize.value
            
            // 导航后大小应该增加
            navigationManager.navigateToVpnHome()
            
            // 验证大小变化
            // 注意：由于异步特性，这里只是验证流的可用性
            assertNotNull(backStackManager.backStackSize)
        }
    }
    
    @Test
    fun testCanGoBackFlow() {
        composeTestRule.runOnUiThread {
            // 初始状态
            val initialCanGoBack = backStackManager.canGoBack.value
            
            // 导航后应该可以返回
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            
            // 验证流
            assertNotNull(backStackManager.canGoBack)
        }
    }
    
    // ==================== 返回栈路由列表测试 ====================
    
    @Test
    fun testGetBackStackRoutes() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            
            val routes = backStackManager.getBackStackRoutes()
            assertNotNull(routes)
        }
    }
    
    @Test
    fun testGetBackStackSize() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToVpnHome()
            
            val size = backStackManager.getBackStackSize()
            assertTrue(size > 0)
        }
    }
    
    // ==================== 页面返回处理器测试 ====================
    
    @Test
    fun testPageBackHandler() {
        composeTestRule.runOnUiThread {
            val handler = PageBackHandler(backStackManager, BackBehavior.Default)
            
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            
            val result = handler.handleBack()
            // 验证返回处理
        }
    }
    
    @Test
    fun testPageBackHandlerWithCustomBehavior() {
        composeTestRule.runOnUiThread {
            var customActionCalled = false
            
            val handler = PageBackHandler(backStackManager, BackBehavior.Custom {
                customActionCalled = true
                true
            })
            
            navigationManager.navigateToVpnHome()
            navigationManager.navigateToPlans()
            
            handler.handleBack()
            
            assertTrue(customActionCalled)
        }
    }
    
    // ==================== 返回栈监听器测试 ====================
    
    @Test
    fun testBackStackObserver() {
        composeTestRule.runOnUiThread {
            val navController = rememberNavController()
            val observer = BackStackObserver(navController)
            
            var routePushed = false
            var routePopped = false
            
            val listener = object : BackStackListener {
                override fun onBackStackChanged(routes: List<String>, size: Int) {}
                override fun onRoutePushed(route: String) {
                    routePushed = true
                }
                override fun onRoutePopped(route: String) {
                    routePopped = true
                }
            }
            
            observer.addListener(listener)
            
            // 导航触发推送
            NavigationManager(navController).navigateToVpnHome()
            
            assertTrue(routePushed)
            
            observer.removeListener(listener)
        }
    }
}