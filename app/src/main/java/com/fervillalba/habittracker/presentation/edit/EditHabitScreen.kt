package com.fervillalba.habittracker.presentation.edit

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.presentation.components.MultiReminderSelector
import com.fervillalba.habittracker.ui.theme.Background
import com.fervillalba.habittracker.ui.theme.Border
import com.fervillalba.habittracker.ui.theme.Purple
import com.fervillalba.habittracker.ui.theme.PurpleDim
import com.fervillalba.habittracker.ui.theme.Surface
import com.fervillalba.habittracker.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun EditHabitScreen(
    onNavigateBack: () -> Unit,
    viewModel: EditHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Editar hábito",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = Constants.Dimens.PaddingLarge)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingExtraLarge)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Constants.Dimens.PaddingSmall)) {
                Text(
                    text = "Nombre",
                    style = MaterialTheme.typography.labelLarge,
                    color = Purple,
                    fontWeight = FontWeight.Bold
                )
                OutlinedTextField(
                    value = uiState.name,
                    onValueChange = viewModel::onNameChange,
                    placeholder = { Text("Ej: Correr 20 minutos", color = TextTertiary) },
                    isError = uiState.nameError != null,
                    supportingText = {
                        uiState.nameError?.let { error ->
                            Text(error.asString(), color = MaterialTheme.colorScheme.error)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(Constants.Dimens.RadiusMedium),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = Border,
                        focusedContainerColor = Surface,
                        unfocusedContainerColor = Surface,
                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium)) {
                Text(
                    text = "Icono",
                    style = MaterialTheme.typography.labelLarge,
                    color = Purple,
                    fontWeight = FontWeight.Bold
                )
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall),
                    verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Constants.Dimens.RadiusMedium))
                        .background(Surface)
                        .padding(Constants.Dimens.PaddingSmall)
                ) {
                    Constants.EMOJI_OPTIONS.forEach { emoji ->
                        val isSelected = uiState.iconEmoji == emoji
                        Box(
                            modifier = Modifier
                                .size(Constants.Dimens.BoxSizeEmoji)
                                .clip(RoundedCornerShape(Constants.Dimens.RadiusSmall))
                                .background(if (isSelected) Purple else Color.White.copy(alpha = 0.05f))
                                .clickable { viewModel.onEmojiChange(emoji) },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 24.sp)
                        }
                    }
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium)) {
                Text(
                    text = "Frecuencia",
                    style = MaterialTheme.typography.labelLarge,
                    color = Purple,
                    fontWeight = FontWeight.Bold
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(Constants.Dimens.RadiusMedium))
                        .background(Surface)
                        .padding(Constants.Dimens.PaddingSmall)
                ) {
                    HabitFrequency.entries.forEach { frequency ->
                        val isSelected = uiState.frequency == frequency
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clip(RoundedCornerShape(10.dp))
                                .background(if (isSelected) Purple else Color.Transparent)
                                .clickable { viewModel.onFrequencyChange(frequency) }
                                .padding(vertical = 12.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = when (frequency) {
                                    HabitFrequency.DAILY -> "Diario"
                                    HabitFrequency.WEEKDAYS -> "Laboral"
                                    HabitFrequency.WEEKENDS -> "Fin de semana"
                                },
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) Color.White else TextTertiary
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            MultiReminderSelector(
                reminderTimes = uiState.reminderTimes,
                showTimePicker = uiState.showTimePicker,
                onShowTimePicker = viewModel::onShowTimePicker,
                onTimeAdded = viewModel::addReminderTime,
                onTimeRemoved = viewModel::removeReminderTime
            )

            Button(
                onClick = { viewModel.saveHabit(onNavigateBack) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp)
                    .padding(vertical = Constants.Dimens.PaddingSmall),
                enabled = !uiState.isLoading,
                shape = RoundedCornerShape(Constants.Dimens.RadiusMedium),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Purple,
                    contentColor = Color.White,
                    disabledContainerColor = PurpleDim.copy(alpha = 0.5f)
                )
            ) {
                Text(
                    text = if (uiState.isLoading) "Guardando..." else "Guardar cambios",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}