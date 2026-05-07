package com.fenixgames.ui.screens.diagnostics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fenixgames.core.di.AppContainer

class DiagnosticViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DiagnosticViewModel::class.java)) {
            return DiagnosticViewModel(container.diagnosticRepository) as T
        }
        error("Unknown ViewModel class: ${modelClass.name}")
    }
}

