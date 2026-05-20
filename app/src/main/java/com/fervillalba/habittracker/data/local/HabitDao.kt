package com.fervillalba.habittracker.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface HabitDao {

    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt ASC")
    fun getActiveHabits(): Flow<List<HabitEntity>>

    @Query("SELECT * FROM habits WHERE id = :id")
    fun getHabitById(id: Long): Flow<HabitEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity): Long

    @Update
    suspend fun updateHabit(habit: HabitEntity)

    @Delete
    suspend fun deleteHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabitLog(log: HabitLogEntity)

    @Query("SELECT * FROM habit_logs WHERE habitId = :habitId ORDER BY completedAt DESC")
    suspend fun getLogsForHabit(habitId: Long): List<HabitLogEntity>

    @Query("SELECT * FROM habit_logs WHERE completedAt >= :startOfDay AND completedAt < :endOfDay")
    suspend fun getLogsForDate(startOfDay: Long, endOfDay: Long): List<HabitLogEntity>

    @Delete
    suspend fun deleteHabitLog(log: HabitLogEntity)
    @Query("SELECT * FROM habits WHERE isActive = 1 ORDER BY createdAt ASC")
    suspend fun getActiveHabitsOnce(): List<HabitEntity>
}