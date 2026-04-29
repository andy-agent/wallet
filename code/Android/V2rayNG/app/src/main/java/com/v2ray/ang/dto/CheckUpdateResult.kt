package com.v2ray.ang.dto

data class CheckUpdateResult(
    val hasUpdate: Boolean,
    val latestVersion: String? = null,
    val latestVersionCode: Long? = null,
    val currentVersion: String? = null,
    val currentVersionCode: Long? = null,
    val releaseNotes: String? = null,
    val downloadUrl: String? = null,
    val error: String? = null,
    val isPreRelease: Boolean = false,
    val forceUpdate: Boolean = false,
)
