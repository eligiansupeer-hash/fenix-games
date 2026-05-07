package com.fenixgames.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fenixgames.data.repository.CardRepository
import com.fenixgames.domain.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val cardRepository: CardRepository
) : ViewModel() {
    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                cardRepository.observeCardCount(),
                sessionManager.state
            ) { count, session ->
                HomeUiState(
                    isLoading = false,
                    cardCount = count,
                    currentCardText = session.round.currentCard?.text,
                    roundIndex = session.round.index
                )
            }.collect { uiState ->
                _state.value = uiState
            }
        }

        viewModelScope.launch {
            runCatching { sessionManager.prepare() }
                .onFailure { error ->
                    _state.value = HomeUiState(
                        isLoading = false,
                        error = error.message ?: "Error cargando contenido offline"
                    )
                }
        }
    }

    fun nextCard() {
        viewModelScope.launch {
            sessionManager.nextCard()
        }
    }
}

