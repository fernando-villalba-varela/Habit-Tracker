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
import androidx.glance.appwidget.LinearProgressIndicator
import androidx.glance.appwidget.lazy.LazyColumn
import androidx.glance.appwidget.lazy.items
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.padding
import androidx.glance.layout.height
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
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

        // Usamos la consulta directa suspendida para mayor fiabilidad
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
    val progress = if (total > 0) completedCount.toFloat() / total else 0f

    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(Color(0xFF0F0F14))
            .padding(12.dp)
    ) {
        // Header
        Row(
            modifier = GlanceModifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = GlanceModifier.defaultWeight()) {
                Text(
                    text = "Habit Tracker",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFF7F77DD)),
                        fontSize = 14.sp,
                        fontWeight = FontWeight.Bold
                    )
                )
                Text(
                    text = "$completedCount de $total hechos",
                    style = TextStyle(
                        color = ColorProvider(Color(0xFFB0AECE)),
                        fontSize = 11.sp
                    )
                )
            }
            Text(
                text = "${(progress * 100).toInt()}%",
                style = TextStyle(
                    color = ColorProvider(Color(0xFF7F77DD)),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        LinearProgressIndicator(
            progress = progress,
            modifier = GlanceModifier.fillMaxWidth().height(6.dp),
            color = ColorProvider(Color(0xFF7F77DD)),
            backgroundColor = ColorProvider(Color(0xFF2A2A38))
        )

        Spacer(modifier = GlanceModifier.height(12.dp))

        if (habits.isEmpty()) {
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Añade hábitos en la app",
                    style = TextStyle(color = ColorProvider(Color(0xFF555566)), fontSize = 11.sp)
                )
            }
        } else {
            // Ordenamos y quitamos límites para permitir scroll total
            val sortedHabits = habits.sortedBy { it.id in completedIds }
            
            LazyColumn(
                modifier = GlanceModifier.fillMaxSize() // Fill the remaining space
            ) {
                items(sortedHabits) { habit ->
                    val isCompleted = habit.id in completedIds
                    HabitWidgetItem(habit, isCompleted)
                }
            }
        }
    }
}

@Composable
private fun HabitWidgetItem(habit: Habit, isCompleted: Boolean) {
    Row(
        modifier = GlanceModifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(if (isCompleted) Color(0xFF1D1D26).copy(alpha = 0.4f) else Color(0xFF1D1D26))
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = habit.iconEmoji,
            style = TextStyle(fontSize = 14.sp),
            modifier = GlanceModifier.padding(end = 10.dp)
        )
        Text(
            text = habit.name,
            style = TextStyle(
                color = ColorProvider(
                    if (isCompleted) Color(0xFF8E8E9F) else Color(0xFFFFFFFF)
                ),
                fontSize = 12.sp,
                fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Bold,
                textDecoration = if (isCompleted) 
                    androidx.glance.text.TextDecoration.LineThrough 
                else
                    androidx.glance.text.TextDecoration.None
            ),
            modifier = GlanceModifier.defaultWeight()
        )
        Text(
            text = if (isCompleted) "✓" else "○",
            style = TextStyle(
                color = ColorProvider(
                    if (isCompleted) Color(0xFF7F77DD) else Color(0xFF444455)
                ),
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold
            )
        )
    }
}
