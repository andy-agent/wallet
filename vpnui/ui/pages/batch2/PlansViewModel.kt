package com.cryptovpn.ui.pages.plans

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * 套餐页面 ViewModel
 * 管理套餐列表、选择状态、购买流程等
 */
@HiltViewModel
class PlansViewModel @Inject constructor() : ViewModel() {

    private val _state = MutableStateFlow(PlansPageState())
    val state: StateFlow<PlansPageState> = _state.asStateFlow()

    init {
        loadPlans()
    }

    /**
     * 加载套餐列表
     */
    fun loadPlans() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            
            // 模拟网络请求延迟
            delay(800)
            
            // 设置示例数据
            val recommendedPlan = samplePlans.find { it.isRecommended }
            val regularPlans = samplePlans.filter { !it.isRecommended }
            
            _state.update {
                it.copy(
                    isLoading = false,
                    recommendedPlan = recommendedPlan,
                    regularPlans = regularPlans
                )
            }
        }
    }

    /**
     * 选择套餐
     */
    fun selectPlan(planId: String) {
        _state.update {
            it.copy(selectedPlanId = planId)
        }
    }

    /**
     * 获取选中的套餐
     */
    fun getSelectedPlan(): Plan? {
        val selectedId = _state.value.selectedPlanId ?: return null
        return getPlanById(selectedId)
    }

    /**
     * 根据ID获取套餐
     */
    fun getPlanById(planId: String): Plan? {
        val allPlans = listOfNotNull(
            _state.value.recommendedPlan,
            *_state.value.regularPlans.toTypedArray()
        )
        return allPlans.find { it.id == planId }
    }

    /**
     * 刷新套餐列表
     */
    fun refreshPlans() {
        loadPlans()
    }

    /**
     * 清除错误信息
     */
    fun clearError() {
        _state.update { it.copy(errorMessage = null) }
    }

    /**
     * 设置错误信息
     */
    fun setError(message: String) {
        _state.update { it.copy(errorMessage = message) }
    }

    companion object {
        /**
         * 示例套餐数据
         */
        val samplePlans = listOf(
            Plan(
                id = "pro_yearly",
                name = "专业版年付",
                description = "最受欢迎的选择",
                originalPrice = 299.0,
                discountedPrice = 199.0,
                durationDays = 365,
                isRecommended = true,
                badge = "推荐",
                features = listOf(
                    PlanFeature("全球 50+ 节点", true),
                    PlanFeature("不限流量", true),
                    PlanFeature("5 台设备同时在线", true),
                    PlanFeature("专属客服支持", false),
                    PlanFeature("智能路由优化", false)
                )
            ),
            Plan(
                id = "pro_quarterly",
                name = "专业版季付",
                description = "灵活选择",
                originalPrice = 89.0,
                discountedPrice = 69.0,
                durationDays = 90,
                features = listOf(
                    PlanFeature("全球 50+ 节点", true),
                    PlanFeature("不限流量", true),
                    PlanFeature("5 台设备同时在线", true),
                    PlanFeature("专属客服支持", false)
                )
            ),
            Plan(
                id = "pro_monthly",
                name = "专业版月付",
                description = "随时取消",
                originalPrice = 35.0,
                discountedPrice = 29.0,
                durationDays = 30,
                features = listOf(
                    PlanFeature("全球 50+ 节点", true),
                    PlanFeature("不限流量", true),
                    PlanFeature("3 台设备同时在线", true)
                )
            ),
            Plan(
                id = "basic_yearly",
                name = "基础版年付",
                description = "经济实惠",
                originalPrice = 149.0,
                discountedPrice = 99.0,
                durationDays = 365,
                features = listOf(
                    PlanFeature("全球 20+ 节点", true),
                    PlanFeature("每月 100GB 流量", true),
                    PlanFeature("3 台设备同时在线", true)
                )
            ),
            Plan(
                id = "basic_monthly",
                name = "基础版月付",
                description = "入门选择",
                originalPrice = 19.0,
                discountedPrice = 15.0,
                durationDays = 30,
                features = listOf(
                    PlanFeature("全球 20+ 节点", true),
                    PlanFeature("每月 50GB 流量", true),
                    PlanFeature("2 台设备同时在线", true)
                )
            )
        )
    }
}
