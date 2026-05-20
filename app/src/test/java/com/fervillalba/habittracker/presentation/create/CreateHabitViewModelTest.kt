package com.fervillalba.habittracker.presentation.create

import app.cash.turbine.test
import com.fervillalba.habittracker.domain.manager.ReminderManager
import com.fervillalba.habittracker.domain.manager.WidgetManager
import com.fervillalba.habittracker.domain.usecase.CreateHabitUseCase
import com.fervillalba.habittracker.util.DispatchersProvider
import com.fervillalba.habittracker.util.Resource
import com.fervillalba.habittracker.util.UiText
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import kotlin.time.Duration.Companion.seconds

@OptIn(ExperimentalCoroutinesApi::class)
class CreateHabitViewModelTest {

    private lateinit var createHabitUseCase: CreateHabitUseCase
    private lateinit var reminderManager: ReminderManager
    private lateinit var widgetManager: WidgetManager
    private lateinit var viewModel: CreateHabitViewModel
    
    private val testDispatcher = StandardTestDispatcher()
    
    private val dispatchersProvider = object : DispatchersProvider {
        override val main: CoroutineDispatcher = testDispatcher
        override val io: CoroutineDispatcher = testDispatcher
        override val default: CoroutineDispatcher = testDispatcher
        override val unconfined: CoroutineDispatcher = testDispatcher
    }

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        createHabitUseCase = mockk()
        reminderManager = mockk(relaxed = true)
        widgetManager = mockk(relaxed = true)
        
        viewModel = CreateHabitViewModel(
            createHabitUseCase = createHabitUseCase,
            reminderManager = reminderManager,
            widgetManager = widgetManager,
            dispatchers = dispatchersProvider
        )
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `when name changes, state is updated`() = runTest(testDispatcher) {
        viewModel.onNameChange("Nuevo Hábito")
        assertEquals("Nuevo Hábito", viewModel.uiState.value.name)
    }

    @Test
    fun `when habit created successfully, state transitions through loading to success`() = runTest(testDispatcher) {
        coEvery { createHabitUseCase(any()) } returns Resource.Success(1L)
        
        viewModel.uiState.test(timeout = 5.seconds) {
            assertEquals("", awaitItem().name) // Initial state
            
            viewModel.onNameChange("Hábito Test")
            assertEquals("Hábito Test", awaitItem().name)
            
            viewModel.createHabit { }
            
            // Reaccionamos al cambio de estado Loading
            testDispatcher.scheduler.runCurrent()
            
            val loadingState = awaitItem()
            assertTrue("Expected loading state", loadingState.isLoading)
            
            // Avanzamos el scheduler para que se complete el UseCase y el siguiente cambio de estado
            testDispatcher.scheduler.runCurrent()
            
            val successState = awaitItem()
            assertEquals("Expected non-loading state", false, successState.isLoading)
            
            cancelAndIgnoreRemainingEvents()
        }
    }

    @Test
    fun `when habit creation fails, error is set in state`() = runTest(testDispatcher) {
        val errorMessage = "Error de red"
        coEvery { createHabitUseCase(any()) } returns Resource.Error(UiText.DynamicString(errorMessage))

        viewModel.uiState.test(timeout = 5.seconds) {
            awaitItem() // Initial
            viewModel.onNameChange("Hábito Test")
            awaitItem() // Name change
            
            viewModel.createHabit { }
            
            testDispatcher.scheduler.runCurrent()
            
            assertTrue("Expected loading state", awaitItem().isLoading)
            
            testDispatcher.scheduler.runCurrent()
            
            val errorState = awaitItem()
            assertNotNull("Expected error message", errorState.error)
            assertEquals(false, errorState.isLoading)
        }
    }
}
