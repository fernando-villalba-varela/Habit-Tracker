package com.fervillalba.habittracker.data.repository

import com.fervillalba.habittracker.data.local.HabitDao
import com.fervillalba.habittracker.data.mapper.HabitMapper.toDomain
import com.fervillalba.habittracker.data.mapper.HabitMapper.toEntity
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class HabitRepositoryImpl(
    private val dao: HabitDao
) : HabitRepository {

    override fun getActiveHabits(): Flow<List<Habit>> {
        return dao.getActiveHabits().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getHabitById(id: Long): Flow<Habit?> {
        return dao.getHabitById(id).map { it?.toDomain() }
    }

    override suspend fun insertHabit(habit: Habit) {
        dao.insertHabit(habit.toEntity())
    }

    override suspend fun updateHabit(habit: Habit) {
        dao.updateHabit(habit.toEntity())
    }

    override suspend fun deleteHabit(habit: Habit) {
        dao.deleteHabit(habit.toEntity())
    }

    override suspend fun insertHabitLog(habitLog: HabitLog) {
        dao.insertHabitLog(habitLog.toEntity())
    }

    override suspend fun getLogsForHabit(habitId: Long): List<HabitLog> {
        return dao.getLogsForHabit(habitId).map { it.toDomain() }
    }

    override suspend fun getLogsForDate(dateMillis: Long): List<HabitLog> {
        val startOfDay = getStartOfDay(dateMillis)
        val endOfDay = startOfDay + com.fervillalba.habittracker.Constants.MILLIS_IN_DAY
        return dao.getLogsForDate(startOfDay, endOfDay).map { it.toDomain() }
    }

    override suspend fun deleteHabitLog(habitLog: HabitLog) {
        dao.deleteHabitLog(habitLog.toEntity())
    }

    private fun getStartOfDay(millis: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    override suspend fun getActiveHabitsOnce(): List<Habit> {
        return dao.getActiveHabitsOnce().map { it.toDomain() }
    }
}