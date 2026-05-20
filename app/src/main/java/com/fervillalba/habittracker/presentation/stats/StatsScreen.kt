package com.fervillalba.habittracker.presentation.stats

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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.fervillalba.habittracker.ui.theme.Background
import com.fervillalba.habittracker.ui.theme.Purple
import com.fervillalba.habittracker.ui.theme.PurpleDark
import com.fervillalba.habittracker.ui.theme.Surface
import com.fervillalba.habittracker.ui.theme.TextSecondary
import com.fervillalba.habittracker.ui.theme.TextTertiary
import kotlin.math.roundToInt

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatsScreen(
    onNavigateBack: () -> Unit,
    viewModel: StatsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        stringResource(R.string.stats_content_desc),
                        fontWeight = FontWeight.Medium
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
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Background
                )
            )
        },
        containerColor = Background
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
            uiState.stats.isEmpty() -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall)
                    ) {
                        Text("📊", fontSize = 48.sp)
                        Text(
                            text = stringResource(R.string.no_stats_yet),
                            style = MaterialTheme.typography.titleMedium,
                            color = TextSecondary
                        )
                        Text(
                            text = stringResource(R.string.complete_habits_to_see_stats),
                            style = MaterialTheme.typography.bodyMedium,
                            color = TextTertiary
                        )
                    }
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .padding(horizontal = Constants.Dimens.PaddingMedium),
                    verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium)
                ) {
                    item { }
                    items(uiState.stats) { habitStats ->
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                                .background(Surface)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(Constants.Dimens.PaddingLarge),
                                verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingLarge)
                            ) {
                                Row(
                                    horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(Constants.Dimens.IconSizeLarge)
                                            .clip(RoundedCornerShape(Constants.Dimens.RadiusSmall))
                                            .background(PurpleDark),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = habitStats.habit.iconEmoji,
                                            fontSize = 24.sp
                                        )
                                    }
                                    Column {
                                        Text(
                                            text = habitStats.habit.name,
                                            style = MaterialTheme.typography.titleMedium,
                                            fontWeight = FontWeight.SemiBold
                                        )
                                        Text(
                                            text = stringResource(
                                                R.string.times_completed_format,
                                                habitStats.totalCompleted
                                            ),
                                            style = MaterialTheme.typography.bodySmall,
                                            color = TextTertiary
                                        )
                                    }
                                }

                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall)
                                ) {
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        emoji = "🔥",
                                        value = "${habitStats.currentStreak}",
                                        label = stringResource(R.string.current_streak)
                                    )
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        emoji = "⭐",
                                        value = "${habitStats.bestStreak}",
                                        label = stringResource(R.string.best_streak)
                                    )
                                    StatCard(
                                        modifier = Modifier.weight(1f),
                                        emoji = "📈",
                                        value = "${(habitStats.completionRate * 100).roundToInt()}%",
                                        label = stringResource(R.string.last_30_days_short)
                                    )
                                }

                                Column(
                                    verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall)
                                ) {
                                    Text(
                                        text = stringResource(R.string.last_30_days_label),
                                        style = MaterialTheme.typography.labelMedium,
                                        color = TextTertiary
                                    )
                                    HabitHeatmap(
                                        logs = habitStats.last30DaysLogs
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
private fun StatCard(
    emoji: String,
    value: String,
    label: String,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(Constants.Dimens.RadiusSmall))
            .background(PurpleDark)
            .padding(vertical = Constants.Dimens.SpacingMedium, horizontal = Constants.Dimens.PaddingSmall),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingExtraSmall)
        ) {
            Text(text = emoji, fontSize = 20.sp)
            Text(
                text = value,
                style = MaterialTheme.typography.titleLarge,
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