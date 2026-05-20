package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.repository.HabitRepository
import com.fervillalba.habittracker.util.Resource
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
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
    fun `when name is blank, returns error resource`() = runTest {
        val habit = Habit(name = "   ")

        val result = useCase(habit)

        assertTrue(result is Resource.Error)
        verify(repository, never()).insertHabit(any())
    }

    @Test
    fun `when name is valid, inserts habit and returns success`() = runTest {
        val habit = Habit(name = "Correr")

        val result = useCase(habit)

        assertTrue(result is Resource.Success)
        verify(repository).insertHabit(habit)
    }
}
