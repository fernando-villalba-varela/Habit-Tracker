package com.fervillalba.habittracker.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.domain.repository.HabitRepository
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent

class HabitWidget : GlanceAppWidget() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface HabitWidgetEntryPoint {
        fun habitRepository(): HabitRepository
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val repository = EntryPointAccessors.fromApplication(
            context.applicationContext,
            HabitWidgetEntryPoint::class.java
        ).habitRepository()

        val habits = repository.getActiveHabitsOnce()
        val todayLogs = repository.getLogsForDate(System.currentTimeMillis())
        val completedIds = todayLogs.map { it.habitId }.toSet()

        provideContent {
            GlanceTheme {
                WidgetContent(habits = habits, completedIds = completedIds)
            }
        }
    }
}

@Composable
private fun WidgetContent(
    habits: List<Habit>,
    completedIds: Set<Long>
) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF0A0A0E))
            .padding(12.dp)
    ) {
        Text(
            text = "Mis hábitos",
            style = TextStyle(
                color = androidx.glance.unit.ColorProvider(Color(0xFF7F77DD)),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            ),
            modifier = GlanceModifier.padding(bottom = 8.dp)
        )

        if (habits.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No hay hábitos",
                    style = TextStyle(
                        color = androidx.glance.unit.ColorProvider(Color(0xFF555566)),
                        fontSize = 12.sp
                    )
                )
            }
        } else {
            habits.take(4).forEach { habit ->
                val isCompleted = habit.id in completedIds
                Row(
                    modifier = GlanceModifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isCompleted) "✅" else "⬜",
                        modifier = GlanceModifier.padding(end = 8.dp)
                    )
                    Text(
                        text = habit.name,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(
                                if (isCompleted) Color(0xFF7F77DD) else Color(0xFFB0AECE)
                            ),
                            fontSize = 13.sp
                        )
                    )
                }
            }
        }
    }
}