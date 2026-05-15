package com.fenixgames.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    indices = [
        Index("packId"),
        Index("mode"),
        Index("rating"),
        Index("ratingRank")
    ]
)
data class CardEntity(
    @PrimaryKey val id: String,
    val packId: String,
    val mode: String,
    val rating: String,
    val ratingRank: Int,
    val cardType: String,
    val category: String,
    val textTemplate: String,
    val targetPolicy: String,
    val penaltyPolicy: String
)

