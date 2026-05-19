package com.fervillalba.habittracker.presentation.stats

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.fervillalba.habittracker.domain.model.HabitLog
import java.util.Calendar

@Composable
fun HabitHeatmap(
    logs: List<HabitLog>,
    modifier: Modifier = Modifier
) {
    val completedDays = logs.map { normalizeToDay(it.completedAt) }.toSet()

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp)
    ) {
        val columns = 30
        val cellSize = size.width / (columns + 1)
        val gap = cellSize * 0.15f
        val cornerRadius = cellSize * 0.2f

        for (i in 0 until columns) {
            val dayMillis = normalizeToDay(
                System.currentTimeMillis() - (columns - 1 - i).toLong() * 24 * 60 * 60 * 1000
            )
            val isCompleted = dayMillis in completedDays
            val color = if (isCompleted) Color(0xFF7F77DD) else Color(0xFF2A2A38)

            drawRoundRect(
                color = color,
                topLeft = Offset(
                    x = i * (cellSize + gap),
                    y = size.height / 2 - cellSize / 2
                ),
                size = Size(cellSize, cellSize),
                cornerRadius = CornerRadius(cornerRadius, cornerRadius)
            )
        }
    }
}

private fun normalizeToDay(millis: Long): Long {
    val calendar = Calendar.getInstance()
    calendar.timeInMillis = millis
    calendar.set(Calendar.HOUR_OF_DAY, 0)
    calendar.set(Calendar.MINUTE, 0)
    calendar.set(Calendar.SECOND, 0)
    calendar.set(Calendar.MILLISECOND, 0)
    return calendar.timeInMillis
}