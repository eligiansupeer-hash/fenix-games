package com.fenixgames.data.local.entity

import androidx.room.Entity

@Entity(
    tableName = "used_cards",
    primaryKeys = ["sessionId", "cardId"]
)
data class UsedCardEntity(
    val sessionId: String,
    val cardId: String,
    val usedAtEpochMillis: Long
)

