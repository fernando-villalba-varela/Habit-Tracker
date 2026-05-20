package com.fervillalba.habittracker.data.manager

import android.content.Context
import com.fervillalba.habittracker.domain.manager.WidgetManager
import com.fervillalba.habittracker.widget.WidgetUpdateWorker
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

class WorkManagerWidgetManager @Inject constructor(
    @ApplicationContext private val context: Context
) : WidgetManager {
    override fun updateWidget() {
        WidgetUpdateWorker.enqueue(context)
    }
}
