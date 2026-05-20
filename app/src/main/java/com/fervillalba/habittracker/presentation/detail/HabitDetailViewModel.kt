package com.fervillalba.habittracker.presentation.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.repository.HabitRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HabitDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: HabitRepository
) : ViewModel() {

    private val habitId: Long = checkNotNull(savedStateHandle.get<String>("habitId")).toLong()

    private val _uiState = MutableStateFlow(HabitDetailUiState())
    val uiState: StateFlow<HabitDetailUiState> = _uiState.asStateFlow()

    init {
        loadDetail()
    }

    fun deleteHabit() {
        viewModelScope.launch {
            _uiState.value.habit?.let {
                repository.deleteHabit(it)
            }
        }
    }

    private fun loadDetail() {
        viewModelScope.launch {
            try {
                val habit = repository.getHabitById(habitId).first()
                val logs = repository.getLogsForHabit(habitId)
                val last30Days = getLast30DaysLogs(logs)

                _uiState.update {
                    it.copy(
                        habit = habit,
                        logs = logs,
                        currentStreak = calculateCurrentStreak(logs),
                        bestStreak = calculateBestStreak(logs),
                        totalCompleted = logs.size,
                        completionRate = (last30Days.size / 30f).coerceIn(0f, 1f),
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(error = e.message, isLoading = false) }
            }
        }
    }

    private fun getLast30DaysLogs(logs: List<HabitLog>): List<HabitLog> {
        val thirtyDaysAgo = System.currentTimeMillis() - 30L * 24 * 60 * 60 * 1000
        return logs.filter { it.completedAt >= thirtyDaysAgo }
    }

    private fun calculateCurrentStreak(logs: List<HabitLog>): Int {
        if (logs.isEmpty()) return 0
        val completedDays = logs
            .map { normalizeToDay(it.completedAt) }
            .toSortedSet(reverseOrder())
        var streak = 0
        var expectedDay = normalizeToDay(System.currentTimeMillis())
        for (day in completedDays) {
            if (day == expectedDay) {
                streak++
                expectedDay -= 24 * 60 * 60 * 1000
            } else break
        }
        return streak
    }

    private fun calculateBestStreak(logs: List<HabitLog>): Int {
        if (logs.isEmpty()) return 0
        val completedDays = logs
            .map { normalizeToDay(it.completedAt) }
            .toSortedSet()
        var best = 0
        var current = 0
        var previousDay = 0L
        for (day in completedDays) {
            current = if (previousDay == 0L || day - previousDay == 24 * 60 * 60 * 1000L) {
                current + 1
            } else 1
            best = maxOf(best, current)
            previousDay = day
        }
        return best
    }

    private fun normalizeToDay(millis: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}