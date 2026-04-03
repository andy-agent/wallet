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
 * 启动流程测试
 * 
 * 验证所有启动相关的导航路径
 */
@RunWith(AndroidJUnit4::class)
class LaunchFlowTest {
    
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
    
    // ==================== Splash 导航测试 ====================
    
    @Test
    fun testSplashRouteExists() {
        // 验证Splash路由常量
        assertEquals("splash", Routes.Splash.ROUTE)
        assertEquals("splash", Routes.Splash.FULL_ROUTE)
        assertEquals("cryptovpn://app/splash", Routes.Splash.DEEP_LINK)
    }
    
    @Test
    fun testSplashDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/splash")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.Splash.FULL_ROUTE, navResult.route)
    }
    
    // ==================== 强制更新导航测试 ====================
    
    @Test
    fun testForceUpdateRouteExists() {
        assertEquals("force_update", Routes.ForceUpdate.ROUTE)
        assertNotNull(Routes.ForceUpdate.PARAM_VERSION)
        assertNotNull(Routes.ForceUpdate.PARAM_DOWNLOAD_URL)
    }
    
    @Test
    fun testForceUpdateDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/force_update?version=2.0.0&downloadUrl=https://example.com/app.apk")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.ForceUpdate.ROUTE, navResult.route)
        assertEquals("2.0.0", navResult.params[Routes.ForceUpdate.PARAM_VERSION])
        assertEquals("https://example.com/app.apk", navResult.params[Routes.ForceUpdate.PARAM_DOWNLOAD_URL])
    }
    
    @Test
    fun testForceUpdateDeepLinkFactory() {
        val uri = DeepLinkFactory.forceUpdate("2.0.0", "https://example.com/app.apk")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("force_update", uri.pathSegments[0])
        assertEquals("2.0.0", uri.getQueryParameter("version"))
        assertEquals("https://example.com/app.apk", uri.getQueryParameter("downloadUrl"))
    }
    
    // ==================== 可选更新导航测试 ====================
    
    @Test
    fun testOptionalUpdateRouteExists() {
        assertEquals("optional_update", Routes.OptionalUpdate.ROUTE)
        assertNotNull(Routes.OptionalUpdate.PARAM_VERSION)
        assertNotNull(Routes.OptionalUpdate.PARAM_DOWNLOAD_URL)
    }
    
    // ==================== 启动流程场景测试 ====================
    
    @Test
    fun testLaunchFlow_UnauthenticatedUser() {
        // 场景: 未登录用户从Splash到登录页
        // splash → email_login
        composeTestRule.runOnUiThread {
            // 模拟未登录状态
            val isLoggedIn = false
            
            if (!isLoggedIn) {
                navigationManager.navigateToEmailLogin()
            }
            
            // 验证当前路由
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
        }
    }
    
    @Test
    fun testLaunchFlow_AuthenticatedUser() {
        // 场景: 已登录用户从Splash到VPN首页
        // splash → vpn_home
        composeTestRule.runOnUiThread {
            // 模拟已登录状态
            val isLoggedIn = true
            
            if (isLoggedIn) {
                navigationManager.navigateToVpnHome()
            }
            
            // 验证当前路由
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testLaunchFlow_ForceUpdateRequired() {
        // 场景: 需要强制更新
        // splash → force_update
        composeTestRule.runOnUiThread {
            val needForceUpdate = true
            
            if (needForceUpdate) {
                navigationManager.navigateToForceUpdate("2.0.0", "https://example.com/app.apk")
            }
            
            // 验证当前路由
            assertTrue(navigationManager.isCurrentRoute(Routes.ForceUpdate.ROUTE))
        }
    }
    
    @Test
    fun testLaunchFlow_OptionalUpdateAvailable() {
        // 场景: 可选更新可用
        // splash → optional_update (弹窗)
        composeTestRule.runOnUiThread {
            val hasOptionalUpdate = true
            
            if (hasOptionalUpdate) {
                navigationManager.navigateToOptionalUpdate("1.5.0", "https://example.com/app.apk")
            }
            
            // 验证当前路由
            assertTrue(navigationManager.isCurrentRoute(Routes.OptionalUpdate.ROUTE))
        }
    }
    
    // ==================== 深层链接启动测试 ====================
    
    @Test
    fun testDeepLinkLaunch_VpnHome() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/vpn_home")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testDeepLinkLaunch_EmailLogin() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/email_login")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
        }
    }
    
    @Test
    fun testDeepLinkLaunch_WithAutoConnect() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/vpn_home?autoConnect=true")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    // ==================== 路由参数测试 ====================
    
    @Test
    fun testRouteArgsConstants() {
        // 验证所有路由参数常量
        assertNotNull(Routes.Args.PLAN_ID)
        assertNotNull(Routes.Args.ORDER_ID)
        assertNotNull(Routes.Args.ASSET_ID)
        assertNotNull(Routes.Args.CHAIN_ID)
        assertNotNull(Routes.Args.TX_HASH)
        assertNotNull(Routes.Args.DOC_ID)
        assertNotNull(Routes.Args.EMAIL)
        assertNotNull(Routes.Args.TOKEN)
        assertNotNull(Routes.Args.INVITE_CODE)
        assertNotNull(Routes.Args.VERSION)
        assertNotNull(Routes.Args.DOWNLOAD_URL)
        assertNotNull(Routes.Args.STATUS)
        assertNotNull(Routes.Args.AMOUNT)
        assertNotNull(Routes.Args.CURRENCY)
    }
    
    // ==================== 路由分组测试 ====================
    
    @Test
    fun testAuthRoutesGroup() {
        val authRoutes = Routes.Groups.AUTH_ROUTES
        assertTrue(authRoutes.contains(Routes.EmailLogin.ROUTE))
        assertTrue(authRoutes.contains(Routes.EmailRegister.ROUTE))
        assertTrue(authRoutes.contains(Routes.ResetPassword.ROUTE))
    }
    
    @Test
    fun testVpnRoutesGroup() {
        val vpnRoutes = Routes.Groups.VPN_ROUTES
        assertTrue(vpnRoutes.contains(Routes.VpnHome.ROUTE))
        assertTrue(vpnRoutes.contains(Routes.Plans.ROUTE))
        assertTrue(vpnRoutes.contains(Routes.OrderCheckout.ROUTE))
        assertTrue(vpnRoutes.contains(Routes.OrderDetail.ROUTE))
    }
    
    @Test
    fun testWalletRoutesGroup() {
        val walletRoutes = Routes.Groups.WALLET_ROUTES
        assertTrue(walletRoutes.contains(Routes.WalletHome.ROUTE))
        assertTrue(walletRoutes.contains(Routes.AssetDetail.ROUTE))
        assertTrue(walletRoutes.contains(Routes.Send.ROUTE))
        assertTrue(walletRoutes.contains(Routes.Receive.ROUTE))
    }
    
    @Test
    fun testProtectedRoutesGroup() {
        val protectedRoutes = Routes.Groups.PROTECTED_ROUTES
        // 验证受保护路由包含VPN、钱包、增长和个人中心路由
        assertTrue(protectedRoutes.contains(Routes.VpnHome.ROUTE))
        assertTrue(protectedRoutes.contains(Routes.WalletHome.ROUTE))
        assertTrue(protectedRoutes.contains(Routes.InviteCenter.ROUTE))
        assertTrue(protectedRoutes.contains(Routes.Profile.ROUTE))
    }
}
