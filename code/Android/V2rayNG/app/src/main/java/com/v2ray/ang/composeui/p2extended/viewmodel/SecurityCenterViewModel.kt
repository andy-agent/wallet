package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterEvent
import com.v2ray.ang.composeui.p2extended.model.SecurityCenterUiState

class SecurityCenterViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<SecurityCenterUiState>(SecurityCenterUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SecurityCenterEvent) {
        when (event) {
            is SecurityCenterEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SecurityCenterEvent.PrimaryActionClicked -> Unit
            SecurityCenterEvent.SecondaryActionClicked -> Unit
            SecurityCenterEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSecurityCenterState()
        }
    }
}
