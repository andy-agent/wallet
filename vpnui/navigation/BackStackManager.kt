package com.cryptovpn.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraph.Companion.findStartDestination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * 返回栈管理器
 * 
 * 管理导航返回栈，提供返回拦截和栈清理功能
 */
class BackStackManager(private val navController: NavController) {
    
    // ==================== 返回拦截 ====================
    
    private val _interceptors = mutableListOf<BackInterceptor>()
    
    /**
     * 返回拦截器接口
     */
    fun interface BackInterceptor {
        /**
         * 拦截返回操作
         * @return true 表示拦截，false 表示不拦截
         */
        fun onBackPressed(): Boolean
    }
    
    /**
     * 注册返回拦截器
     */
    fun registerInterceptor(interceptor: BackInterceptor) {
        if (!_interceptors.contains(interceptor)) {
            _interceptors.add(interceptor)
        }
    }
    
    /**
     * 注销返回拦截器
     */
    fun unregisterInterceptor(interceptor: BackInterceptor) {
        _interceptors.remove(interceptor)
    }
    
    /**
     * 处理返回操作
     * @return true 表示已处理，false 表示未处理
     */
    fun handleBackPress(): Boolean {
        // 先检查拦截器
        for (interceptor in _interceptors.reversed()) {
            if (interceptor.onBackPressed()) {
                return true
            }
        }
        
        // 执行默认返回
        return navController.popBackStack()
    }
    
    // ==================== 返回栈状态 ====================
    
    private val _backStackSize = MutableStateFlow(0)
    val backStackSize: StateFlow<Int> = _backStackSize.asStateFlow()
    
    private val _canGoBack = MutableStateFlow(false)
    val canGoBack: StateFlow<Boolean> = _canGoBack.asStateFlow()
    
    init {
        navController.addOnDestinationChangedListener { controller, _, _ ->
            val backQueue = controller.currentBackStack.value
            _backStackSize.value = backQueue.size
            _canGoBack.value = controller.previousBackStackEntry != null
        }
    }
    
    // ==================== 返回栈操作 ====================
    
    /**
     * 获取当前返回栈大小
     */
    fun getBackStackSize(): Int {
        return navController.currentBackStack.value.size
    }
    
    /**
     * 获取返回栈中的所有路由
     */
    fun getBackStackRoutes(): List<String> {
        return navController.currentBackStack.value
            .mapNotNull { it.destination.route }
    }
    
    /**
     * 检查是否可以返回
     */
    fun canGoBack(): Boolean {
        return navController.previousBackStackEntry != null
    }
    
    /**
     * 返回到上一页
     */
    fun goBack(): Boolean {
        return navController.popBackStack()
    }
    
    /**
     * 返回到指定路由
     */
    fun popBackTo(route: String, inclusive: Boolean = false): Boolean {
        return navController.popBackStack(route, inclusive)
    }
    
    /**
     * 返回到起始目的地
     */
    fun popToStart() {
        val startDestination = navController.graph.findStartDestination().route
        startDestination?.let {
            navController.popBackStack(it, false)
        }
    }
    
    /**
     * 返回到根页面（清空返回栈）
     */
    fun popToRoot() {
        val startDestination = navController.graph.findStartDestination().route
        startDestination?.let {
            navController.popBackStack(it, false)
        }
    }
    
    /**
     * 清空返回栈到指定路由
     */
    fun clearStackTo(route: String, inclusive: Boolean = false) {
        while (navController.currentDestination?.route != route && canGoBack()) {
            if (!navController.popBackStack()) break
        }
        if (inclusive && navController.currentDestination?.route == route) {
            navController.popBackStack()
        }
    }
    
    /**
     * 清空整个返回栈
     */
    fun clearStack() {
        while (canGoBack()) {
            if (!navController.popBackStack()) break
        }
    }
    
    // ==================== 特殊返回行为 ====================
    
    /**
     * 返回到VPN首页（用于购买流程完成后）
     */
    fun popToVpnHome() {
        popBackTo(Routes.VpnHome.FULL_ROUTE, false)
    }
    
    /**
     * 返回到钱包首页（用于交易完成后）
     */
    fun popToWalletHome() {
        popBackTo(Routes.WalletHome.FULL_ROUTE, false)
    }
    
    /**
     * 返回到订单列表
     */
    fun popToOrderList() {
        popBackTo(Routes.OrderList.FULL_ROUTE, false)
    }
    
    /**
     * 返回到邀请中心
     */
    fun popToInviteCenter() {
        popBackTo(Routes.InviteCenter.FULL_ROUTE, false)
    }
    
    // ==================== 返回栈保存与恢复 ====================
    
    private var savedBackStack: List<String>? = null
    
    /**
     * 保存当前返回栈
     */
    fun saveBackStack() {
        savedBackStack = getBackStackRoutes()
    }
    
    /**
     * 恢复保存的返回栈
     */
    fun restoreBackStack() {
        savedBackStack?.let { stack ->
            // 恢复逻辑需要根据具体需求实现
            // 这里只是示例
        }
    }
    
    /**
     * 清除保存的返回栈
     */
    fun clearSavedBackStack() {
        savedBackStack = null
    }
    
    // ==================== 返回动画配置 ====================
    
    /**
     * 返回动画类型
     */
    enum class BackAnimation {
        DEFAULT,      // 默认动画
        SLIDE_RIGHT,  // 向右滑出
        FADE_OUT,     // 淡出
        SCALE_DOWN,   // 缩小消失
        NONE          // 无动画
    }
    
    private var currentBackAnimation = BackAnimation.DEFAULT
    
    /**
     * 设置返回动画
     */
    fun setBackAnimation(animation: BackAnimation) {
        currentBackAnimation = animation
    }
    
    /**
     * 获取当前返回动画
     */
    fun getBackAnimation(): BackAnimation {
        return currentBackAnimation
    }
}

/**
 * 返回行为配置
 * 
 * 用于配置特定页面的返回行为
 */
sealed class BackBehavior {
    /**
     * 默认返回行为
     */
    object Default : BackBehavior()
    
    /**
     * 返回时弹出确认对话框
     */
    data class Confirm(val message: String, val title: String? = null) : BackBehavior()
    
    /**
     * 返回时执行自定义操作
     */
    data class Custom(val action: () -> Boolean) : BackBehavior()
    
    /**
     * 禁用返回
     */
    object Disabled : BackBehavior()
    
    /**
     * 返回到指定路由
     */
    data class PopTo(val route: String, val inclusive: Boolean = false) : BackBehavior()
}

/**
 * 返回行为管理器
 * 
 * 管理不同页面的返回行为配置
 */
class BackBehaviorManager {
    
    private val behaviors = mutableMapOf<String, BackBehavior>()
    
    /**
     * 设置页面的返回行为
     */
    fun setBehavior(route: String, behavior: BackBehavior) {
        behaviors[route] = behavior
    }
    
    /**
     * 获取页面的返回行为
     */
    fun getBehavior(route: String): BackBehavior {
        return behaviors[route] ?: BackBehavior.Default
    }
    
    /**
     * 清除页面的返回行为配置
     */
    fun clearBehavior(route: String) {
        behaviors.remove(route)
    }
    
    /**
     * 清除所有返回行为配置
     */
    fun clearAllBehaviors() {
        behaviors.clear()
    }
    
    companion object {
        // 预定义的返回行为配置
        val VPN_PURCHASE_FLOW_BEHAVIOR = BackBehavior.PopTo(Routes.VpnHome.FULL_ROUTE)
        val WALLET_SEND_FLOW_BEHAVIOR = BackBehavior.PopTo(Routes.WalletHome.FULL_ROUTE)
        val FORCE_UPDATE_BEHAVIOR = BackBehavior.Disabled
        
        // 创建确认返回行为
        fun confirmExit(message: String = "确定要退出吗？") = BackBehavior.Confirm(message, "确认退出")
    }
}

/**
 * 页面返回处理器
 * 
 * 用于Compose页面中处理返回操作
 */
class PageBackHandler(
    private val backStackManager: BackStackManager,
    private val behavior: BackBehavior = BackBehavior.Default
) {
    /**
     * 处理返回操作
     */
    fun handleBack(): Boolean {
        return when (behavior) {
            is BackBehavior.Default -> backStackManager.goBack()
            is BackBehavior.Confirm -> {
                // 显示确认对话框的逻辑由调用方处理
                false
            }
            is BackBehavior.Custom -> behavior.action()
            is BackBehavior.Disabled -> true // 返回true表示已处理（拦截）
            is BackBehavior.PopTo -> backStackManager.popBackTo(behavior.route, behavior.inclusive)
        }
    }
}

/**
 * 返回栈监听器
 * 
 * 监听返回栈变化
 */
interface BackStackListener {
    fun onBackStackChanged(routes: List<String>, size: Int)
    fun onRoutePushed(route: String)
    fun onRoutePopped(route: String)
}

/**
 * 返回栈观察者
 */
class BackStackObserver(private val navController: NavController) {
    
    private val listeners = mutableListOf<BackStackListener>()
    private var previousRoutes: List<String> = emptyList()
    
    init {
        navController.addOnDestinationChangedListener { controller, destination, _ ->
            val currentRoutes = controller.currentBackStack.value
                .mapNotNull { it.destination.route }
            
            // 检测新增的路由
            val addedRoutes = currentRoutes - previousRoutes.toSet()
            addedRoutes.forEach { route ->
                listeners.forEach { it.onRoutePushed(route) }
            }
            
            // 检测移除的路由
            val removedRoutes = previousRoutes - currentRoutes.toSet()
            removedRoutes.forEach { route ->
                listeners.forEach { it.onRoutePopped(route) }
            }
            
            // 通知变化
            listeners.forEach { 
                it.onBackStackChanged(currentRoutes, currentRoutes.size) 
            }
            
            previousRoutes = currentRoutes
        }
    }
    
    fun addListener(listener: BackStackListener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener)
        }
    }
    
    fun removeListener(listener: BackStackListener) {
        listeners.remove(listener)
    }
}
