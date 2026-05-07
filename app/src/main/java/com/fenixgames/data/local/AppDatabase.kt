package com.fenixgames.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fenixgames.data.local.entity.ProjectMarkerEntity

@Database(
    entities = [ProjectMarkerEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase()

