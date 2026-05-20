package com.fervillalba.habittracker.di

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.fervillalba.habittracker.worker.HabitReminderWorker
import java.util.Calendar
import java.util.concurrent.TimeUnit

object WorkManagerHelper {

    fun scheduleHabitReminder(context: Context) {
        val request = PeriodicWorkRequestBuilder<HabitReminderWorker>(
            repeatInterval = 24,
            repeatIntervalTimeUnit = TimeUnit.HOURS
        )
            .setConstraints(Constraints.NONE)
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            HabitReminderWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            request
        )
    }

    fun scheduleHabitReminderAtTime(
        context: Context,
        habitId: Long,
        habitName: String,
        reminderTime: String
    ) {
        val parts = reminderTime.split(":")
        val hour = parts.getOrNull(0)?.toIntOrNull() ?: return
        val minute = parts.getOrNull(1)?.toIntOrNull() ?: return

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
            .setInputData(workDataOf("habit_name" to habitName))
            .build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "habit_reminder_$habitId",
            ExistingPeriodicWorkPolicy.REPLACE,
            request
        )
    }

    fun cancelHabitReminder(context: Context, habitId: Long) {
        WorkManager.getInstance(context).cancelUniqueWork("habit_reminder_$habitId")
    }
}