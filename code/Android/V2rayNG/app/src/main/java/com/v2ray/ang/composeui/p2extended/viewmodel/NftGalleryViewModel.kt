package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.NftGalleryEvent
import com.v2ray.ang.composeui.p2extended.model.NftGalleryUiState

class NftGalleryViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<NftGalleryUiState>(NftGalleryUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: NftGalleryEvent) {
        when (event) {
            is NftGalleryEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            NftGalleryEvent.PrimaryActionClicked -> Unit
            NftGalleryEvent.SecondaryActionClicked -> Unit
            NftGalleryEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getNftGalleryState()
        }
    }
}
