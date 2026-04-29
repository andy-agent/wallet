package com.v2ray.ang.dto

import com.google.gson.annotations.SerializedName

data class AppVersionLatestResponse(
    @SerializedName("code")
    val code: String? = null,
    @SerializedName("message")
    val message: String? = null,
    @SerializedName("data")
    val data: AppVersionLatestData? = null,
)

data class AppVersionLatestData(
    @SerializedName("versionName")
    val versionName: String? = null,
    @SerializedName("versionCode")
    val versionCode: Long? = null,
    @SerializedName("minAndroidVersionCode")
    val minAndroidVersionCode: Long? = null,
    @SerializedName("downloadUrl")
    val downloadUrl: String? = null,
    @SerializedName("forceUpdate")
    val forceUpdate: Boolean = false,
    @SerializedName("releaseNotes")
    val releaseNotes: String? = null,
    @SerializedName("status")
    val status: String? = null,
)
