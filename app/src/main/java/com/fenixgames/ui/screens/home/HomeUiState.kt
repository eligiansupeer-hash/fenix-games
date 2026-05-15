package com.fenixgames.ui.screens.home

import com.fenixgames.domain.model.ContentRating
import com.fenixgames.domain.model.GameMode
import com.fenixgames.domain.model.PenaltyPolicy
import com.fenixgames.domain.model.Team

data class HomeUiState(
    val isLoading: Boolean = true,
    val cardCount: Int = 0,
    val selectedMode: GameMode = GameMode.NEVER_HAVE_I_EVER,
    val selectedRating: ContentRating = ContentRating.TEEN,
    val selectedPenalty: PenaltyPolicy = PenaltyPolicy.LOSE_POINT,
    val playersText: String = "Sofi, Nico, Valen, Lu",
    val teamsText: String = "Equipo A, Equipo B",
    val competitionEnabled: Boolean = false,
    val teamScores: List<Team> = emptyList(),
    val legalAccepted: Boolean = false,
    val currentCardText: String? = null,
    val roundIndex: Int = 1,
    val actorName: String? = null,
    val error: String? = null
)

