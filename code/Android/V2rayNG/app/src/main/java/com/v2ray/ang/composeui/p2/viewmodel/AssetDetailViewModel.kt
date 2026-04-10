package com.v2ray.ang.composeui.p2.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2.model.AssetDetailEvent
import com.v2ray.ang.composeui.p2.model.AssetDetailUiState
import com.v2ray.ang.composeui.p2.model.AssetDetailRouteArgs

class AssetDetailViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: AssetDetailRouteArgs = AssetDetailRouteArgs(),
) : BaseFeatureViewModel<AssetDetailUiState>(AssetDetailUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: AssetDetailEvent) {
        when (event) {
            is AssetDetailEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            AssetDetailEvent.PrimaryActionClicked -> Unit
            AssetDetailEvent.SecondaryActionClicked -> Unit
            AssetDetailEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getAssetDetailState(routeArgs)
        }
    }
}
