package com.fenixgames.domain.model

data class GameSession(
    val id: String,
    val players: List<Player>,
    val mode: GameMode,
    val maxIntensity: Int = 0,
    val round: Round = Round(index = 1, mode = mode)
)

