package com.fervillalba.habittracker.domain.model

data class Habit(
    val id: Long = 0,
    val name: String,
    val iconEmoji: String = "✅",
    val colorHex: String = "#7F77DD",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val reminderTime: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class HabitFrequency {
    DAILY,
    WEEKDAYS,
    WEEKENDS
}