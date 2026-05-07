package com.fenixgames.domain.model

data class Card(
    val id: String,
    val mode: GameMode,
    val text: String,
    val intensity: Int
)

