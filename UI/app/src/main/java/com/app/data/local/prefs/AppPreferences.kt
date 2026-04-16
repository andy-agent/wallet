package com.app.data.local.prefs

import android.content.Context
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class AppPreferences(@Suppress("UNUSED_PARAMETER") context: Context) {
    private val _loggedIn = MutableStateFlow(false)
    val loggedIn: StateFlow<Boolean> = _loggedIn.asStateFlow()

    private val _forceUpdateRequired = MutableStateFlow(false)
    val forceUpdateRequired: StateFlow<Boolean> = _forceUpdateRequired.asStateFlow()

    private val _versionUpdateAvailable = MutableStateFlow(true)
    val versionUpdateAvailable: StateFlow<Boolean> = _versionUpdateAvailable.asStateFlow()

    fun setLoggedIn(value: Boolean) { _loggedIn.value = value }
    fun setForceUpdateRequired(value: Boolean) { _forceUpdateRequired.value = value }
    fun setVersionUpdateAvailable(value: Boolean) { _versionUpdateAvailable.value = value }
}
