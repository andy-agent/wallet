package com.v2ray.ang.handler

import android.content.Context
import android.os.Build
import android.util.Log
import com.v2ray.ang.AppConfig
import com.v2ray.ang.BuildConfig
import com.v2ray.ang.dto.AppVersionLatestResponse
import com.v2ray.ang.dto.CheckUpdateResult
import com.v2ray.ang.payment.PaymentConfig
import com.v2ray.ang.util.HttpUtil
import com.v2ray.ang.util.JsonUtil
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream

object UpdateCheckerManager {
    private const val APP_VERSION_QUERY = "platform=android&channel=official"
    private const val UPDATE_CHECK_TIMEOUT_MS = 8_000

    suspend fun checkForUpdate(
        context: Context,
        includePreRelease: Boolean = false,
    ): CheckUpdateResult = withContext(Dispatchers.IO) {
        if (includePreRelease) {
            Log.i(AppConfig.TAG, "Pre-release update checks are ignored for backend app_versions")
        }

        val currentVersionCode = getInstalledVersionCode(context)
        val currentVersionName = getInstalledVersionName(context)
        val url = buildAppVersionUrl()

        var response = fetchAppVersionPayload(
            url = url,
            httpPort = 0,
            currentVersionCode = currentVersionCode,
        )
        if (response.isNullOrEmpty()) {
            val httpPort = SettingsManager.getHttpPort()
            response = fetchAppVersionPayload(
                url = url,
                httpPort = httpPort,
                currentVersionCode = currentVersionCode,
            )
                ?: throw IllegalStateException("Failed to get response")
        }

        return@withContext parseAppVersionResponse(
            response = response,
            currentVersionName = currentVersionName,
            currentVersionCode = currentVersionCode,
        )
    }

    fun parseAppVersionResponse(
        response: String,
        currentVersionName: String,
        currentVersionCode: Long,
    ): CheckUpdateResult {
        val envelope = JsonUtil.fromJson(response, AppVersionLatestResponse::class.java)
            ?: throw IllegalStateException("Invalid app version response")
        if (!envelope.code.equals("OK", ignoreCase = true)) {
            throw IllegalStateException(envelope.message ?: "Failed to check app version")
        }
        val latest = envelope.data ?: throw IllegalStateException("App version data is empty")
        val latestVersionName = latest.versionName?.takeIf { it.isNotBlank() }
            ?: throw IllegalStateException("Latest version name is empty")
        val latestVersionCode = latest.versionCode ?: 0L
        val minSupportedCode = latest.minAndroidVersionCode ?: 0L
        val hasUpdate = if (latestVersionCode > 0L) {
            latestVersionCode > currentVersionCode
        } else {
            compareVersions(latestVersionName, currentVersionName) > 0
        }
        val forceUpdate = latest.forceUpdate || (minSupportedCode > 0L && currentVersionCode < minSupportedCode)

        logInfo(
            "Backend app version: $latestVersionName/$latestVersionCode " +
                "(current: $currentVersionName/$currentVersionCode, force=$forceUpdate)",
        )

        return if (hasUpdate || forceUpdate) {
            CheckUpdateResult(
                hasUpdate = true,
                latestVersion = latestVersionName,
                latestVersionCode = latestVersionCode.takeIf { it > 0L },
                currentVersion = currentVersionName,
                currentVersionCode = currentVersionCode,
                releaseNotes = latest.releaseNotes,
                downloadUrl = latest.downloadUrl,
                forceUpdate = forceUpdate,
            )
        } else {
            CheckUpdateResult(
                hasUpdate = false,
                latestVersion = latestVersionName,
                latestVersionCode = latestVersionCode.takeIf { it > 0L },
                currentVersion = currentVersionName,
                currentVersionCode = currentVersionCode,
            )
        }
    }

    suspend fun downloadApk(context: Context, downloadUrl: String): File? = withContext(Dispatchers.IO) {
        try {
            val httpPort = SettingsManager.getHttpPort()
            val connection = HttpUtil.createProxyConnection(downloadUrl, httpPort, 10000, 10000, true)
                ?: throw IllegalStateException("Failed to create connection")

            try {
                val apkFile = File(context.cacheDir, "update.apk")
                Log.i(AppConfig.TAG, "Downloading APK to: ${apkFile.absolutePath}")

                FileOutputStream(apkFile).use { outputStream ->
                    connection.inputStream.use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
                Log.i(AppConfig.TAG, "APK download completed")
                return@withContext apkFile
            } catch (e: Exception) {
                Log.e(AppConfig.TAG, "Failed to download APK: ${e.message}")
                return@withContext null
            } finally {
                try {
                    connection.disconnect()
                } catch (e: Exception) {
                    Log.e(AppConfig.TAG, "Error closing connection: ${e.message}")
                }
            }
        } catch (e: Exception) {
            Log.e(AppConfig.TAG, "Failed to initiate download: ${e.message}")
            return@withContext null
        }
    }

    private fun buildAppVersionUrl(): String {
        val minuteBucket = System.currentTimeMillis() / 60_000L
        return "${PaymentConfig.FULL_API_URL}/app-versions/latest?$APP_VERSION_QUERY&_t=$minuteBucket"
    }

    private fun fetchAppVersionPayload(
        url: String,
        httpPort: Int,
        currentVersionCode: Long,
    ): String? {
        val conn = HttpUtil.createProxyConnection(
            urlStr = url,
            port = httpPort,
            connectTimeout = UPDATE_CHECK_TIMEOUT_MS,
            readTimeout = UPDATE_CHECK_TIMEOUT_MS,
        ) ?: return null

        return try {
            conn.requestMethod = "GET"
            conn.setRequestProperty("Accept", "application/json")
            conn.setRequestProperty("Cache-Control", "no-cache")
            conn.setRequestProperty("Pragma", "no-cache")
            conn.setRequestProperty("User-Agent", "V2rayNG/${BuildConfig.VERSION_NAME} (Android)")
            conn.setRequestProperty("X-Client-Version", BuildConfig.VERSION_NAME)
            conn.setRequestProperty("X-Client-Version-Code", currentVersionCode.toString())
            conn.setRequestProperty("X-Client-Distribution", BuildConfig.DISTRIBUTION)

            if (conn.responseCode in 200..299) {
                conn.inputStream.bufferedReader().readText()
            } else {
                val errorText = conn.errorStream?.bufferedReader()?.readText().orEmpty()
                Log.e(AppConfig.TAG, "App version check failed: HTTP ${conn.responseCode} $errorText")
                null
            }
        } catch (e: Exception) {
            Log.e(AppConfig.TAG, "Failed to fetch app version", e)
            null
        } finally {
            conn.disconnect()
        }
    }

    private fun getInstalledVersionName(context: Context): String {
        return runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull()?.takeIf { it.isNotBlank() } ?: BuildConfig.VERSION_NAME
    }

    private fun getInstalledVersionCode(context: Context): Long {
        return runCatching {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                packageInfo.longVersionCode
            } else {
                @Suppress("DEPRECATION")
                packageInfo.versionCode.toLong()
            }
        }.getOrDefault(BuildConfig.VERSION_CODE.toLong())
    }

    private fun compareVersions(version1: String, version2: String): Int {
        val v1 = version1.split(".")
        val v2 = version2.split(".")

        for (i in 0 until maxOf(v1.size, v2.size)) {
            val num1 = if (i < v1.size) v1[i].toIntOrNull() ?: 0 else 0
            val num2 = if (i < v2.size) v2[i].toIntOrNull() ?: 0 else 0
            if (num1 != num2) return num1 - num2
        }
        return 0
    }

    private fun logInfo(message: String) {
        runCatching {
            Log.i(AppConfig.TAG, message)
        }
    }
}
