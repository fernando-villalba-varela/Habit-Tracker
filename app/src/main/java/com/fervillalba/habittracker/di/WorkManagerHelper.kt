package com.fervillalba.habittracker.di

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.fervillalba.habittracker.worker.HabitReminderWorker
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
}