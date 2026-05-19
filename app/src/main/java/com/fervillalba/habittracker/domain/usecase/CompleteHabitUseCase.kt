package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.repository.HabitRepository

class CompleteHabitUseCase(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(habitId: Long, note: String? = null) {
        val log = HabitLog(
            habitId = habitId,
            note = note
        )
        repository.insertHabitLog(log)
    }
}