package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.repository.HabitRepository

class CreateHabitUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habit: Habit) {
        require(habit.name.isNotBlank()) { "Habit name cannot be empty" }
        repository.insertHabit(habit)
    }
}