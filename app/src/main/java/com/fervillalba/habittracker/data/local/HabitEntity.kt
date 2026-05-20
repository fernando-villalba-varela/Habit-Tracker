package com.fervillalba.habittracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

import com.fervillalba.habittracker.Constants

@Entity(tableName = Constants.TABLE_HABITS)
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconEmoji: String,
    val colorHex: String,
    val frequency: String,
    val reminderTimes: List<String>,
    val createdAt: Long,
    val isActive: Boolean
)