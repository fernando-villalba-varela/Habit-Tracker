package com.fervillalba.habittracker.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.delay

@HiltWorker
class WidgetUpdateWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters
) : CoroutineWorker(context, params) {

    companion object {
        private const val WORK_NAME = "WidgetUpdateWork"

        fun enqueue(context: Context) {
            val request = OneTimeWorkRequestBuilder<WidgetUpdateWorker>()
                .build()
            
            // Usamos REPLACE para que si hay varias actualizaciones seguidas, 
            // solo se ejecute la última y no colapse el sistema.
            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME,
                ExistingWorkPolicy.REPLACE,
                request
            )
        }
    }

    override suspend fun doWork(): Result {
        return try {
            // Delay para asegurar que Room haya persistido los cambios
            delay(500)
            
            val manager = GlanceAppWidgetManager(context)
            val glanceIds = manager.getGlanceIds(HabitWidget::class.java)
            
            glanceIds.forEach { glanceId ->
                HabitWidget().update(context, glanceId)
            }
            
            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.retry()
        }
    }
}
