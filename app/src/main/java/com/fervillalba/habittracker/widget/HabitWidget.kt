package com.fervillalba.habittracker.widget

import android.annotation.SuppressLint
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

@SuppressLint("RestrictedApi")
@Composable
private fun WidgetContent(
    habits: List<Habit>,
    completedIds: Set<Long>
) {
    val completedCount = habits.count { it.id in completedIds }
    val total = habits.size

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xF2111116))
            .padding(16.dp)
    ) {
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Mis hábitos",
                style = TextStyle(
                    color = androidx.glance.unit.ColorProvider(Color(0xFF7F77DD)),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            androidx.glance.layout.Spacer(modifier = GlanceModifier.defaultWeight())
            Text(
                text = "$completedCount/$total",
                style = TextStyle(
                    color = androidx.glance.unit.ColorProvider(Color(0xFF534AB7)),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }

        androidx.glance.layout.Spacer(modifier = GlanceModifier.padding(top = 8.dp))

        if (habits.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Añade hábitos en la app",
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
                        .padding(vertical = 3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = habit.iconEmoji,
                        style = TextStyle(fontSize = 14.sp),
                        modifier = GlanceModifier.padding(end = 8.dp)
                    )
                    Text(
                        text = habit.name,
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(
                                if (isCompleted) Color(0xFF534AB7) else Color(0xFFB0AECE)
                            ),
                            fontSize = 13.sp,
                            fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                            textDecoration = if (isCompleted)
                                androidx.glance.text.TextDecoration.LineThrough
                            else
                                androidx.glance.text.TextDecoration.None
                        ),
                        modifier = GlanceModifier.defaultWeight()
                    )
                    Text(
                        text = if (isCompleted) "✓" else "·",
                        style = TextStyle(
                            color = androidx.glance.unit.ColorProvider(
                                if (isCompleted) Color(0xFF7F77DD) else Color(0xFF2A2A38)
                            ),
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }
    }
}