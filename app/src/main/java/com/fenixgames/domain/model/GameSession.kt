package com.fenixgames.domain.model

data class GameSession(
    val id: String,
    val players: List<Player>,
    val mode: GameMode,
    val rating: ContentRating = ContentRating.TEEN,
    val penaltyPolicy: PenaltyPolicy = PenaltyPolicy.LOSE_POINT,
    val legalAccepted: Boolean = false,
    val round: Round = Round(index = 1, mode = mode)
)

