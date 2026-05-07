package com.fenixgames.ui.screens.home

import com.fenixgames.domain.model.GameMode

data class HomeUiState(
    val isLoading: Boolean = true,
    val cardCount: Int = 0,
    val selectedMode: GameMode = GameMode.NEVER_HAVE_I_EVER,
    val currentCardText: String? = null,
    val roundIndex: Int = 1,
    val error: String? = null
)
