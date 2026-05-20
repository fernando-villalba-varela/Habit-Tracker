package com.fervillalba.habittracker.presentation.create

import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.util.UiText

data class CreateHabitUiState(
    val name: String = "",
    val iconEmoji: String = Constants.DEFAULT_EMOJI,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val isLoading: Boolean = false,
    val nameError: UiText? = null,
    val error: UiText? = null,
    val reminderTime: String? = null,
    val showTimePicker: Boolean = false
)