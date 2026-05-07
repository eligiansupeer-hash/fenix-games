package com.fenixgames.domain.model

data class Player(
    val id: String,
    val name: String,
    val score: Score = Score()
)

