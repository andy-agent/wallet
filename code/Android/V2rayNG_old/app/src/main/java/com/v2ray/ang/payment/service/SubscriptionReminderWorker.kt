package com.v2ray.ang.payment.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.v2ray.ang.R
import com.v2ray.ang.payment.data.repository.LocalPaymentRepository
import com.v2ray.ang.payment.ui.activity.UserProfileActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.concurrent.TimeUnit

/**
 * 订阅到期提醒Worker
 * 定期检查即将到期的订阅并发送通知
 */
class SubscriptionReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "subscription_reminder_work"
        const val CHANNEL_ID = "subscription_reminder_channel"
        const val CHANNEL_NAME = "订阅到期提醒"
        const val NOTIFICATION_ID_3_DAYS = 1001
        const val NOTIFICATION_ID_1_DAY = 1002
        const val NOTIFICATION_ID_EXPIRED = 1003

        // 提前提醒时间（毫秒）
        const val REMIND_3_DAYS = 3 * 24 * 60 * 60 * 1000L  // 3天
        const val REMIND_1_DAY = 1 * 24 * 60 * 60 * 1000L  // 1天

        /**
         * 启动定期提醒任务
         */
        fun startReminderWork(context: Context) {
            val workRequest = PeriodicWorkRequestBuilder<SubscriptionReminderWorker>(
                12, TimeUnit.HOURS  // 每12小时检查一次
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

        /**
         * 停止提醒任务
         */
        fun stopReminderWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }

    private val localRepository = LocalPaymentRepository(applicationContext)

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        try {
            createNotificationChannel()
            checkAndNotifyExpiringSubscriptions()
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

    /**
     * 检查即将到期的订阅并发送通知
     */
    private suspend fun checkAndNotifyExpiringSubscriptions() {
        val currentUser = localRepository.getCurrentUser() ?: return
        val currentTime = System.currentTimeMillis()

        // 获取即将到期的订单（3天内）
        val expiringOrders = localRepository.getExpiringOrders(
            currentUser.userId,
            threshold = currentTime + REMIND_3_DAYS
        )

        expiringOrders.forEach { order ->
            val expiredAt = order.expiredAt ?: return@forEach
            val timeUntilExpiry = expiredAt - currentTime

            when {
                // 已过期
                timeUntilExpiry <= 0 -> {
                    sendNotification(
                        NOTIFICATION_ID_EXPIRED + order.orderNo.hashCode(),
                        "订阅已过期",
                        "您的${order.planName}套餐已过期，请及时续费以保持服务。",
                        NotificationCompat.PRIORITY_HIGH
                    )
                }
                // 1天内到期
                timeUntilExpiry <= REMIND_1_DAY -> {
                    sendNotification(
                        NOTIFICATION_ID_1_DAY + order.orderNo.hashCode(),
                        "订阅即将到期",
                        "您的${order.planName}套餐将在24小时内到期，请及时续费。",
                        NotificationCompat.PRIORITY_HIGH
                    )
                }
                // 3天内到期
                timeUntilExpiry <= REMIND_3_DAYS -> {
                    sendNotification(
                        NOTIFICATION_ID_3_DAYS + order.orderNo.hashCode(),
                        "订阅即将到期",
                        "您的${order.planName}套餐将在3天内到期，建议提前续费。",
                        NotificationCompat.PRIORITY_DEFAULT
                    )
                }
            }
        }
    }

    /**
     * 发送通知
     */
    private fun sendNotification(
        notificationId: Int,
        title: String,
        content: String,
        priority: Int
    ) {
        val intent = Intent(applicationContext, UserProfileActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            applicationContext,
            0,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle(title)
            .setContentText(content)
            .setPriority(priority)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        val notificationManager = applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(notificationId, notification)
    }

    /**
     * 创建通知渠道（Android 8.0+）
     */
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "提醒用户订阅即将到期"
            }

            val notificationManager = applicationContext.getSystemService(NotificationManager::class.java)
            notificationManager.createNotificationChannel(channel)
        }
    }
}
