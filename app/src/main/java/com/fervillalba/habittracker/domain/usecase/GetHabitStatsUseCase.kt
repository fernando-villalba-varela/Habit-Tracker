package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

data class HabitStats(
    val habit: Habit,
    val currentStreak: Int,
    val bestStreak: Int,
    val completionRate: Float,
    val totalCompleted: Int,
    val last30DaysLogs: List<HabitLog>
)

class GetHabitStatsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(): List<HabitStats> {
        val habits = repository.getActiveHabitsOnce()
        return habits.map { habit ->
            val logs = repository.getLogsForHabit(habit.id)
            val last30Days = getLast30DaysLogs(logs)
            HabitStats(
                habit = habit,
                currentStreak = calculateCurrentStreak(logs),
                bestStreak = calculateBestStreak(logs),
                completionRate = calculateCompletionRate(last30Days),
                totalCompleted = logs.size,
                last30DaysLogs = last30Days
            )
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
            } else {
                1
            }
            best = maxOf(best, current)
            previousDay = day
        }
        return best
    }

    private fun calculateCompletionRate(last30DaysLogs: List<HabitLog>): Float {
        return (last30DaysLogs.size / 30f).coerceIn(0f, 1f)
    }

    private fun normalizeToDay(millis: Long): Long {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = millis
        calendar.set(java.util.Calendar.HOUR_OF_DAY, 0)
        calendar.set(java.util.Calendar.MINUTE, 0)
        calendar.set(java.util.Calendar.SECOND, 0)
        calendar.set(java.util.Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
}