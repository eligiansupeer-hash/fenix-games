package com.fenixgames.domain.model

data class Team(
    val id: String,
    val name: String,
    val playerIds: List<String>,
    val score: Score = Score()
)

