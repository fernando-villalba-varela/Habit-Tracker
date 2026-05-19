package com.fervillalba.habittracker.presentation.home

import com.fervillalba.habittracker.domain.model.Habit

data class HomeUiState(
    val habits: List<Habit> = emptyList(),
    val completedHabitIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val completedHabitId: Long? = null,
    val deletedHabitMessage: String? = null
)