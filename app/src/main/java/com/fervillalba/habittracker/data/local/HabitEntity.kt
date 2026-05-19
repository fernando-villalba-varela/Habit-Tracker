package com.fervillalba.habittracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habits")
data class HabitEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val iconEmoji: String,
    val colorHex: String,
    val frequency: String,
    val reminderTime: String?,
    val createdAt: Long,
    val isActive: Boolean
)