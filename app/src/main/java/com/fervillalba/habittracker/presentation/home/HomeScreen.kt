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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.BarChart
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
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.R
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
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

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
                actionLabel = context.getString(R.string.undo),
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
                contentColor = Background,
                shape = CircleShape
            ) {
                Icon(
                    Icons.Default.Add,
                    contentDescription = stringResource(R.string.add_habit_content_desc)
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
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = getTodayDate(),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextTertiary
                    )
                }
                IconButton(onClick = onNavigateToStats) {
                    Icon(
                        imageVector = Icons.Default.BarChart,
                        contentDescription = stringResource(R.string.stats_content_desc),
                        tint = TextSecondary
                    )
                }
            }

            // Progress card
            if (uiState.habits.isNotEmpty()) {
                val completed = uiState.completedHabitIds.size
                val total = uiState.habits.size
                val progress = if (total > 0) completed.toFloat() / total else 0f

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Constants.Dimens.PaddingLarge)
                        .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                        .background(Surface)
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
                                    style = MaterialTheme.typography.labelMedium,
                                    color = TextTertiary
                                )
                                Text(
                                    text = stringResource(
                                        R.string.habits_completed_format,
                                        completed,
                                        total
                                    ),
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.SemiBold
                                )
                            }
                            Box(
                                modifier = Modifier
                                    .size(Constants.Dimens.BoxSizeCheck)
                                    .clip(CircleShape)
                                    .background(PurpleDark),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "${(progress * 100).toInt()}%",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Purple
                                )
                            }
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(Constants.Dimens.ProgressHeight)
                                .clip(RoundedCornerShape(Constants.Dimens.RadiusIndicator)),
                            color = Purple,
                            trackColor = PurpleDark,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }

                Spacer(modifier = Modifier.height(Constants.Dimens.SpacingLarge))
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
                            verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall)
                        ) {
                            Text("🎯", fontSize = 48.sp)
                            Text(
                                text = stringResource(R.string.no_habits_yet),
                                style = MaterialTheme.typography.titleMedium,
                                color = TextSecondary
                            )
                            Text(
                                text = stringResource(R.string.click_to_create_first),
                                style = MaterialTheme.typography.bodyMedium,
                                color = TextTertiary
                            )
                        }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(
                            start = Constants.Dimens.PaddingLarge,
                            end = Constants.Dimens.PaddingLarge,
                            bottom = 80.dp
                        ),
                        verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingItems)
                    ) {
                        items(
                            items = uiState.habits,
                            key = { it.id }
                        ) { habit ->
                            HabitItem(
                                habit = habit,
                                isCompleted = habit.id in uiState.completedHabitIds,
                                onComplete = { viewModel.completeHabit(habit.id) },
                                onDelete = { viewModel.deleteHabit(habit) }
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