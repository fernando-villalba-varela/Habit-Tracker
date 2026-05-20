package com.fervillalba.habittracker.data.manager

import android.content.Context
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.fervillalba.habittracker.domain.manager.ReminderManager
import com.fervillalba.habittracker.worker.HabitReminderWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import java.util.Calendar
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class WorkManagerReminderManager @Inject constructor(
    @ApplicationContext private val context: Context
) : ReminderManager {

    override fun scheduleHabitReminders(habitId: Long, habitName: String, reminderTimes: List<String>) {
        // Primero cancelamos los anteriores para este hábito
        cancelHabitReminder(habitId)

        reminderTimes.forEachIndexed { index, time ->
            val parts = time.split(":")
            val hour = parts.getOrNull(0)?.toIntOrNull() ?: return@forEachIndexed
            val minute = parts.getOrNull(1)?.toIntOrNull() ?: return@forEachIndexed

            val now = Calendar.getInstance()
            val target = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, hour)
                set(Calendar.MINUTE, minute)
                set(Calendar.SECOND, 0)
            }

            if (target.before(now)) {
                target.add(Calendar.DAY_OF_YEAR, 1)
            }

            val delay = target.timeInMillis - now.timeInMillis

            val request = PeriodicWorkRequestBuilder<HabitReminderWorker>(
                repeatInterval = 24,
                repeatIntervalTimeUnit = TimeUnit.HOURS
            )
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .addTag("habit_$habitId") // Usamos tag para poder cancelar todos juntos
                .setInputData(workDataOf("habit_name" to habitName))
                .build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                "habit_reminder_${habitId}_$index",
                ExistingPeriodicWorkPolicy.REPLACE,
                request
            )
        }
    }

    override fun cancelHabitReminder(habitId: Long) {
        // Cancelamos por tag para eliminar todos los recordatorios del hábito
        WorkManager.getInstance(context).cancelAllWorkByTag("habit_$habitId")
    }
}
