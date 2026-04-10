package com.cryptovpn.ui.p2.viewmodel

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel
import com.cryptovpn.ui.p2.model.ProfileEvent
import com.cryptovpn.ui.p2.model.ProfileUiState

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
        launchLoad {
            repository.getProfileState()
        }
    }
}
