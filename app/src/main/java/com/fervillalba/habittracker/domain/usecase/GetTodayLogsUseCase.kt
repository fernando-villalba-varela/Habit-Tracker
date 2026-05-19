package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.domain.repository.HabitRepository
import javax.inject.Inject

class GetTodayLogsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    suspend operator fun invoke(): List<HabitLog> {
        return repository.getLogsForDate(System.currentTimeMillis())
    }
}