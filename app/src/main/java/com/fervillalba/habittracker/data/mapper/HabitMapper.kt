package com.fervillalba.habittracker.data.mapper

import com.fervillalba.habittracker.data.local.HabitEntity
import com.fervillalba.habittracker.data.local.HabitLogEntity
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.domain.model.HabitLog

object HabitMapper {

    fun HabitEntity.toDomain(): Habit {
        return Habit(
            id = id,
            name = name,
            iconEmoji = iconEmoji,
            colorHex = colorHex,
            frequency = HabitFrequency.valueOf(frequency),
            reminderTime = reminderTime,
            createdAt = createdAt,
            isActive = isActive
        )
    }

    fun Habit.toEntity(): HabitEntity {
        return HabitEntity(
            id = id,
            name = name,
            iconEmoji = iconEmoji,
            colorHex = colorHex,
            frequency = frequency.name,
            reminderTime = reminderTime,
            createdAt = createdAt,
            isActive = isActive
        )
    }

    fun HabitLogEntity.toDomain(): HabitLog {
        return HabitLog(
            id = id,
            habitId = habitId,
            completedAt = completedAt,
            note = note
        )
    }

    fun HabitLog.toEntity(): HabitLogEntity {
        return HabitLogEntity(
            id = id,
            habitId = habitId,
            completedAt = completedAt,
            note = note
        )
    }
}