package com.fenixgames.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.fenixgames.core.di.AppContainer

class HomeViewModelFactory(
    private val container: AppContainer
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            return HomeViewModel(
                sessionManager = container.sessionManager,
                cardRepository = container.cardRepository
            ) as T
        }
        error("Unknown ViewModel class: ${modelClass.name}")
    }
}

