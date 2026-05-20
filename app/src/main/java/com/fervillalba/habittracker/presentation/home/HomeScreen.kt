package com.fervillalba.habittracker.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.EventNote
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.outlined.EventNote
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.HabitFrequency
import com.fervillalba.habittracker.ui.theme.Background
import com.fervillalba.habittracker.ui.theme.Purple
import com.fervillalba.habittracker.ui.theme.PurpleDark
import com.fervillalba.habittracker.ui.theme.Surface
import com.fervillalba.habittracker.ui.theme.TextSecondary
import com.fervillalba.habittracker.ui.theme.TextTertiary
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HomeScreen(
    onNavigateToCreate: () -> Unit,
    onNavigateToStats: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val filteredHabits by viewModel.filteredHabits.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    val undoLabel = stringResource(R.string.undo)

    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it.asString(context))
            viewModel.clearError()
        }
    }

    LaunchedEffect(uiState.deletedHabitMessage) {
        uiState.deletedHabitMessage?.let {
            val result = snackbarHostState.showSnackbar(
                message = it.asString(context),
                actionLabel = undoLabel,
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                viewModel.undoDelete()
            } else {
                viewModel.clearDeletedMessage()
            }
        }
    }

    Scaffold(
        containerColor = Background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = Purple,
                contentColor = Color.White,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.shadow(8.dp, RoundedCornerShape(16.dp))
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_habit_content_desc),
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        horizontal = Constants.Dimens.PaddingLarge,
                        vertical = Constants.Dimens.SpacingLarge
                    ),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = getGreeting(context),
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextTertiary
                    )
                    Text(
                        text = stringResource(R.string.home_title),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    if (uiState.habits.isNotEmpty()) {
                        Text(
                            text = getTodayDate(),
                            style = MaterialTheme.typography.bodySmall,
                            color = TextTertiary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                }
                IconButton(
                    onClick = onNavigateToStats,
                    modifier = Modifier
                        .clip(CircleShape)
                        .background(Surface)
                ) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = stringResource(R.string.stats_content_desc),
                        tint = Purple
                    )
                }
            }

            // Progress card
            if (filteredHabits.isNotEmpty()) {
                val completed = filteredHabits.count { it.id in uiState.completedHabitIds }
                val total = filteredHabits.size
                val progress = if (total > 0) completed.toFloat() / total else 0f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Constants.Dimens.PaddingLarge)
                        .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                        .background(
                            brush = Brush.verticalGradient(
                                colors = listOf(Surface, Surface.copy(alpha = 0.8f))
                            )
                        )
                        .padding(Constants.Dimens.PaddingLarge)
                ) {
                    Column(
                        verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.today_progress),
                                    style = MaterialTheme.typography.labelLarge,
                                    color = Purple,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    text = stringResource(
                                        R.string.habits_completed_format,
                                        completed,
                                        total
                                    ),
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(CircleShape)
                                    .background(PurpleDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Purple
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Constants.Dimens.ProgressHeight)
                                .clip(CircleShape),
                            color = Purple,
                            trackColor = PurpleDark,
                            strokeCap = StrokeCap.Butt,
                            gapSize = 0.dp,
                            drawStopIndicator = {}
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Constants.Dimens.SpacingLarge))
            }

            // Filtros de frecuencia
            if (uiState.habits.isNotEmpty()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Constants.Dimens.PaddingLarge),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    FilterChip(
                        selected = uiState.selectedFilter == null,
                        onClick = { viewModel.onFilterChange(null) },
                        label = { Text("Todos") },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = Purple,
                            selectedLabelColor = Color.White
                        )
                    )
                    HabitFrequency.entries.forEach { frequency ->
                        FilterChip(
                            selected = uiState.selectedFilter == frequency,
                            onClick = { viewModel.onFilterChange(frequency) },
                            label = {
                                Text(
                                    when (frequency) {
                                        HabitFrequency.DAILY -> "Diario"
                                        HabitFrequency.WEEKDAYS -> "Laboral"
                                        HabitFrequency.WEEKENDS -> "Fin de semana"
                                    }
                                )
                            },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Purple,
                                selectedLabelColor = Color.White
                            )
                        )
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Lista o empty state
            when {
                uiState.habits.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium)
                        ) {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.padding(bottom = 16.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(120.dp)
                                        .clip(CircleShape)
                                        .background(Purple.copy(alpha = 0.08f))
                                )
                                Icon(
                                    imageVector = Icons.AutoMirrored.Outlined.EventNote,
                                    contentDescription = null,
                                    modifier = Modifier.size(64.dp),
                                    tint = Purple.copy(alpha = 0.6f)
                                )
                            }
                            Text(
                                text = stringResource(R.string.no_habits_yet),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = stringResource(R.string.click_to_create_first),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary,
                                modifier = Modifier.padding(horizontal = 48.dp),
                                textAlign = TextAlign.Center,
                                lineHeight = 20.sp
                            )
                        }
                    }
                }
                filteredHabits.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text("🔍", fontSize = 48.sp)
                            Text(
                                text = "No hay hábitos con este filtro",
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                        }
                    }
                }
                else -> {
                    // Mensaje celebración
                    if (filteredHabits.isNotEmpty() &&
                        filteredHabits.all { it.id in uiState.completedHabitIds }
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = Constants.Dimens.PaddingLarge)
                                .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                                .background(Purple.copy(alpha = 0.15f))
                                .padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text("🎉", fontSize = 28.sp)
                                Column {
                                    Text(
                                        text = "¡Todos completados!",
                                        style = MaterialTheme.typography.titleSmall,
                                        fontWeight = FontWeight.Bold,
                                        color = Purple
                                    )
                                    Text(
                                        text = "Excelente trabajo hoy",
                                        style = MaterialTheme.typography.bodySmall,
                                        color = TextSecondary
                                    )
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = Constants.Dimens.PaddingLarge,
                            end = Constants.Dimens.PaddingLarge,
                            bottom = 100.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingItems)
                    ) {
                        items(
                            items = filteredHabits,
                            key = { it.id }
                        ) { habit ->
                            HabitItem(
                                habit = habit,
                                isCompleted = habit.id in uiState.completedHabitIds,
                                onComplete = { viewModel.completeHabit(habit.id) },
                                onDelete = { viewModel.deleteHabit(habit) },
                                onEdit = { onNavigateToEdit(habit.id) },
                                onDetail = { onNavigateToDetail(habit.id) }
                            )
                        }
                    }
                }
            }
        }
    }
}

private fun getGreeting(context: android.content.Context): String {
    val hour = java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)
    return when (hour) {
        in 6..11 -> context.getString(R.string.greeting_morning)
        in 12..17 -> context.getString(R.string.greeting_afternoon)
        in 18..21 -> context.getString(R.string.greeting_evening)
        else -> context.getString(R.string.greeting_night)
    }
}

private fun getTodayDate(): String {
    val format = SimpleDateFormat("EEEE, d 'de' MMMM", Locale("es", "ES"))
    return format.format(Date()).replaceFirstChar { it.uppercase() }
}