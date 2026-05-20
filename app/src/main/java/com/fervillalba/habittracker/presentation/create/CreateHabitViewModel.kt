package com.fervillalba.habittracker.presentation.create

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.domain.usecase.CreateHabitUseCase
import com.fervillalba.habittracker.domain.manager.ReminderManager
import com.fervillalba.habittracker.domain.manager.WidgetManager
import com.fervillalba.habittracker.util.DispatchersProvider
import com.fervillalba.habittracker.util.Resource
import com.fervillalba.habittracker.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    private val createHabitUseCase: CreateHabitUseCase,
    private val reminderManager: ReminderManager,
    private val widgetManager: WidgetManager,
    private val dispatchers: DispatchersProvider
) : ViewModel() {

    private val _uiState = MutableStateFlow(CreateHabitUiState())
    val uiState: StateFlow<CreateHabitUiState> = _uiState.asStateFlow()

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onEmojiChange(emoji: String) {
        _uiState.update { it.copy(iconEmoji = emoji) }
    }

    fun onFrequencyChange(frequency: HabitFrequency) {
        _uiState.update { it.copy(frequency = frequency) }
    }

    fun addReminderTime(time: String) {
        _uiState.update { state ->
            if (time !in state.reminderTimes) {
                state.copy(reminderTimes = (state.reminderTimes + time).sorted())
            } else state
        }
    }

    fun removeReminderTime(time: String) {
        _uiState.update { state ->
            state.copy(reminderTimes = state.reminderTimes - time)
        }
    }

    fun onShowTimePicker(show: Boolean) {
        _uiState.update { it.copy(showTimePicker = show) }
    }

    fun createHabit(onSuccess: () -> Unit) {
        val state = _uiState.value

        viewModelScope.launch(dispatchers.main) {
            _uiState.update { it.copy(isLoading = true) }
            val habit = Habit(
                name = state.name.trim(),
                iconEmoji = state.iconEmoji,
                frequency = state.frequency,
                reminderTimes = state.reminderTimes
            )

            when (val result = createHabitUseCase(habit)) {
                is Resource.Success -> {
                    _uiState.update { it.copy(isLoading = false) }
                    val habitId = result.data ?: System.currentTimeMillis()
                    
                    if (state.reminderTimes.isNotEmpty()) {
                        reminderManager.scheduleHabitReminders(
                            habitId = habitId,
                            habitName = state.name,
                            reminderTimes = state.reminderTimes
                        )
                    }
                    widgetManager.updateWidget()
                    onSuccess()
                }
                is Resource.Error -> {
                    val isNameError = result.message is UiText.StringResource && 
                                     result.message.resId == R.string.error_empty_name
                    
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            nameError = if (isNameError) result.message else null,
                            error = if (!isNameError) result.message else null
                        )
                    }
                }
                is Resource.Loading -> { /* Handled above */ }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}
