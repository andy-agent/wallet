package com.app.data.repository

import com.app.data.model.LegalDocument
import com.app.data.model.UserProfile
import kotlinx.coroutines.flow.StateFlow

interface SettingsRepository {
    val profile: StateFlow<UserProfile>
    suspend fun getProfile(): UserProfile
    suspend fun getLegalDocuments(): List<LegalDocument>
    suspend fun getLegalDocument(docId: String): LegalDocument?
    suspend fun updateSecurity(key: String, enabled: Boolean)
}
