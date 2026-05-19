package com.fervillalba.habittracker.presentation.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.Habit

@Composable
fun HabitItem(
    habit: Habit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(Constants.Dimens.PaddingMedium),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingMedium),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.iconEmoji,
                    style = MaterialTheme.typography.headlineMedium
                )
                Text(
                    text = habit.name,
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            IconButton(onClick = onComplete) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = stringResource(R.string.complete_habit_content_desc),
                    modifier = Modifier.size(Constants.Dimens.IconSizeMedium),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}