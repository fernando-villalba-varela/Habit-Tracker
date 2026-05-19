package com.fervillalba.habittracker.domain.repository

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitLog
import kotlinx.coroutines.flow.Flow

interface HabitRepository {
    fun getActiveHabits(): Flow<List<Habit>>
    fun getHabitById(id: Long): Flow<Habit?>
    suspend fun insertHabit(habit: Habit)
    suspend fun updateHabit(habit: Habit)
    suspend fun deleteHabit(habit: Habit)
    suspend fun insertHabitLog(habitLog: HabitLog)
    suspend fun getLogsForHabit(habitId: Long): List<HabitLog>
    suspend fun getLogsForDate(dateMillis: Long): List<HabitLog>
    suspend fun deleteHabitLog(habitLog: HabitLog)
}