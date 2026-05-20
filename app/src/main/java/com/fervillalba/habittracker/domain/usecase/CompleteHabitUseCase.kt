package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.repository.HabitRepository
import com.fervillalba.habittracker.util.Resource
import com.fervillalba.habittracker.util.UiText
import timber.log.Timber
import javax.inject.Inject

class CompleteHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long, note: String? = null): Resource<Unit> {
        return try {
            val log = HabitLog(
                habitId = habitId,
                note = note
            )
            repository.insertHabitLog(log)
            Timber.d("Habit completed: ID $habitId")
            Resource.Success(Unit)
        } catch (e: Exception) {
            Timber.e(e, "Error completing habit: ID $habitId")
            Resource.Error(UiText.DynamicString(e.message ?: "Unknown error"))
        }
    }
}
