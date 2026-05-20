package com.fervillalba.habittracker.domain.model

import com.fervillalba.habittracker.Constants

data class Habit(
    val id: Long = 0,
    val name: String,
    val iconEmoji: String = Constants.DEFAULT_EMOJI,
    val colorHex: String = Constants.DEFAULT_COLOR_HEX,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val reminderTimes: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
)

enum class HabitFrequency {
    DAILY,
    WEEKDAYS,
    WEEKENDS
}