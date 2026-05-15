package com.fenixgames.domain.model

data class Round(
    val index: Int,
    val mode: GameMode,
    val actor: Player? = null,
    val target: Player? = null,
    val targetA: Player? = null,
    val targetB: Player? = null,
    val currentCard: Card? = null,
    val renderedText: String? = null
)

