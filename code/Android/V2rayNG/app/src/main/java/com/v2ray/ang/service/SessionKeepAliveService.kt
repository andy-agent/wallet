package com.v2ray.ang.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.v2ray.ang.R
import com.v2ray.ang.payment.data.repository.PaymentRepository

class SessionKeepAliveService : Service() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_STOP -> {
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }

            else -> {
                startForeground(NOTIFICATION_ID, buildNotification())
                return START_STICKY
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun buildNotification(): Notification {
        val channelId = ensureChannel()
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
            ?: Intent(this, com.v2ray.ang.ui.compose.LaunchSplashActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this,
            0,
            launchIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
        )
        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_stat_name)
            .setContentTitle("CryptoVPN 已就绪")
            .setContentText("后台保活中，等待连接")
            .setOngoing(true)
            .setOnlyAlertOnce(true)
            .setShowWhen(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setContentIntent(pendingIntent)
            .build()
    }

    private fun ensureChannel(): String {
        val channelId = CHANNEL_ID
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            val channel = NotificationChannel(
                channelId,
                "CryptoVPN Session",
                NotificationManager.IMPORTANCE_LOW,
            ).apply {
                description = "Keeps CryptoVPN ready in background after login."
                setShowBadge(false)
            }
            manager.createNotificationChannel(channel)
        }
        return channelId
    }

    companion object {
        private const val CHANNEL_ID = "cryptovpn_session_keepalive"
        private const val NOTIFICATION_ID = 2002
        private const val ACTION_START = "com.v2ray.ang.action.SESSION_KEEPALIVE_START"
        private const val ACTION_STOP = "com.v2ray.ang.action.SESSION_KEEPALIVE_STOP"

        fun start(context: Context) {
            val appContext = context.applicationContext
            val intent = Intent(appContext, SessionKeepAliveService::class.java).apply {
                action = ACTION_START
            }
            ContextCompat.startForegroundService(appContext, intent)
        }

        fun stop(context: Context) {
            val appContext = context.applicationContext
            val intent = Intent(appContext, SessionKeepAliveService::class.java).apply {
                action = ACTION_STOP
            }
            appContext.startService(intent)
        }

        fun restartIfLoggedIn(context: Context) {
            val repo = PaymentRepository(context.applicationContext)
            val hasSession = repo.isTokenValid() || !repo.getCurrentUserId().isNullOrBlank()
            if (hasSession) {
                start(context)
            }
        }
    }
}
