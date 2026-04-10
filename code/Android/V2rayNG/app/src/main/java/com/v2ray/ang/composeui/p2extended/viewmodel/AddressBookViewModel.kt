package com.v2ray.ang.composeui.p2extended.viewmodel

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel
import com.v2ray.ang.composeui.p2extended.model.AddressBookEvent
import com.v2ray.ang.composeui.p2extended.model.AddressBookUiState
import com.v2ray.ang.composeui.p2extended.model.AddressBookRouteArgs

class AddressBookViewModel(
    private val repository: CryptoVpnRepository,
    private val routeArgs: AddressBookRouteArgs = AddressBookRouteArgs(),
) : BaseFeatureViewModel<AddressBookUiState>(AddressBookUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: AddressBookEvent) {
        when (event) {
            is AddressBookEvent.FieldChanged -> {
                _uiState.value = _uiState.value.copy(
                    fields = _uiState.value.fields.map { field ->
                        if (field.key == event.key) field.copy(value = event.value) else field
                    },
                )
            }

            AddressBookEvent.PrimaryActionClicked -> Unit
            AddressBookEvent.SecondaryActionClicked -> Unit
            AddressBookEvent.Refresh -> refresh()
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getAddressBookState(routeArgs)
        }
    }
}
