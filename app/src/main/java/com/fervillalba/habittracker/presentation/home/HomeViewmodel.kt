package com.fervillalba.habittracker.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.usecase.CompleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.CreateHabitUseCase
import com.fervillalba.habittracker.domain.usecase.DeleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.GetHabitsUseCase
import com.fervillalba.habittracker.domain.usecase.GetTodayLogsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getHabitsUseCase: GetHabitsUseCase,
    private val completeHabitUseCase: CompleteHabitUseCase,
    private val getTodayLogsUseCase: GetTodayLogsUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val createHabitUseCase: CreateHabitUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var recentlyDeletedHabit: Habit? = null

    init {
        loadHabits()
        loadTodayLogs()
    }

    private fun loadHabits() {
        viewModelScope.launch {
            getHabitsUseCase().collect { habits ->
                _uiState.update { it.copy(habits = habits, isLoading = false) }
            }
        }
    }

    private fun loadTodayLogs() {
        viewModelScope.launch {
            val logs = getTodayLogsUseCase()
            _uiState.update {
                it.copy(completedHabitIds = logs.map { log -> log.habitId }.toSet())
            }
        }
    }

    fun completeHabit(habitId: Long) {
        viewModelScope.launch {
            try {
                completeHabitUseCase(habitId)
                _uiState.update {
                    it.copy(completedHabitIds = it.completedHabitIds + habitId)
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            try {
                recentlyDeletedHabit = habit
                deleteHabitUseCase(habit)
                _uiState.update { it.copy(deletedHabitMessage = "Hábito eliminado") }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            recentlyDeletedHabit?.let { habit ->
                createHabitUseCase(habit)
                recentlyDeletedHabit = null
                _uiState.update { it.copy(deletedHabitMessage = null) }
            }
        }
    }

    fun clearDeletedMessage() {
        _uiState.update { it.copy(deletedHabitMessage = null) }
        recentlyDeletedHabit = null
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}