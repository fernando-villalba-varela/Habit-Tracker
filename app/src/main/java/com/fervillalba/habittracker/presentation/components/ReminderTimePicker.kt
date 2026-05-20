package com.fervillalba.habittracker.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TimePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.fervillalba.habittracker.ui.theme.Border
import com.fervillalba.habittracker.ui.theme.Purple
import com.fervillalba.habittracker.ui.theme.Surface
import com.fervillalba.habittracker.ui.theme.TextSecondary
import com.fervillalba.habittracker.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReminderTimeSelector(
    reminderTime: String?,
    showTimePicker: Boolean,
    onShowTimePicker: (Boolean) -> Unit,
    onTimeSelected: (String?) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            text = "Recordatorio",
            style = MaterialTheme.typography.labelLarge,
            color = Purple,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(Surface)
                .clickable { onShowTimePicker(true) }
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (reminderTime != null)
                        Icons.Default.Notifications
                    else
                        Icons.Default.NotificationsOff,
                    contentDescription = null,
                    tint = if (reminderTime != null) Purple else TextTertiary
                )
                Text(
                    text = reminderTime ?: "Sin recordatorio",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (reminderTime != null) MaterialTheme.colorScheme.onSurface else TextTertiary
                )
            }
            if (reminderTime != null) {
                TextButton(onClick = { onTimeSelected(null) }) {
                    Text("Quitar", color = TextSecondary)
                }
            }
        }
    }

    if (showTimePicker) {
        val parts = reminderTime?.split(":")
        val initialHour = parts?.getOrNull(0)?.toIntOrNull() ?: 8
        val initialMinute = parts?.getOrNull(1)?.toIntOrNull() ?: 0

        val timePickerState = rememberTimePickerState(
            initialHour = initialHour,
            initialMinute = initialMinute
        )

        Dialog(onDismissRequest = { onShowTimePicker(false) }) {
            Column(
                modifier = Modifier
                    .clip(RoundedCornerShape(20.dp))
                    .background(Surface)
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Hora del recordatorio",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { onShowTimePicker(false) }) {
                        Text("Cancelar", color = TextSecondary)
                    }
                    TextButton(
                        onClick = {
                            val hour = timePickerState.hour.toString().padStart(2, '0')
                            val minute = timePickerState.minute.toString().padStart(2, '0')
                            onTimeSelected("$hour:$minute")
                            onShowTimePicker(false)
                        }
                    ) {
                        Text("Aceptar", color = Purple)
                    }
                }
            }
        }
    }
}