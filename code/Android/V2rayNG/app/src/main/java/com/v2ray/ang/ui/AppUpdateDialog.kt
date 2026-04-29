package com.v2ray.ang.ui

import android.app.Activity
import android.app.AlertDialog
import com.v2ray.ang.R
import com.v2ray.ang.dto.CheckUpdateResult
import com.v2ray.ang.extension.toastError
import com.v2ray.ang.util.Utils

object AppUpdateDialog {
    fun show(
        activity: Activity,
        result: CheckUpdateResult,
        onDismiss: (() -> Unit)? = null,
    ) {
        if (activity.isFinishing || activity.isDestroyed) {
            return
        }

        val dialog = AlertDialog.Builder(activity)
            .setTitle(activity.getString(R.string.update_new_version_found, result.latestVersion.orEmpty()))
            .setMessage(buildMessage(activity, result))
            .setPositiveButton(R.string.update_now) { _, _ ->
                val downloadUrl = result.downloadUrl
                if (downloadUrl.isNullOrBlank()) {
                    activity.toastError(R.string.update_download_url_missing)
                } else {
                    Utils.openUri(activity, downloadUrl)
                }
            }
            .apply {
                if (result.forceUpdate) {
                    setNegativeButton(R.string.update_exit_app) { _, _ ->
                        activity.finishAffinity()
                    }
                } else {
                    setNegativeButton(R.string.update_later, null)
                }
            }
            .create()

        dialog.setCancelable(!result.forceUpdate)
        dialog.setOnDismissListener { onDismiss?.invoke() }
        dialog.show()
    }

    private fun buildMessage(activity: Activity, result: CheckUpdateResult): String {
        val currentVersion = result.currentVersion.orEmpty().ifBlank { "-" }
        val currentVersionCode = result.currentVersionCode?.toString() ?: "-"
        val latestVersion = result.latestVersion.orEmpty().ifBlank { "-" }
        val latestVersionCode = result.latestVersionCode?.toString() ?: "-"
        val releaseNotes = result.releaseNotes.orEmpty().trim()

        return buildString {
            append(
                activity.getString(
                    if (result.forceUpdate) {
                        R.string.update_force_required
                    } else {
                        R.string.update_optional_available
                    },
                ),
            )
            append("\n\n")
            append(
                activity.getString(
                    R.string.update_version_summary,
                    currentVersion,
                    currentVersionCode,
                    latestVersion,
                    latestVersionCode,
                ),
            )
            if (releaseNotes.isNotBlank()) {
                append("\n\n")
                append(activity.getString(R.string.update_release_notes))
                append("\n")
                append(releaseNotes)
            }
        }
    }
}
