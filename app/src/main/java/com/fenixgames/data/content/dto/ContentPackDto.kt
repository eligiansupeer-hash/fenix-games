package com.fenixgames.data.content.dto

import kotlinx.serialization.Serializable

@Serializable
data class ContentPackDto(
    val id: String,
    val name: String,
    val version: Int,
    val cards: List<CardDto>
)

@Serializable
data class CardDto(
    val id: String,
    val mode: String,
    val text: String,
    val intensity: Int = 0
)

