package com.v2ray.ang.composeui.p2.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.InviteShareEvent
import com.v2ray.ang.composeui.p2.model.InviteShareUiState
import kotlinx.coroutines.launch

class InviteShareViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<InviteShareUiState>(InviteShareUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: InviteShareEvent) {
        when (event) {
            is InviteShareEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }
            InviteShareEvent.PrimaryActionClicked -> Unit
            InviteShareEvent.SecondaryActionClicked -> Unit
            InviteShareEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedInviteShareState()?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getInviteShareState()
        }
    }
}
