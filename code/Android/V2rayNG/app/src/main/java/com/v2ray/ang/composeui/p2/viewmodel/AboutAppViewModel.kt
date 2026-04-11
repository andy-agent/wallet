package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.AboutAppEvent
import com.v2ray.ang.composeui.p2.model.AboutAppUiState
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class AboutAppViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<AboutAppUiState>(initialAboutAppState()) {

    init {
        refresh()
    }

    fun onEvent(event: AboutAppEvent) {
        when (event) {
            is AboutAppEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }
            AboutAppEvent.PrimaryActionClicked -> Unit
            AboutAppEvent.SecondaryActionClicked -> Unit
            AboutAppEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null, emptyMessage = null)
        viewModelScope.launch {
            runCatching { repository.getAboutAppState() }
                .onSuccess { _uiState.value = it.copy(isLoading = false) }
                .onFailure { _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = it.message ?: "加载关于页失败") }
        }
    }
}

private fun initialAboutAppState() = AboutAppUiState(
    badge = "",
    summary = "",
    primaryActionLabel = null,
    secondaryActionLabel = null,
    metrics = emptyList(),
    highlights = emptyList(),
    checklist = emptyList(),
    note = "",
    isLoading = true,
)
