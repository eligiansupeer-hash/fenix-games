package com.fenixgames.ui.screens.home

data class HomeUiState(
    val isLoading: Boolean = true,
    val cardCount: Int = 0,
    val currentCardText: String? = null,
    val roundIndex: Int = 1,
    val error: String? = null
)

