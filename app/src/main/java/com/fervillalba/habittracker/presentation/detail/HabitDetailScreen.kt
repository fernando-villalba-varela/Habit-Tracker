package com.fervillalba.habittracker.presentation.detail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.presentation.components.HabitCalendar
import com.fervillalba.habittracker.ui.theme.Background
import com.fervillalba.habittracker.ui.theme.Purple
import com.fervillalba.habittracker.ui.theme.PurpleDark
import com.fervillalba.habittracker.ui.theme.Surface
import com.fervillalba.habittracker.ui.theme.TextSecondary
import com.fervillalba.habittracker.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.roundToInt

@Composable
fun HabitDetailScreen(
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    viewModel: HabitDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    HabitDetailContent(
        uiState = uiState,
        onNavigateBack = onNavigateBack,
        onNavigateToEdit = onNavigateToEdit,
        onDeleteHabit = {
            viewModel.deleteHabit()
            onNavigateBack()
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HabitDetailContent(
    uiState: HabitDetailUiState,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onDeleteHabit: () -> Unit
) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text(stringResource(R.string.delete_habit_confirm)) },
            text = { Text(stringResource(R.string.delete_habit_message)) },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteHabit()
                        showDeleteDialog = false
                    }
                ) {
                    Text(stringResource(R.string.delete_content_desc), color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text(stringResource(R.string.undo))
                }
            }
        )
    }

    Scaffold(
        containerColor = Background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.habit_detail_title),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_desc)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { uiState.habit?.id?.let { onNavigateToEdit(it) } }) {
                        Icon(Icons.Default.Edit, contentDescription = stringResource(R.string.edit_content_desc))
                    }
                    IconButton(onClick = { showDeleteDialog = true }) {
                        Icon(Icons.Default.Delete, contentDescription = stringResource(R.string.delete_content_desc))
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Purple)
                }
            }
            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = uiState.error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(Constants.Dimens.PaddingLarge)
                    )
                }
            }
            uiState.habit == null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(stringResource(R.string.habit_not_found), color = TextSecondary)
                }
            }
            else -> {
                val habit = uiState.habit
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = Constants.Dimens.PaddingLarge),
                    verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingLarge)
                ) {
                    item {
                        // Header del hábito
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                                .background(Surface)
                                .padding(Constants.Dimens.PaddingLarge)
                        ) {
                            Row(
                                horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingLarge),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(Constants.Dimens.IconSizeLarge + 16.dp)
                                        .clip(RoundedCornerShape(Constants.Dimens.RadiusSmall))
                                        .background(PurpleDark),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = habit.iconEmoji,
                                        fontSize = Constants.Dimens.EmojiSizeDetail
                                    )
                                }
                                Column {
                                    Text(
                                        text = habit.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = when (habit.frequency) {
                                            com.fervillalba.habittracker.domain.model.HabitFrequency.DAILY -> stringResource(R.string.frequency_daily)
                                            com.fervillalba.habittracker.domain.model.HabitFrequency.WEEKDAYS -> stringResource(R.string.frequency_weekdays)
                                            com.fervillalba.habittracker.domain.model.HabitFrequency.WEEKENDS -> stringResource(R.string.frequency_weekends)
                                        },
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Purple
                                    )
                                    habit.reminderTimes.firstOrNull()?.let { time ->
                                        Text(
                                            text = "⏰ " + stringResource(R.string.reminder_at, time),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextTertiary
                                        )
                                    }
                                    Text(
                                        text = "📅 " + stringResource(R.string.tracking_since, formatDateShort(habit.createdAt)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Stats cards
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall)
                        ) {
                            DetailStatCard(
                                modifier = Modifier.weight(1f),
                                emoji = "🔥",
                                value = "${uiState.currentStreak}",
                                label = stringResource(R.string.current_streak)
                            )
                            DetailStatCard(
                                modifier = Modifier.weight(1f),
                                emoji = "⭐",
                                value = "${uiState.bestStreak}",
                                label = stringResource(R.string.best_streak)
                            )
                            DetailStatCard(
                                modifier = Modifier.weight(1f),
                                emoji = "✅",
                                value = "${uiState.totalCompleted}",
                                label = stringResource(R.string.total_times_label)
                            )
                            DetailStatCard(
                                modifier = Modifier.weight(1f),
                                emoji = "📈",
                                value = "${(uiState.completionRate * 100).roundToInt()}%",
                                label = stringResource(R.string.last_30_days_short)
                            )
                        }
                    }

                    item {
                        // Monthly Calendar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                                .background(Surface)
                                .padding(Constants.Dimens.PaddingLarge)
                        ) {
                            Column(
                                verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium)
                            ) {
                                Text(
                                    text = "Calendario Mensual",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = TextTertiary
                                )
                                HabitCalendar(logs = uiState.logs)
                            }
                        }
                    }

                    item {
                        Text(
                            text = stringResource(R.string.history_label),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    if (uiState.logs.isEmpty()) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                                    .background(Surface)
                                    .padding(Constants.Dimens.PaddingLarge),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = stringResource(R.string.no_logs_yet),
                                    color = TextTertiary,
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                        }
                    } else {
                        items(uiState.logs.take(30)) { log ->
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(Constants.Dimens.RadiusSmall))
                                    .background(Surface)
                                    .padding(Constants.Dimens.PaddingMedium)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "✅ " + stringResource(R.string.completed_at),
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Text(
                                        text = formatDate(log.completedAt),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextTertiary
                                    )
                                }
                            }
                        }
                    }
                    item { }
                }
            }
        }
    }
}

@Composable
private fun DetailStatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Constants.Dimens.RadiusSmall))
            .background(PurpleDark)
            .padding(vertical = 12.dp, horizontal = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 18.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Purple
            )
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall,
                color = TextTertiary
            )
        }
    }
}

private fun formatDate(millis: Long): String {
    val format = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale("es", "ES"))
    return format.format(Date(millis))
}

private fun formatDateShort(millis: Long): String {
    val format = SimpleDateFormat("dd/MM/yyyy", Locale("es", "ES"))
    return format.format(Date(millis))
}
