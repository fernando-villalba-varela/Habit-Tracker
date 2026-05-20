package com.fervillalba.habittracker.domain.usecase

import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.domain.repository.HabitRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Calendar
import javax.inject.Inject

class GetHabitsUseCase @Inject constructor(
    private val repository: HabitRepository
) {
    operator fun invoke(): Flow<List<Habit>> {
        return repository.getActiveHabits().map { habits ->
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
            val isWeekday = dayOfWeek in Calendar.MONDAY..Calendar.FRIDAY
            val isWeekend = dayOfWeek == Calendar.SATURDAY || dayOfWeek == Calendar.SUNDAY

            habits.filter { habit ->
                when (habit.frequency) {
                    HabitFrequency.DAILY -> true
                    HabitFrequency.WEEKDAYS -> isWeekday
                    HabitFrequency.WEEKENDS -> isWeekend
                }
            }
        }
    }
}