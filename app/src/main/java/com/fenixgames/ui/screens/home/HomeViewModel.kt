package com.fenixgames.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fenixgames.data.diagnostics.DiagnosticRepository
import com.fenixgames.data.repository.CardRepository
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.session.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

class HomeViewModel(
    private val sessionManager: SessionManager,
    private val cardRepository: CardRepository,
    private val diagnosticRepository: DiagnosticRepository
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
                    selectedMode = session.mode,
                    currentCardText = session.round.currentCard?.text,
                    roundIndex = session.round.index
                )
            }.collect { uiState ->
                _state.value = uiState
            }
        }

        viewModelScope.launch {
            runCatching { sessionManager.prepare() }
                .onSuccess {
                    diagnosticRepository.logInfo("HomeViewModel", "Offline content prepared")
                }
                .onFailure { error ->
                    diagnosticRepository.logError("HomeViewModel", "Offline content failed", error)
                    _state.value = HomeUiState(
                        isLoading = false,
                        error = error.message ?: "Error cargando contenido offline"
                    )
                }
        }
    }

    fun nextCard() {
        viewModelScope.launch {
            runCatching { sessionManager.nextCard() }
                .onFailure { error ->
                    diagnosticRepository.logError("HomeViewModel", "Next card failed", error)
                }
        }
    }

    fun selectMode(mode: GameMode) {
        viewModelScope.launch {
            runCatching { sessionManager.selectMode(mode) }
                .onSuccess {
                    diagnosticRepository.logInfo("HomeViewModel", "Game mode selected: ${mode.name}")
                }
                .onFailure { error ->
                    diagnosticRepository.logError("HomeViewModel", "Mode change failed", error)
                }
        }
    }
}
