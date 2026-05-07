package com.fenixgames.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "project_marker")
data class ProjectMarkerEntity(
    @PrimaryKey val id: String,
    val label: String
)

