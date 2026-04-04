package com.v2ray.ang.composeui.pages.splash

import com.v2ray.ang.dto.CheckUpdateResult
import com.v2ray.ang.handler.UpdateCheckerManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class ComposeUpdateDecision(
    val hasForceUpdate: Boolean,
    val hasOptionalUpdate: Boolean,
    val latestVersion: String? = null,
    val downloadUrl: String? = null,
)

class ComposeUpdateBridge(
    private val forceUpdateFromIntent: Boolean = false,
) {
    suspend fun check(): ComposeUpdateDecision = withContext(Dispatchers.IO) {
        if (forceUpdateFromIntent) {
            return@withContext ComposeUpdateDecision(
                hasForceUpdate = true,
                hasOptionalUpdate = false,
            )
        }
        val result: CheckUpdateResult = try {
            UpdateCheckerManager.checkForUpdate(includePreRelease = false)
        } catch (_: Exception) {
            CheckUpdateResult(hasUpdate = false)
        }
        if (result.hasUpdate) {
            ComposeUpdateDecision(
                hasForceUpdate = false,
                hasOptionalUpdate = true,
                latestVersion = result.latestVersion,
                downloadUrl = result.downloadUrl,
            )
        } else {
            ComposeUpdateDecision(
                hasForceUpdate = false,
                hasOptionalUpdate = false,
            )
        }
    }
}
