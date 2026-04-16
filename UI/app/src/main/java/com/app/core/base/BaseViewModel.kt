package com.app.core.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class BaseViewModel<S : UiState, E : UiEvent, F : UiEffect>(
    initialState: S,
) : ViewModel() {
    private val _uiState = MutableStateFlow(initialState)
    val uiState: StateFlow<S> = _uiState.asStateFlow()

    private val _effects = MutableSharedFlow<F>()
    val effects: SharedFlow<F> = _effects.asSharedFlow()

    protected fun setState(reducer: (S) -> S) {
        _uiState.update(reducer)
    }

    protected fun emitEffect(effect: F) {
        viewModelScope.launch {
            _effects.emit(effect)
        }
    }

    protected fun launch(block: suspend () -> Unit) {
        viewModelScope.launch {
            block()
        }
    }

    abstract fun onEvent(event: E)
}
