package com.fervillalba.habittracker.presentation.create

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.di.WorkManagerHelper
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.domain.usecase.CreateHabitUseCase
import com.fervillalba.habittracker.util.UiText
import com.fervillalba.habittracker.widget.HabitWidget
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CreateHabitViewModel @Inject constructor(
    application: Application,
    private val createHabitUseCase: CreateHabitUseCase
) : AndroidViewModel(application) {

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

    fun onReminderTimeChange(time: String?) {
        _uiState.update { it.copy(reminderTime = time) }
    }

    fun onShowTimePicker(show: Boolean) {
        _uiState.update { it.copy(showTimePicker = show) }
    }

    fun createHabit(onSuccess: () -> Unit) {
        val state = _uiState.value

        if (state.name.isBlank()) {
            _uiState.update {
                it.copy(nameError = UiText.StringResource(R.string.error_empty_name))
            }
            return
        }

        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val habit = Habit(
                    name = state.name.trim(),
                    iconEmoji = state.iconEmoji,
                    frequency = state.frequency,
                    reminderTime = state.reminderTime
                )
                createHabitUseCase(habit)

                state.reminderTime?.let { time ->
                    WorkManagerHelper.scheduleHabitReminderAtTime(
                        context = getApplication(),
                        habitId = System.currentTimeMillis(),
                        habitName = state.name,
                        reminderTime = time
                    )
                }

                updateWidget()
                onSuccess()
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        error = e.message?.let { msg -> UiText.DynamicString(msg) },
                        isLoading = false
                    )
                }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }

    private fun updateWidget() {
        val context = getApplication<Application>().applicationContext
        com.fervillalba.habittracker.widget.WidgetUpdateWorker.enqueue(context)
    }
}