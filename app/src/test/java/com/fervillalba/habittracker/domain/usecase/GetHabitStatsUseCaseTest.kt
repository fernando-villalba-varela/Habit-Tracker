package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class GetHabitStatsUseCaseTest {

    private lateinit var repository: HabitRepository
    private lateinit var useCase: GetHabitStatsUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = GetHabitStatsUseCase(repository)
    }

    @Test
    fun `when no logs, streaks are zero`() = runTest {
        val habit = Habit(id = 1, name = "Correr")
        whenever(repository.getActiveHabitsOnce()).thenReturn(listOf(habit))
        whenever(repository.getLogsForHabit(1L)).thenReturn(emptyList())

        val stats = useCase()

        assertEquals(0, stats.first().currentStreak)
        assertEquals(0, stats.first().bestStreak)
    }

    @Test
    fun `when completed today, current streak is 1`() = runTest {
        val habit = Habit(id = 1, name = "Correr")
        val todayLog = HabitLog(
            habitId = 1L,
            completedAt = System.currentTimeMillis()
        )
        whenever(repository.getActiveHabitsOnce()).thenReturn(listOf(habit))
        whenever(repository.getLogsForHabit(1L)).thenReturn(listOf(todayLog))

        val stats = useCase()

        assertEquals(1, stats.first().currentStreak)
    }

    @Test
    fun `when completed 3 consecutive days, best streak is 3`() = runTest {
        val habit = Habit(id = 1, name = "Correr")
        val now = System.currentTimeMillis()
        val logs = listOf(
            HabitLog(habitId = 1L, completedAt = now),
            HabitLog(habitId = 1L, completedAt = now - 24 * 60 * 60 * 1000),
            HabitLog(habitId = 1L, completedAt = now - 2 * 24 * 60 * 60 * 1000)
        )
        whenever(repository.getActiveHabitsOnce()).thenReturn(listOf(habit))
        whenever(repository.getLogsForHabit(1L)).thenReturn(logs)

        val stats = useCase()

        assertEquals(3, stats.first().bestStreak)
        assertEquals(3, stats.first().currentStreak)
    }
}