package com.v2ray.ang.composeui.p1.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p1.model.PlansEvent
import com.v2ray.ang.composeui.p1.model.PlansUiState
import kotlinx.coroutines.launch

class PlansViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<PlansUiState>(PlansUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: PlansEvent) {
        when (event) {
            PlansEvent.PrimaryActionClicked -> Unit
            PlansEvent.SecondaryActionClicked -> Unit
            PlansEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedPlansState()?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getPlansState()
        }
    }
}
