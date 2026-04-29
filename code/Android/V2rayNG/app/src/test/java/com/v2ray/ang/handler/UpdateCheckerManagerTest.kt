package com.v2ray.ang.handler

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class UpdateCheckerManagerTest {

    @Test
    fun parseAppVersionResponse_detectsNewerServerVersionCode() {
        val result = UpdateCheckerManager.parseAppVersionResponse(
            response = appVersionResponse(
                versionName = "2.0.17.16",
                versionCode = 5_073_300,
                minAndroidVersionCode = 5_073_100,
                forceUpdate = false,
            ),
            currentVersionName = "2.0.17.15",
            currentVersionCode = 5_073_200,
        )

        assertTrue(result.hasUpdate)
        assertFalse(result.forceUpdate)
        assertEquals("2.0.17.16", result.latestVersion)
        assertEquals(5_073_300L, result.latestVersionCode)
        assertEquals("https://api.residential-agent.com/downloads/cryptovpn-android.apk?v=2.0.17.16", result.downloadUrl)
    }

    @Test
    fun parseAppVersionResponse_doesNotPromptWhenInstalledVersionCodeMatches() {
        val result = UpdateCheckerManager.parseAppVersionResponse(
            response = appVersionResponse(
                versionName = "2.0.17.16",
                versionCode = 5_073_300,
                minAndroidVersionCode = 5_073_100,
                forceUpdate = false,
            ),
            currentVersionName = "2.0.17.16",
            currentVersionCode = 5_073_300,
        )

        assertFalse(result.hasUpdate)
    }

    @Test
    fun parseAppVersionResponse_marksForceUpdateWhenCurrentBelowMinimum() {
        val result = UpdateCheckerManager.parseAppVersionResponse(
            response = appVersionResponse(
                versionName = "2.0.17.16",
                versionCode = 5_073_300,
                minAndroidVersionCode = 5_073_300,
                forceUpdate = false,
            ),
            currentVersionName = "2.0.17.14",
            currentVersionCode = 5_073_100,
        )

        assertTrue(result.hasUpdate)
        assertTrue(result.forceUpdate)
    }

    private fun appVersionResponse(
        versionName: String,
        versionCode: Long,
        minAndroidVersionCode: Long,
        forceUpdate: Boolean,
    ): String = """
        {
          "requestId": "test-request",
          "code": "OK",
          "message": "ok",
          "data": {
            "versionId": "test-version-id",
            "platform": "ANDROID",
            "channel": "OFFICIAL",
            "versionName": "$versionName",
            "versionCode": $versionCode,
            "minAndroidVersionCode": $minAndroidVersionCode,
            "downloadUrl": "https://api.residential-agent.com/downloads/cryptovpn-android.apk?v=$versionName",
            "forceUpdate": $forceUpdate,
            "releaseNotes": "Ghost $versionName",
            "status": "PUBLISHED"
          }
        }
    """.trimIndent()
}
