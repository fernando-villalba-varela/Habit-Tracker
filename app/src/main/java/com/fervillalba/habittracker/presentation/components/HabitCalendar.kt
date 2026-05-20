package com.fervillalba.habittracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fervillalba.habittracker.domain.model.HabitLog
import com.fervillalba.habittracker.ui.theme.Purple
import com.fervillalba.habittracker.ui.theme.TextTertiary
import java.util.Calendar

@Composable
fun HabitCalendar(
    logs: List<HabitLog>,
    modifier: Modifier = Modifier
) {
    val calendar = Calendar.getInstance()
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentYear = calendar.get(Calendar.YEAR)
    
    // Set to first day of month
    calendar.set(Calendar.DAY_OF_MONTH, 1)
    val firstDayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
    val daysInMonth = calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    
    val completedDays = logs.filter { log ->
        val logCal = Calendar.getInstance().apply { timeInMillis = log.completedAt }
        logCal.get(Calendar.MONTH) == currentMonth && logCal.get(Calendar.YEAR) == currentYear
    }.map { log ->
        val logCal = Calendar.getInstance().apply { timeInMillis = log.completedAt }
        logCal.get(Calendar.DAY_OF_MONTH)
    }.toSet()

    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            listOf("L", "M", "X", "J", "V", "S", "D").forEach { day ->
                Text(
                    text = day,
                    style = MaterialTheme.typography.labelSmall,
                    color = TextTertiary,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        // Adjust for Monday start (Calendar.SUNDAY is 1)
        val offset = if (firstDayOfWeek == Calendar.SUNDAY) 6 else firstDayOfWeek - 2
        
        var dayCounter = 1
        for (row in 0..5) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                for (col in 0..6) {
                    val currentDayIndex = row * 7 + col
                    if (currentDayIndex < offset || dayCounter > daysInMonth) {
                        Box(modifier = Modifier.size(32.dp))
                    } else {
                        val isCompleted = completedDays.contains(dayCounter)
                        DayCircle(day = dayCounter, isCompleted = isCompleted)
                        dayCounter++
                    }
                }
            }
            if (dayCounter > daysInMonth) break
        }
    }
}

@Composable
private fun DayCircle(day: Int, isCompleted: Boolean) {
    Box(
        modifier = Modifier
            .size(32.dp)
            .padding(2.dp)
            .clip(CircleShape)
            .background(if (isCompleted) Purple else Color.Transparent),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = day.toString(),
            fontSize = 12.sp,
            color = if (isCompleted) Color.White else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isCompleted) FontWeight.Bold else FontWeight.Normal
        )
    }
}
