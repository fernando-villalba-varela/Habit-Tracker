package com.fervillalba.habittracker.presentation.home

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.domain.usecase.CompleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.CreateHabitUseCase
import com.fervillalba.habittracker.domain.usecase.DeleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.GetHabitsUseCase
import com.fervillalba.habittracker.domain.usecase.GetTodayLogsUseCase
import com.fervillalba.habittracker.util.Resource
import com.fervillalba.habittracker.util.UiText
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val getHabitsUseCase: GetHabitsUseCase,
    private val completeHabitUseCase: CompleteHabitUseCase,
    private val getTodayLogsUseCase: GetTodayLogsUseCase,
    private val deleteHabitUseCase: DeleteHabitUseCase,
    private val createHabitUseCase: CreateHabitUseCase
) : AndroidViewModel(application) {

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
            when (val result = completeHabitUseCase(habitId)) {
                is Resource.Success -> {
                    _uiState.update {
                        it.copy(completedHabitIds = it.completedHabitIds + habitId)
                    }
                    updateWidget()
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun deleteHabit(habit: Habit) {
        viewModelScope.launch {
            recentlyDeletedHabit = habit
            when (val result = deleteHabitUseCase(habit)) {
                is Resource.Success -> {
                    updateWidget()
                    _uiState.update {
                        it.copy(deletedHabitMessage = UiText.StringResource(R.string.habit_deleted))
                    }
                }
                is Resource.Error -> {
                    _uiState.update { it.copy(error = result.message) }
                }
                is Resource.Loading -> {}
            }
        }
    }

    fun undoDelete() {
        viewModelScope.launch {
            recentlyDeletedHabit?.let { habit ->
                when (val result = createHabitUseCase(habit)) {
                    is Resource.Success -> {
                        recentlyDeletedHabit = null
                        _uiState.update { it.copy(deletedHabitMessage = null) }
                        updateWidget()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(error = result.message) }
                    }
                    is Resource.Loading -> {}
                }
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

    val filteredHabits: StateFlow<List<Habit>> = uiState
        .map { state ->
            if (state.selectedFilter == null) state.habits
            else state.habits.filter { it.frequency == state.selectedFilter }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun onFilterChange(filter: HabitFrequency?) {
        _uiState.update { it.copy(selectedFilter = filter) }
    }

    private fun updateWidget() {
        val context = getApplication<Application>().applicationContext
        com.fervillalba.habittracker.widget.WidgetUpdateWorker.enqueue(context)
    }
}
