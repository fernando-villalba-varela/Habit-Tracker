package com.fervillalba.habittracker.presentation.create

import com.fervillalba.habittracker.domain.model.HabitFrequency

data class CreateHabitUiState(
    val name: String = "",
    val iconEmoji: String = "✅",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val isLoading: Boolean = false,
    val nameError: String? = null,
    val error: String? = null
)