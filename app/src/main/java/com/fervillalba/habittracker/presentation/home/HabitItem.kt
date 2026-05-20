package com.fervillalba.habittracker.presentation.home

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fervillalba.habittracker.Constants
import com.fervillalba.habittracker.R
import com.fervillalba.habittracker.domain.model.Habit
import com.fervillalba.habittracker.ui.theme.Border
import com.fervillalba.habittracker.ui.theme.Purple
import com.fervillalba.habittracker.ui.theme.PurpleDark
import com.fervillalba.habittracker.ui.theme.Surface as AppSurface
import com.fervillalba.habittracker.ui.theme.TextSecondary
import com.fervillalba.habittracker.ui.theme.TextTertiary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitItem(
    habit: Habit,
    isCompleted: Boolean,
    onComplete: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dismissState = rememberSwipeToDismissBoxState()

    LaunchedEffect(dismissState.currentValue) {
        if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
            onDelete()
        }
    }

    val checkScale by animateFloatAsState(
        targetValue = if (isCompleted) 1.2f else 1f,
        animationSpec = tween(200),
        label = "check_scale"
    )

    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = when (dismissState.targetValue) {
                    SwipeToDismissBoxValue.EndToStart -> Color(0xFFFF4444)
                    else -> Color.Transparent
                },
                label = "swipe_color"
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(Constants.Dimens.RadiusLarge))
                    .background(color)
                    .padding(end = Constants.Dimens.PaddingLarge),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(R.string.delete_content_desc),
                    tint = Color.White,
                    modifier = Modifier.size(Constants.Dimens.IconSizeSmall)
                )
            }
        }
    ) {
        Surface(
            modifier = modifier.fillMaxWidth(),
            shape = RoundedCornerShape(Constants.Dimens.RadiusLarge),
            color = if (isCompleted) PurpleDark else AppSurface,
            tonalElevation = 0.dp
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (isCompleted) Modifier else Modifier.background(
                            color = Border.copy(alpha = 0.5f),
                            shape = RoundedCornerShape(Constants.Dimens.RadiusLarge)
                        )
                    )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Constants.Dimens.PaddingLarge,
                            vertical = Constants.Dimens.SpacingHeader
                        ),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(Constants.Dimens.SpacingHeader),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Constants.Dimens.BoxSizeEmojiItem)
                                .clip(RoundedCornerShape(Constants.Dimens.RadiusSmall))
                                .background(
                                    if (isCompleted)
                                        Purple.copy(alpha = 0.3f)
                                    else
                                        Color.White.copy(alpha = 0.05f)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = habit.iconEmoji,
                                fontSize = 22.sp
                            )
                        }
                        Text(
                            text = habit.name,
                            style = MaterialTheme.typography.bodyLarge.copy(
                                fontWeight = if (isCompleted) FontWeight.Normal else FontWeight.Medium,
                                textDecoration = if (isCompleted) TextDecoration.LineThrough else TextDecoration.None
                            ),
                            color = if (isCompleted) TextSecondary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = onComplete,
                        enabled = !isCompleted,
                        modifier = Modifier.scale(checkScale)
                    ) {
                        Icon(
                            imageVector = if (isCompleted)
                                Icons.Filled.CheckCircle
                            else
                                Icons.Outlined.CheckCircle,
                            contentDescription = stringResource(R.string.complete_habit_content_desc),
                            modifier = Modifier.size(Constants.Dimens.IconSizeCheck),
                            tint = if (isCompleted) Purple else TextTertiary
                        )
                    }
                }

                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(start = Constants.Dimens.SpacingMedium, top = 0.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(Constants.Dimens.DotSize)
                                .clip(CircleShape)
                                .background(Purple)
                        )
                    }
                }
            }
        }
    }
}