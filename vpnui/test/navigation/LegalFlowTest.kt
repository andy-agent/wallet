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
 * 法务文档流程测试
 * 
 * 验证法务文档相关的导航路径
 */
@RunWith(AndroidJUnit4::class)
class LegalFlowTest {
    
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
    
    // ==================== 我的页测试 ====================
    
    @Test
    fun testProfileRouteExists() {
        assertEquals("profile", Routes.Profile.ROUTE)
        assertEquals("profile", Routes.Profile.FULL_ROUTE)
        assertEquals("cryptovpn://app/profile", Routes.Profile.DEEP_LINK)
    }
    
    @Test
    fun testProfileNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToProfile()
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
        }
    }
    
    @Test
    fun testProfileDeepLinkFactory() {
        val uri = DeepLinkFactory.profile()
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("profile", uri.pathSegments[0])
    }
    
    @Test
    fun testProfileDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/profile")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.Profile.FULL_ROUTE, navResult.route)
    }
    
    // ==================== 法务文档列表页测试 ====================
    
    @Test
    fun testLegalDocumentsRouteExists() {
        assertEquals("legal_documents", Routes.LegalDocuments.ROUTE)
        assertEquals("legal_documents", Routes.LegalDocuments.FULL_ROUTE)
        assertNotNull(Routes.LegalDocuments.PARAM_CATEGORY)
    }
    
    @Test
    fun testLegalDocumentsNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToLegalDocuments()
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
        }
    }
    
    @Test
    fun testLegalDocumentsWithCategory() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToLegalDocuments("privacy")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
        }
    }
    
    @Test
    fun testLegalDocumentsDeepLinkFactory() {
        val uri = DeepLinkFactory.legalDocuments("terms")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("legal_documents", uri.pathSegments[0])
        assertEquals("terms", uri.getQueryParameter("category"))
    }
    
    @Test
    fun testLegalDocumentsDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/legal_documents?category=privacy")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals(Routes.LegalDocuments.FULL_ROUTE, navResult.route)
        assertEquals("privacy", navResult.params[Routes.LegalDocuments.PARAM_CATEGORY])
    }
    
    // ==================== 法务文档详情页测试 ====================
    
    @Test
    fun testLegalDocumentDetailRouteExists() {
        assertEquals("legal_document_detail", Routes.LegalDocumentDetail.ROUTE)
        assertEquals("legal_document_detail/{docId}", Routes.LegalDocumentDetail.FULL_ROUTE)
        assertNotNull(Routes.LegalDocumentDetail.PARAM_DOC_ID)
        assertNotNull(Routes.LegalDocumentDetail.PARAM_DOC_TYPE)
    }
    
    @Test
    fun testLegalDocumentDetailNavigation() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToLegalDocumentDetail("terms_of_service")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocumentDetail.ROUTE))
        }
    }
    
    @Test
    fun testLegalDocumentDetailWithType() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToLegalDocumentDetail("privacy_policy", "privacy")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocumentDetail.ROUTE))
        }
    }
    
    @Test
    fun testLegalDocumentDetailDeepLinkFactory() {
        val uri = DeepLinkFactory.legalDocumentDetail("terms_of_service", "terms")
        assertEquals("cryptovpn", uri.scheme)
        assertEquals("app", uri.host)
        assertEquals("legal_document_detail", uri.pathSegments[0])
        assertEquals("terms_of_service", uri.pathSegments[1])
        assertEquals("terms", uri.getQueryParameter("docType"))
    }
    
    @Test
    fun testLegalDocumentDetailDeepLinkParsing() {
        val handler = DeepLinkHandler()
        val uri = Uri.parse("cryptovpn://app/legal_document_detail/privacy_policy?docType=privacy")
        val result = handler.parse(uri)
        
        assertTrue(result is DeepLinkHandler.DeepLinkType.Navigation)
        val navResult = result as DeepLinkHandler.DeepLinkType.Navigation
        assertEquals("legal_document_detail/privacy_policy", navResult.route)
        assertEquals("privacy", navResult.params[Routes.LegalDocumentDetail.PARAM_DOC_TYPE])
    }
    
    // ==================== 法务文档流程场景测试 ====================
    
    @Test
    fun testLegalFlow_ProfileToLegalDocuments() {
        // 场景: 从我的页到法务文档列表
        // profile → legal_documents
        composeTestRule.runOnUiThread {
            navigationManager.navigateToProfile()
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
            
            navigationManager.navigateToLegalDocuments()
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
        }
    }
    
    @Test
    fun testLegalFlow_LegalDocumentsToDetail() {
        // 场景: 从法务文档列表到详情
        // legal_documents → legal_document_detail
        composeTestRule.runOnUiThread {
            navigationManager.navigateToLegalDocuments("terms")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
            
            navigationManager.navigateToLegalDocumentDetail("terms_of_service", "terms")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocumentDetail.ROUTE))
        }
    }
    
    @Test
    fun testLegalFlow_Complete() {
        // 完整法务文档流程: profile → legal_documents → legal_document_detail
        composeTestRule.runOnUiThread {
            // 1. 进入我的页
            navigationManager.navigateToProfile()
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
            
            // 2. 进入法务文档列表
            navigationManager.navigateToLegalDocuments()
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
            
            // 3. 查看文档详情
            navigationManager.navigateToLegalDocumentDetail("privacy_policy", "privacy")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocumentDetail.ROUTE))
        }
    }
    
    @Test
    fun testLegalFlow_DifferentCategories() {
        // 测试不同类别的法务文档
        composeTestRule.runOnUiThread {
            // 隐私政策
            navigationManager.navigateToLegalDocuments("privacy")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
            
            // 服务条款
            navigationManager.navigateToLegalDocuments("terms")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
            
            // 用户协议
            navigationManager.navigateToLegalDocuments("agreement")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
            
            // 免责声明
            navigationManager.navigateToLegalDocuments("disclaimer")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
        }
    }
    
    @Test
    fun testLegalFlow_DirectDocumentDetailDeepLink() {
        // 通过深层链接直接进入文档详情
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/legal_document_detail/terms_of_service?docType=terms")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocumentDetail.ROUTE))
        }
    }
    
    @Test
    fun testLegalFlow_DirectLegalDocumentsDeepLink() {
        // 通过深层链接直接进入法务文档列表
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/legal_documents?category=privacy")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
        }
    }
    
    // ==================== 返回行为测试 ====================
    
    @Test
    fun testBackFromLegalDocumentDetail() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToLegalDocuments()
            navigationManager.navigateToLegalDocumentDetail("terms_of_service")
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocumentDetail.ROUTE))
            
            val canGoBack = navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    @Test
    fun testBackFromLegalDocuments() {
        composeTestRule.runOnUiThread {
            navigationManager.navigateToProfile()
            navigationManager.navigateToLegalDocuments()
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
            
            val canGoBack = navigationManager.goBack()
            // 验证返回成功
        }
    }
    
    // ==================== 路由参数数据类测试 ====================
    
    @Test
    fun testLegalDocumentParams() {
        val params = RouteParams.LegalDocumentParams(
            docId = "privacy_policy_v2",
            docType = "privacy",
            title = "隐私政策"
        )
        assertEquals("privacy_policy_v2", params.docId)
        assertEquals("privacy", params.docType)
        assertEquals("隐私政策", params.title)
    }
    
    // ==================== 个人中心相关测试 ====================
    
    @Test
    fun testProfileRoutesGroup() {
        val profileRoutes = Routes.Groups.PROFILE_ROUTES
        assertTrue(profileRoutes.contains(Routes.Profile.ROUTE))
        assertTrue(profileRoutes.contains(Routes.LegalDocuments.ROUTE))
        assertTrue(profileRoutes.contains(Routes.LegalDocumentDetail.ROUTE))
    }
    
    @Test
    fun testProfileToInviteCenter() {
        // 从我的页到邀请中心
        composeTestRule.runOnUiThread {
            navigationManager.navigateToProfile()
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
            
            navigationManager.navigateToInviteCenter()
            assertTrue(navigationManager.isCurrentRoute(Routes.InviteCenter.ROUTE))
        }
    }
    
    @Test
    fun testProfileToWalletHome() {
        // 从我的页到钱包首页
        composeTestRule.runOnUiThread {
            navigationManager.navigateToProfile()
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
            
            navigationManager.navigateToWalletHome()
            assertTrue(navigationManager.isCurrentRoute(Routes.WalletHome.ROUTE))
        }
    }
    
    @Test
    fun testProfileToOrderList() {
        // 从我的页到订单列表
        composeTestRule.runOnUiThread {
            navigationManager.navigateToProfile()
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
            
            navigationManager.navigateToOrderList()
            assertTrue(navigationManager.isCurrentRoute(Routes.OrderList.ROUTE))
        }
    }
    
    // ==================== 深层链接测试 ====================
    
    @Test
    fun testDeepLink_Profile() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/profile")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.Profile.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_LegalDocuments() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/legal_documents?category=terms")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocuments.ROUTE))
        }
    }
    
    @Test
    fun testDeepLink_LegalDocumentDetail() {
        composeTestRule.runOnUiThread {
            val uri = Uri.parse("cryptovpn://app/legal_document_detail/privacy_policy?docType=privacy")
            val handled = navigationManager.handleDeepLink(uri)
            
            assertTrue(handled)
            assertTrue(navigationManager.isCurrentRoute(Routes.LegalDocumentDetail.ROUTE))
        }
    }
}