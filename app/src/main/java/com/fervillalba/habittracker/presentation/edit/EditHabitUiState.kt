package com.fervillalba.habittracker.presentation.edit

import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.util.UiText

data class EditHabitUiState(
    val habitId: Long = 0,
    val name: String = "",
    val iconEmoji: String = Constants.DEFAULT_EMOJI,
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val isLoading: Boolean = false,
    val isSaved: Boolean = false,
    val nameError: UiText? = null,
    val error: UiText? = null,
    val reminderTimes: List<String> = emptyList(),
    val showTimePicker: Boolean = false
)