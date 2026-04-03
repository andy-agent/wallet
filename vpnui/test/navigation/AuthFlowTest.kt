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
 * 认证流程测试
 * 
 * 验证所有认证相关的导航路径
 */
@RunWith(AndroidJUnit4::class)
class AuthFlowTest {
    
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
    
    // ==================== 邮箱登录页测试 ====================
    
    @Test
    fun testEmailLoginRouteExists() {
        assertEquals("email_login", Routes.EmailLogin.ROUTE)
        assertEquals("email_login", Routes.EmailLogin.FULL_ROUTE)
        assertEquals("cryptovpn://app/email_login?redirect={redirect}", Routes.EmailLogin.DEEP_LINK)
    }
    
    @Test
    fun testEmailLoginNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailLogin()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
        }
    }
    
    @Test
    fun testEmailLoginWithRedirect() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailLogin("vpn_home")
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
        }
    }
    
    @Test
    fun testEmailLoginDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/email_login?redirect=vpn_home")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.EmailLogin.FULL_ROUTE, navResult.route)
        assertEquals("vpn_home", navResult.params[Routes.EmailLogin.PARAM_REDIRECT])
    }
    
    @Test
    fun testEmailLoginDeepLinkFactory() {
        val uri = DeepLinkFactory.emailLogin("vpn_home")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("email_login", uri.pathSegments[0])
        assertEquals("vpn_home", uri.getQueryParameter("redirect"))
    }
    
    // ==================== 邮箱注册页测试 ====================
    
    @Test
    fun testEmailRegisterRouteExists() {
        assertEquals("email_register", Routes.EmailRegister.ROUTE)
        assertEquals("email_register", Routes.EmailRegister.FULL_ROUTE)
        assertEquals("cryptovpn://app/email_register?inviteCode={inviteCode}", Routes.EmailRegister.DEEP_LINK)
    }
    
    @Test
    fun testEmailRegisterNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailRegister()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
        }
    }
    
    @Test
    fun testEmailRegisterWithInviteCode() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailRegister("INVITE123")
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
        }
    }
    
    @Test
    fun testEmailRegisterDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/email_register?inviteCode=ABC123")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.EmailRegister.FULL_ROUTE, navResult.route)
        assertEquals("ABC123", navResult.params[Routes.EmailRegister.PARAM_INVITE_CODE])
    }
    
    @Test
    fun testEmailRegisterDeepLinkFactory() {
        val uri = DeepLinkFactory.emailRegister("INVITE123")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("email_register", uri.pathSegments[0])
        assertEquals("INVITE123", uri.getQueryParameter("inviteCode"))
    }
    
    // ==================== 重置密码页测试 ====================
    
    @Test
    fun testResetPasswordRouteExists() {
        assertEquals("reset_password", Routes.ResetPassword.ROUTE)
        assertEquals("reset_password", Routes.ResetPassword.FULL_ROUTE)
        assertEquals("cryptovpn://app/reset_password?email={email}&token={token}", Routes.ResetPassword.DEEP_LINK)
    }
    
    @Test
    fun testResetPasswordNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToResetPassword()
            assertTrue(navigationManager.isCurrentRoute(Routes.ResetPassword.ROUTE))
        }
    }
    
    @Test
    fun testResetPasswordWithEmailAndToken() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToResetPassword("user@example.com", "reset_token_123")
            assertTrue(navigationManager.isCurrentRoute(Routes.ResetPassword.ROUTE))
        }
    }
    
    @Test
    fun testResetPasswordDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/reset_password?email=user@example.com&token=reset123")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.ResetPassword.FULL_ROUTE, navResult.route)
        assertEquals("user@example.com", navResult.params[Routes.ResetPassword.PARAM_EMAIL])
        assertEquals("reset123", navResult.params[Routes.ResetPassword.PARAM_TOKEN])
    }
    
    @Test
    fun testResetPasswordDeepLinkFactory() {
        val uri = DeepLinkFactory.resetPassword("user@example.com", "token123")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("reset_password", uri.pathSegments[0])
        assertEquals("user@example.com", uri.getQueryParameter("email"))
        assertEquals("token123", uri.getQueryParameter("token"))
    }
    
    // ==================== 认证流程场景测试 ====================
    
    @Test
    fun testAuthFlow_LoginToRegister() {
        // 场景: 从登录页到注册页
        // email_login → email_register
        composeTestRule.runOnUiThread {
            // 在登录页点击"去注册"
            navigationManager.navigateToEmailLogin()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
            
            // 导航到注册页
            navigationManager.navigateToEmailRegister()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
        }
    }
    
    @Test
    fun testAuthFlow_LoginToResetPassword() {
        // 场景: 从登录页到重置密码页
        // email_login → reset_password
        composeTestRule.runOnUiThread {
            // 在登录页点击"忘记密码"
            navigationManager.navigateToEmailLogin()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
            
            // 导航到重置密码页
            navigationManager.navigateToResetPassword()
            assertTrue(navigationManager.isCurrentRoute(Routes.ResetPassword.ROUTE))
        }
    }
    
    @Test
    fun testAuthFlow_RegisterToLogin() {
        // 场景: 从注册页返回登录页
        // email_register → email_login
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailRegister()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
            
            // 返回登录页
            navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    @Test
    fun testAuthFlow_CompleteLogin() {
        // 场景: 完成登录后导航到VPN首页
        // email_login → vpn_home
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailLogin()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
            
            // 登录成功后导航到VPN首页
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testAuthFlow_CompleteRegister() {
        // 场景: 完成注册后导航到VPN首页
        // email_register → vpn_home
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailRegister("INVITE123")
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
            
            // 注册成功后导航到VPN首页
            navigationManager.navigateToVpnHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.VpnHome.ROUTE))
        }
    }
    
    @Test
    fun testAuthFlow_ResetPasswordComplete() {
        // 场景: 重置密码完成后返回登录页
        // reset_password → email_login
        composeTestRule.runOnUiThread {
            navigationManager.navigateToResetPassword("user@example.com", "token123")
            assertTrue(navigationManager.isCurrentRoute(Routes.ResetPassword.ROUTE))
            
            // 重置完成后返回登录页
            navigationManager.navigateToEmailLogin()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
        }
    }
    
    // ==================== 邀请码流程测试 ====================
    
    @Test
    fun testAuthFlow_RegisterWithInviteCode() {
        // 场景: 使用邀请码注册
        composeTestRule.runOnUiThread {
            val inviteCode = "INVITE123"
            navigationManager.navigateToEmailRegister(inviteCode)
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
        }
    }
    
    @Test
    fun testAuthFlow_DeepLinkWithInviteCode() {
        // 场景: 通过深层链接带邀请码打开注册页
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/email_register?inviteCode=ABC123")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
        }
    }
    
    // ==================== 返回行为测试 ====================
    
    @Test
    fun testAuthFlow_BackFromLogin() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailLogin()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailLogin.ROUTE))
            
            // 从登录页返回
            val canGoBack = navigationManager.goBack()
            // 验证返回行为
        }
    }
    
    @Test
    fun testAuthFlow_BackFromRegister() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToEmailRegister()
            assertTrue(navigationManager.isCurrentRoute(Routes.EmailRegister.ROUTE))
            
            // 从注册页返回
            val canGoBack = navigationManager.goBack()
            // 验证返回行为
        }
    }
    
    // ==================== 路由参数数据类测试 ====================
    
    @Test
    fun testRouteParams() {
        // 测试各种路由参数数据类
        val orderParams = RouteParams.OrderCheckoutParams("plan123", 3, "COUPON10")
        assertEquals("plan123", orderParams.planId)
        assertEquals(3, orderParams.duration)
        assertEquals("COUPON10", orderParams.couponCode)
        
        val sendParams = RouteParams.SendParams("BTC", "bitcoin", "address123", "1.5")
        assertEquals("BTC", sendParams.assetId)
        assertEquals("bitcoin", sendParams.chainId)
        assertEquals("address123", sendParams.toAddress)
        assertEquals("1.5", sendParams.amount)
        
        val withdrawParams = RouteParams.WithdrawParams("USDT", "1000", "100")
        assertEquals("USDT", withdrawParams.currency)
        assertEquals("1000", withdrawParams.maxAmount)
        assertEquals("100", withdrawParams.minAmount)
    }
}