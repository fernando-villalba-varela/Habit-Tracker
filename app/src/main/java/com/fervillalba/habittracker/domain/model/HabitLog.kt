package com.fervillalba.habittracker.domain.model

data class HabitLog(
    val id: Long = 0,
    val habitId: Long,
    val completedAt: Long = System.currentTimeMillis(),
    val note: String? = null
)