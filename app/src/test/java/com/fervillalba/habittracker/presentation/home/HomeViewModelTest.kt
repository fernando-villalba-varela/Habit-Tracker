package com.fervillalba.habittracker.presentation.home

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.usecase.CompleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.CreateHabitUseCase
import com.fervillalba.habittracker.domain.usecase.DeleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.GetHabitsUseCase
import com.fervillalba.habittracker.domain.usecase.GetTodayLogsUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var getHabitsUseCase: GetHabitsUseCase
    private lateinit var completeHabitUseCase: CompleteHabitUseCase
    private lateinit var getTodayLogsUseCase: GetTodayLogsUseCase
    private lateinit var deleteHabitUseCase: DeleteHabitUseCase
    private lateinit var createHabitUseCase: CreateHabitUseCase
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        getHabitsUseCase = mock()
        completeHabitUseCase = mock()
        getTodayLogsUseCase = mock()
        deleteHabitUseCase = mock()
        createHabitUseCase = mock()
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when habits loaded, state contains habits`() = runTest {
        val habits = listOf(
            Habit(id = 1, name = "Correr"),
            Habit(id = 2, name = "Leer")
        )
        whenever(getHabitsUseCase()).thenReturn(flowOf(habits))
        whenever(getTodayLogsUseCase()).thenReturn(emptyList())

        viewModel = HomeViewModel(
            getHabitsUseCase,
            completeHabitUseCase,
            getTodayLogsUseCase,
            deleteHabitUseCase,
            createHabitUseCase
        )

        advanceUntilIdle()

        assertEquals(habits, viewModel.uiState.value.habits)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `when habit completed, completedHabitIds contains habit id`() = runTest {
        val habits = listOf(Habit(id = 1, name = "Correr"))
        whenever(getHabitsUseCase()).thenReturn(flowOf(habits))
        whenever(getTodayLogsUseCase()).thenReturn(emptyList())

        viewModel = HomeViewModel(
            getHabitsUseCase,
            completeHabitUseCase,
            getTodayLogsUseCase,
            deleteHabitUseCase,
            createHabitUseCase
        )

        advanceUntilIdle()
        viewModel.completeHabit(1L)
        advanceUntilIdle()

        assertTrue(1L in viewModel.uiState.value.completedHabitIds)
    }

    @Test
    fun `when habit deleted, deletedHabitMessage is set`() = runTest {
        val habit = Habit(id = 1, name = "Correr")
        val habits = listOf(habit)
        whenever(getHabitsUseCase()).thenReturn(flowOf(habits))
        whenever(getTodayLogsUseCase()).thenReturn(emptyList())

        viewModel = HomeViewModel(
            getHabitsUseCase,
            completeHabitUseCase,
            getTodayLogsUseCase,
            deleteHabitUseCase,
            createHabitUseCase
        )

        advanceUntilIdle()
        viewModel.deleteHabit(habit)
        advanceUntilIdle()

        assertEquals("Hábito eliminado", viewModel.uiState.value.deletedHabitMessage)
    }
}