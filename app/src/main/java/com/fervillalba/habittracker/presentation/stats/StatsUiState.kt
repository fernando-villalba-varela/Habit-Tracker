package com.fervillalba.habittracker.presentation.stats

import com.fervillalba.habittracker.domain.usecase.HabitStats

data class StatsUiState(
    val stats: List<HabitStats> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)