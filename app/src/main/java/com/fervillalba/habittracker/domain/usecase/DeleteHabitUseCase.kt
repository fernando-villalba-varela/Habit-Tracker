package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.repository.HabitRepository
import com.fervillalba.habittracker.util.Resource
import com.fervillalba.habittracker.util.UiText
import timber.log.Timber
import javax.inject.Inject

class DeleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Resource<Unit> {
        return try {
            repository.deleteHabit(habit)
            Timber.d("Habit deleted: ${habit.name}")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error deleting habit: ${habit.name}")
            Resource.Error(UiText.DynamicString(e.message ?: "Unknown error"))
        }
    }
}
