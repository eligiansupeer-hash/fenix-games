package com.fenixgames.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fenixgames.data.diagnostics.DiagnosticRepository
import com.fenixgames.data.repository.CardRepository
import com.fenixgames.domain.model.ContentRating
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.model.PenaltyPolicy
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
                _state.value.copy(
                    isLoading = false,
                    cardCount = count,
                    selectedMode = session.mode,
                    selectedRating = session.rating,
                    selectedPenalty = session.penaltyPolicy,
                    competitionEnabled = session.competitionEnabled,
                    teamScores = session.teams,
                    legalAccepted = session.legalAccepted,
                    currentCardText = session.round.renderedText,
                    roundIndex = session.round.index,
                    actorName = session.round.actor?.name
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
                    _state.value = _state.value.copy(
                        isLoading = false,
                        error = error.message ?: "Error cargando contenido offline"
                    )
                }
        }
    }

    fun updatePlayers(value: String) {
        _state.value = _state.value.copy(playersText = value)
    }

    fun selectMode(mode: GameMode) {
        _state.value = _state.value.copy(
            selectedMode = mode,
            competitionEnabled = _state.value.competitionEnabled && mode.supportsCompetition,
            legalAccepted = false
        )
    }

    fun selectRating(rating: ContentRating) {
        _state.value = _state.value.copy(selectedRating = rating, legalAccepted = false)
    }

    fun selectPenalty(policy: PenaltyPolicy) {
        _state.value = _state.value.copy(selectedPenalty = policy, legalAccepted = false)
    }

    fun updateTeams(value: String) {
        _state.value = _state.value.copy(teamsText = value, legalAccepted = false)
    }

    fun setCompetitionEnabled(enabled: Boolean) {
        _state.value = _state.value.copy(
            competitionEnabled = enabled && _state.value.selectedMode.supportsCompetition,
            legalAccepted = false
        )
    }

    fun startAcceptedSession() {
        val current = _state.value
        viewModelScope.launch {
            runCatching {
                cardRepository.ensureBundledContentLoaded()
                sessionManager.startSession(
                    playerNames = current.playersText.split(","),
                    mode = current.selectedMode,
                    rating = current.selectedRating,
                    penaltyPolicy = current.selectedPenalty,
                    competitionEnabled = current.competitionEnabled,
                    teamNames = current.teamsText.split(",")
                )
            }
                .onSuccess {
                    diagnosticRepository.logInfo(
                        "HomeViewModel",
                        "Session accepted: ${current.selectedMode.name}/${current.selectedRating.name}"
                    )
                }
                .onFailure { error ->
                    diagnosticRepository.logError("HomeViewModel", "Session start failed", error)
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

    fun scoreActor(delta: Int) {
        sessionManager.scoreActor(delta)
    }
}
