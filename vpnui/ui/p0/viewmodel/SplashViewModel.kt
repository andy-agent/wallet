package com.cryptovpn.ui.p0.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.cryptovpn.ui.p0.model.SplashEvent
import com.cryptovpn.ui.p0.model.SplashUiState
import com.cryptovpn.ui.p0.repository.P0Repository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SplashViewModel(
    private val repository: P0Repository,
) : ViewModel() {

    private val _uiState = MutableStateFlow(SplashUiState())
    val uiState: StateFlow<SplashUiState> = _uiState.asStateFlow()

    init {
        onEvent(SplashEvent.Refresh)
    }

    fun onEvent(event: SplashEvent) {
        when (event) {
            SplashEvent.Refresh -> {
                viewModelScope.launch {
                    _uiState.value = repository.getSplashState()
                }
            }

            SplashEvent.Continue -> Unit
        }
    }
}
