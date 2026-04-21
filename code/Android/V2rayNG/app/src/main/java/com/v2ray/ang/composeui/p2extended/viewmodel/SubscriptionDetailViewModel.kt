package com.v2ray.ang.composeui.p2extended.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailEvent
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailUiState
import com.v2ray.ang.composeui.p2extended.model.SubscriptionDetailRouteArgs
import kotlinx.coroutines.launch

class SubscriptionDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: SubscriptionDetailRouteArgs = SubscriptionDetailRouteArgs(),
) : BaseFeatureViewModel<SubscriptionDetailUiState>(SubscriptionDetailUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SubscriptionDetailEvent) {
        when (event) {
            is SubscriptionDetailEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            SubscriptionDetailEvent.PrimaryActionClicked -> Unit
            SubscriptionDetailEvent.SecondaryActionClicked -> Unit
            SubscriptionDetailEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedSubscriptionDetailState(routeArgs)?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getSubscriptionDetailState(routeArgs)
        }
    }
}
