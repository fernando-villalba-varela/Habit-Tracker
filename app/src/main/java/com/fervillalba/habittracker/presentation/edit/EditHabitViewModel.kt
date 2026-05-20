package com.fervillalba.habittracker.presentation.edit

import android.app.Application
import androidx.glance.appwidget.updateAll
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.domain.usecase.EditHabitUseCase
import com.fervillalba.habittracker.domain.usecase.GetHabitsUseCase
import com.fervillalba.habittracker.util.UiText
import com.fervillalba.habittracker.widget.HabitWidget
import dagger.hilt.android.internal.Contexts.getApplication
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class EditHabitViewModel @Inject constructor(
    application: Application,
    savedStateHandle: SavedStateHandle,
    private val editHabitUseCase: EditHabitUseCase,
    private val getHabitsUseCase: GetHabitsUseCase
) : AndroidViewModel(application) {

    private val habitId: Long = checkNotNull(savedStateHandle.get<String>("habitId")).toLong()

    private val _uiState = MutableStateFlow(EditHabitUiState())
    val uiState: StateFlow<EditHabitUiState> = _uiState.asStateFlow()

    init {
        loadHabit()
    }

    private fun loadHabit() {
        viewModelScope.launch {
            val habits = getHabitsUseCase().first()
            val habit = habits.find { it.id == habitId } ?: return@launch
            _uiState.update {
                it.copy(
                    habitId = habit.id,
                    name = habit.name,
                    iconEmoji = habit.iconEmoji,
                    frequency = habit.frequency,
                    isLoading = false
                )
            }
        }
    }

    fun onNameChange(name: String) {
        _uiState.update { it.copy(name = name, nameError = null) }
    }

    fun onEmojiChange(emoji: String) {
        _uiState.update { it.copy(iconEmoji = emoji) }
    }

    fun onFrequencyChange(frequency: HabitFrequency) {
        _uiState.update { it.copy(frequency = frequency) }
    }

    fun saveHabit(onSuccess: () -> Unit) {
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
                val habits = getHabitsUseCase().first()
                val original = habits.find { it.id == habitId } ?: return@launch
                editHabitUseCase(
                    original.copy(
                        name = state.name.trim(),
                        iconEmoji = state.iconEmoji,
                        frequency = state.frequency
                    )
                )
                viewModelScope.launch {
                    HabitWidget().updateAll(getApplication())
                }
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
}