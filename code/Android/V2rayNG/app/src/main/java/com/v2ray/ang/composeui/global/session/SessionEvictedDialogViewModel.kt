package com.v2ray.ang.composeui.global.session

import com.v2ray.ang.composeui.common.repository.CryptoVpnRepository
import com.v2ray.ang.composeui.common.viewmodel.BaseFeatureViewModel

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
