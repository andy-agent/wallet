package com.app.feature.settings.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.app.AppGraph
import com.app.data.model.LegalDocument
import com.app.data.model.UserProfile
import com.app.data.repository.SettingsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class SettingsUiState(
    val profile: UserProfile? = null,
    val legalDocuments: List<LegalDocument> = emptyList(),
)

class SettingsViewModel(
    private val repository: SettingsRepository = AppGraph.settingsRepository,
) : ViewModel() {
    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            _uiState.value = SettingsUiState(repository.getProfile(), repository.getLegalDocuments())
        }
    }

    fun document(docId: String): LegalDocument? = uiState.value.legalDocuments.firstOrNull { it.id == docId }
    fun logout() { viewModelScope.launch { repository.updateSecurity("logout", true) } }
}
