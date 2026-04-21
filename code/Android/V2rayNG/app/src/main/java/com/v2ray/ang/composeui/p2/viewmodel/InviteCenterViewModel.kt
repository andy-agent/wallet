package com.v2ray.ang.composeui.p2.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.InviteCenterEvent
import com.v2ray.ang.composeui.p2.model.InviteCenterUiState
import kotlinx.coroutines.launch

class InviteCenterViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<InviteCenterUiState>(InviteCenterUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: InviteCenterEvent) {
        when (event) {
            is InviteCenterEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            InviteCenterEvent.PrimaryActionClicked -> Unit
            InviteCenterEvent.SecondaryActionClicked -> Unit
            InviteCenterEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedInviteCenterState()?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getInviteCenterState()
        }
    }
}
