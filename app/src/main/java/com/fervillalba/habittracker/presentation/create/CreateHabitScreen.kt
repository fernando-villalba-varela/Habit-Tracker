package com.fervillalba.habittracker.presentation.create

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.HabitFrequency

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateHabitScreen(
    onNavigateBack: () -> Unit,
    viewModel: CreateHabitViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(R.string.new_habit_title)) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.back_content_desc)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(Constants.Dimens.PaddingMedium)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingLarge)
        ) {
            OutlinedTextField(
                value = uiState.name,
                onValueChange = viewModel::onNameChange,
                label = { Text(stringResource(R.string.habit_name_label)) },
                placeholder = { Text(stringResource(R.string.habit_name_placeholder)) },
                isError = uiState.nameError != null,
                supportingText = {
                    uiState.nameError?.let { Text(it.asString()) }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )

            Text(
                text = stringResource(R.string.emoji_label),
                style = MaterialTheme.typography.labelLarge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall)) {
                Constants.EMOJI_OPTIONS.forEach { emoji ->
                    FilterChip(
                        selected = uiState.iconEmoji == emoji,
                        onClick = { viewModel.onEmojiChange(emoji) },
                        label = { Text(emoji) }
                    )
                }
            }

            Text(
                text = stringResource(R.string.frequency_label),
                style = MaterialTheme.typography.labelLarge
            )

            Row(horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingSmall)) {
                HabitFrequency.entries.forEach { frequency ->
                    FilterChip(
                        selected = uiState.frequency == frequency,
                        onClick = { viewModel.onFrequencyChange(frequency) },
                        label = {
                            Text(
                                when (frequency) {
                                    HabitFrequency.DAILY -> stringResource(R.string.frequency_daily)
                                    HabitFrequency.WEEKDAYS -> stringResource(R.string.frequency_weekdays)
                                    HabitFrequency.WEEKENDS -> stringResource(R.string.frequency_weekends)
                                }
                            )
                        }
                    )
                }
            }

            Button(
                onClick = { viewModel.createHabit(onNavigateBack) },
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Text(stringResource(R.string.create_habit_button))
            }
        }
    }
}