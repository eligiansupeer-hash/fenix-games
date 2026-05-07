package com.fenixgames.ui.screens.diagnostics

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fenixgames.data.diagnostics.DiagnosticRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DiagnosticViewModel(
    private val repository: DiagnosticRepository
) : ViewModel() {
    private val _state = MutableStateFlow(DiagnosticUiState())
    val state: StateFlow<DiagnosticUiState> = _state.asStateFlow()

    init {
        refresh()
    }

    fun refresh() {
        viewModelScope.launch {
            _state.value = _state.value.copy(report = repository.readReport())
        }
    }

    fun export(uri: Uri) {
        viewModelScope.launch {
            runCatching { repository.exportReport(uri) }
                .onSuccess {
                    _state.value = _state.value.copy(message = "Diagnostico exportado")
                    refresh()
                }
                .onFailure { error ->
                    _state.value = _state.value.copy(message = error.message)
                }
        }
    }

    fun triggerTestCrash() {
        error("Fenix diagnostic crash test")
    }
}

