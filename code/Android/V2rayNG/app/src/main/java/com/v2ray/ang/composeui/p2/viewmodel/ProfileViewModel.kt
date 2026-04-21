package com.v2ray.ang.composeui.p2.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.ProfileEvent
import com.v2ray.ang.composeui.p2.model.ProfileUiState
import kotlinx.coroutines.launch

class ProfileViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<ProfileUiState>(ProfileUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: ProfileEvent) {
        when (event) {
            is ProfileEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            ProfileEvent.PrimaryActionClicked -> Unit
            ProfileEvent.SecondaryActionClicked -> Unit
            ProfileEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedProfileState()?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getProfileState()
        }
    }
}
