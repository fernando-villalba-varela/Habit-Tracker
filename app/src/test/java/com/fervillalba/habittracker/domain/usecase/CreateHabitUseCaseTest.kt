package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertThrows
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.never
import org.mockito.kotlin.verify

class CreateHabitUseCaseTest {

    private lateinit var repository: HabitRepository
    private lateinit var useCase: CreateHabitUseCase

    @Before
    fun setUp() {
        repository = mock()
        useCase = CreateHabitUseCase(repository)
    }

    @Test
    fun `when name is blank, throws IllegalArgumentException`() = runTest {
        val habit = Habit(name = "   ")

        assertThrows(IllegalArgumentException::class.java) {
            kotlinx.coroutines.runBlocking { useCase(habit) }
        }

        verify(repository, never()).insertHabit(habit)
    }

    @Test
    fun `when name is valid, inserts habit`() = runTest {
        val habit = Habit(name = "Correr")

        useCase(habit)

        verify(repository).insertHabit(habit)
    }
}