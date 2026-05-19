package com.fervillalba.habittracker.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.fervillalba.habittracker.Constants

@Database(
    entities = [HabitEntity::class, HabitLogEntity::class],
    version = Constants.DATABASE_VERSION,
    exportSchema = false
)
abstract class HabitDatabase : RoomDatabase() {
    abstract fun habitDao(): HabitDao
}