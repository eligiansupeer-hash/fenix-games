package com.fenixgames.domain.model

data class Round(
    val index: Int,
    val mode: GameMode,
    val currentCard: Card? = null
)

