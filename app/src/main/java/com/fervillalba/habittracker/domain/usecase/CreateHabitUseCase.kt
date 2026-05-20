package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.repository.HabitRepository
import com.fervillalba.habittracker.util.Resource
import com.fervillalba.habittracker.util.UiText
import javax.inject.Inject

class CreateHabitUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit): Resource<Long> {
        if (habit.name.isBlank()) {
            return Resource.Error(UiText.StringResource(R.string.error_empty_name))
        }
        return try {
            val id = repository.insertHabit(habit)
            Resource.Success(id)
        } catch (e: Exception) {
            Resource.Error(UiText.DynamicString(e.message ?: "Unknown error"))
        }
    }
}
