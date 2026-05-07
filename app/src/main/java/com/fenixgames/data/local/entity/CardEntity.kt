package com.fenixgames.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cards",
    indices = [
        Index("packId"),
        Index("mode"),
        Index("intensity")
    ]
)
data class CardEntity(
    @PrimaryKey val id: String,
    val packId: String,
    val mode: String,
    val text: String,
    val intensity: Int
)

