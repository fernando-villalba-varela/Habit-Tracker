package com.fervillalba.habittracker.presentation.detail

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitLog

data class HabitDetailUiState(
    val habit: Habit? = null,
    val logs: List<HabitLog> = emptyList(),
    val currentStreak: Int = 0,
    val bestStreak: Int = 0,
    val totalCompleted: Int = 0,
    val completionRate: Float = 0f,
    val isLoading: Boolean = true,
    val error: String? = null
)