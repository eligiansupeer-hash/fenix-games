package com.fenixgames.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fenixgames.data.local.dao.CardDao
import com.fenixgames.data.local.entity.CardEntity
import com.fenixgames.data.local.entity.PackEntity
import com.fenixgames.data.local.entity.ProjectMarkerEntity
import com.fenixgames.data.local.entity.UsedCardEntity

@Database(
    entities = [
        ProjectMarkerEntity::class,
        PackEntity::class,
        CardEntity::class,
        UsedCardEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDao(): CardDao
}

