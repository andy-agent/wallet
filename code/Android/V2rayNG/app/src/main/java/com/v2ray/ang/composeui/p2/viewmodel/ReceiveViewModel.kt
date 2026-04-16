package com.v2ray.ang.composeui.p2.viewmodel

import androidx.lifecycle.viewModelScope
import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.repository.previewVariantSelection
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.ReceiveEvent
import com.v2ray.ang.composeui.p2.model.ReceiveRouteArgs
import com.v2ray.ang.composeui.p2.model.ReceiveUiState
import kotlinx.coroutines.launch

class ReceiveViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: ReceiveRouteArgs = ReceiveRouteArgs(),
) : BaseFeatureViewModel<ReceiveUiState>(ReceiveUiState()) {
    private var currentRouteArgs: ReceiveRouteArgs = routeArgs

    init {
        refresh()
    }

    fun onEvent(event: ReceiveEvent) {
        when (event) {
            is ReceiveEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            is ReceiveEvent.VariantSelected -> switchVariant(event.assetId, event.chainId)
            ReceiveEvent.PrimaryActionClicked -> Unit
            ReceiveEvent.SecondaryActionClicked -> Unit
            ReceiveEvent.Refresh -> refresh()
        }
    }

    private fun switchVariant(assetId: String, chainId: String) {
        val nextArgs = ReceiveRouteArgs(assetId = assetId, chainId = chainId)
        if (nextArgs == currentRouteArgs) {
            return
        }
        currentRouteArgs = nextArgs
        viewModelScope.launch {
            _uiState.value = _uiState.value.previewVariantSelection(assetId, chainId)
            repository.getCachedReceiveState(currentRouteArgs)?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getReceiveState(currentRouteArgs)
        }
    }

    private fun refresh() {
        viewModelScope.launch {
            repository.getCachedReceiveState(currentRouteArgs)?.let { cached ->
                _uiState.value = cached
            }
            _uiState.value = repository.getReceiveState(currentRouteArgs)
        }
    }
}
