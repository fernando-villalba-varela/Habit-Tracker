package com.fervillalba.habittracker.presentation.home

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.util.UiText

data class HomeUiState(
    val habits: List<Habit> = emptyList(),
    val completedHabitIds: Set<Long> = emptySet(),
    val isLoading: Boolean = true,
    val error: UiText? = null,
    val completedHabitId: Long? = null,
    val deletedHabitMessage: UiText? = null
)