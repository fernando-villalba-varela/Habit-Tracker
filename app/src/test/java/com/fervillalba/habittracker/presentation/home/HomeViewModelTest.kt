package com.fervillalba.habittracker.presentation.home

import android.app.Application
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.usecase.CompleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.CreateHabitUseCase
import com.fervillalba.habittracker.domain.usecase.DeleteHabitUseCase
import com.fervillalba.habittracker.domain.usecase.GetHabitsUseCase
import com.fervillalba.habittracker.domain.usecase.GetTodayLogsUseCase
import com.fervillalba.habittracker.util.Resource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private lateinit var application: Application
    private lateinit var getHabitsUseCase: GetHabitsUseCase
    private lateinit var completeHabitUseCase: CompleteHabitUseCase
    private lateinit var getTodayLogsUseCase: GetTodayLogsUseCase
    private lateinit var deleteHabitUseCase: DeleteHabitUseCase
    private lateinit var createHabitUseCase: CreateHabitUseCase
    private lateinit var viewModel: HomeViewModel

    private val testDispatcher = UnconfinedTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        application = mock()
        whenever(application.applicationContext).thenReturn(application)
        
        getHabitsUseCase = mock()
        completeHabitUseCase = mock()
        getTodayLogsUseCase = mock()
        deleteHabitUseCase = mock()
        createHabitUseCase = mock()
        
        whenever(getHabitsUseCase()).thenReturn(flowOf(emptyList()))
        runTest {
            whenever(getTodayLogsUseCase()).thenReturn(emptyList())
        }
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

        viewModel = HomeViewModel(
            application,
            getHabitsUseCase,
            completeHabitUseCase,
            getTodayLogsUseCase,
            deleteHabitUseCase,
            createHabitUseCase
        )

        assertEquals(habits, viewModel.uiState.value.habits)
        assertEquals(false, viewModel.uiState.value.isLoading)
    }

    @Test
    fun `when habit completed successfully, completedHabitIds contains habit id`() = runTest {
        whenever(completeHabitUseCase(any(), any())).thenReturn(Resource.Success(Unit))

        viewModel = HomeViewModel(
            application,
            getHabitsUseCase,
            completeHabitUseCase,
            getTodayLogsUseCase,
            deleteHabitUseCase,
            createHabitUseCase
        )

        viewModel.completeHabit(1L)

        assertTrue(1L in viewModel.uiState.value.completedHabitIds)
    }

    @Test
    fun `when habit deleted successfully, deletedHabitMessage is set`() = runTest {
        val habit = Habit(id = 1, name = "Correr")
        whenever(deleteHabitUseCase(any())).thenReturn(Resource.Success(Unit))

        viewModel = HomeViewModel(
            application,
            getHabitsUseCase,
            completeHabitUseCase,
            getTodayLogsUseCase,
            deleteHabitUseCase,
            createHabitUseCase
        )

        viewModel.deleteHabit(habit)

        assertNotNull(viewModel.uiState.value.deletedHabitMessage)
    }
}
