package com.app.data.repository

import com.app.data.local.prefs.AppPreferences
import com.app.data.model.LegalDocument
import com.app.data.model.UserProfile
import com.app.data.remote.mock.MockWalletDataSource
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SettingsRepositoryImpl(
    walletRemote: MockWalletDataSource,
    private val appPreferences: AppPreferences,
) : SettingsRepository {
    private val docs = walletRemote.legalDocs()
    private val profileState = MutableStateFlow(walletRemote.profile())
    override val profile: StateFlow<UserProfile> = profileState.asStateFlow()

    override suspend fun getProfile(): UserProfile = profile.value
    override suspend fun getLegalDocuments(): List<LegalDocument> = docs
    override suspend fun getLegalDocument(docId: String): LegalDocument? = docs.firstOrNull { it.id == docId }
    override suspend fun updateSecurity(key: String, enabled: Boolean) {
        profileState.update { current ->
            current.copy(levelLabel = if (enabled) current.levelLabel else current.levelLabel)
        }
        if (key == "logout" && enabled) {
            appPreferences.setLoggedIn(false)
        }
    }
}
