package com.v2ray.ang.payment.ui

import android.os.Handler
import android.os.Looper
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.payment.data.model.Order
import com.v2ray.ang.payment.data.repository.PaymentRepository
import kotlinx.coroutines.*

/**
 * 订单轮询用例
 */
class OrderPollingUseCase(
    private val repository: PaymentRepository,
    private val callback: PollingCallback
) {
    private val coroutineScope = CoroutineScope(Dispatchers.Main + Job())
    private var pollingJob: Job? = null
    private val handler = Handler(Looper.getMainLooper())
    
    private var currentIntervalIndex = 0
    private var startTime = 0L
    private var isPolling = false

    interface PollingCallback {
        fun onStatusUpdate(order: Order)
        fun onPaymentSuccess(order: Order)
        fun onPaymentFailed(error: String)
        fun onExpired()
        fun onError(error: String)
    }

    /**
     * 开始轮询
     */
    fun startPolling(orderId: String) {
        if (isPolling) return
        
        isPolling = true
        startTime = System.currentTimeMillis()
        currentIntervalIndex = 0
        
        scheduleNextPoll(orderId)
    }

    /**
     * 停止轮询
     */
    fun stopPolling() {
        isPolling = false
        pollingJob?.cancel()
        handler.removeCallbacksAndMessages(null)
    }

    /**
     * 是否正在轮询
     */
    fun isActive(): Boolean = isPolling

    private fun scheduleNextPoll(orderId: String) {
        if (!isPolling) return

        // 检查是否超过最大轮询时间
        val elapsed = System.currentTimeMillis() - startTime
        if (elapsed > PaymentConfig.MAX_POLLING_TIME_MS) {
            stopPolling()
            callback.onExpired()
            return
        }

        // 获取当前轮询间隔
        val interval = if (currentIntervalIndex < PaymentConfig.POLLING_INTERVALS.size) {
            PaymentConfig.POLLING_INTERVALS[currentIntervalIndex]
        } else {
            PaymentConfig.POLLING_INTERVALS.last()
        }

        if (currentIntervalIndex < PaymentConfig.POLLING_INTERVALS.size) {
            currentIntervalIndex++
        }

        // 延迟执行轮询
        handler.postDelayed({
            if (isPolling) {
                pollOrder(orderId)
            }
        }, interval)
    }

    private fun pollOrder(orderId: String) {
        pollingJob = coroutineScope.launch {
            try {
                val result = repository.getOrder(orderId)
                
                result.onSuccess { order ->
                    callback.onStatusUpdate(order)
                    
                    when (order.status) {
                        PaymentConfig.OrderStatus.FULFILLED -> {
                            stopPolling()
                            callback.onPaymentSuccess(order)
                        }
                        PaymentConfig.OrderStatus.EXPIRED,
                        PaymentConfig.OrderStatus.FAILED,
                        PaymentConfig.OrderStatus.LATE_PAID -> {
                            stopPolling()
                            callback.onPaymentFailed(order.statusText)
                        }
                        PaymentConfig.OrderStatus.UNDERPAID -> {
                            stopPolling()
                            callback.onPaymentFailed("支付金额不足，请联系客服")
                        }
                        PaymentConfig.OrderStatus.OVERPAID -> {
                            // 多付但仍成功，继续轮询直到 fulfilled
                            scheduleNextPoll(orderId)
                        }
                        else -> {
                            // 继续轮询
                            scheduleNextPoll(orderId)
                        }
                    }
                }.onFailure { error ->
                    callback.onError(error.message ?: "查询失败")
                    // 出错后继续轮询
                    scheduleNextPoll(orderId)
                }
            } catch (e: Exception) {
                callback.onError(e.message ?: "未知错误")
                scheduleNextPoll(orderId)
            }
        }
    }

    /**
     * 立即查询一次（用于手动刷新）
     */
    fun pollImmediately(orderId: String) {
        pollingJob?.cancel()
        pollOrder(orderId)
    }
}
