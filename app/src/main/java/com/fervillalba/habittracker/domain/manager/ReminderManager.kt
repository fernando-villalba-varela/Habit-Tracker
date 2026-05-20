package com.fervillalba.habittracker.domain.manager

interface ReminderManager {
    fun scheduleHabitReminders(habitId: Long, habitName: String, reminderTimes: List<String>)
    fun cancelHabitReminder(habitId: Long)
}
