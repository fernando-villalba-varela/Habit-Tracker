package com.fervillalba.habittracker.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.fervillalba.habittracker.notification.HabitNotificationHelper
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class HabitReminderWorker @AssistedInject constructor(
    @Assisted context: Context,
    @Assisted params: WorkerParameters,
    private val notificationHelper: HabitNotificationHelper
) : CoroutineWorker(context, params) {

    companion object {
        const val WORK_NAME = "HabitReminderWork"
    }

    override suspend fun doWork(): Result {
        val habitName = inputData.getString("habit_name") ?: "Hábito"
        
        notificationHelper.showNotification(
            title = "Recordatorio de hábito",
            message = "¡No olvides completar tu hábito: $habitName!",
            notificationId = habitName.hashCode()
        )
        
        return Result.success()
    }
}