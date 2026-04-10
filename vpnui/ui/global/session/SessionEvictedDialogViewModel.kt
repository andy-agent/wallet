package com.cryptovpn.ui.global.session

import com.cryptovpn.ui.common.repository.CryptoVpnRepository
import com.cryptovpn.ui.common.viewmodel.BaseFeatureViewModel

class SessionEvictedDialogViewModel(
    private val repository: CryptoVpnRepository,
) : BaseFeatureViewModel<SessionEvictedDialogUiState>(SessionEvictedDialogUiState()) {

    init {
        refresh()
    }

    fun onEvent(event: SessionEvictedDialogEvent) {
        when (event) {
            SessionEvictedDialogEvent.Refresh -> refresh()
            SessionEvictedDialogEvent.Confirm -> Unit
            SessionEvictedDialogEvent.Dismiss -> Unit
        }
    }

    private fun refresh() {
        launchLoad {
            repository.getSessionEvictedDialogState()
        }
    }
}
