package com.fenixgames.domain.model

data class Card(
    val id: String,
    val mode: GameMode,
    val rating: ContentRating,
    val cardType: CardType,
    val category: String,
    val textTemplate: String,
    val targetPolicy: TargetPolicy,
    val penaltyPolicy: PenaltyPolicy
)

