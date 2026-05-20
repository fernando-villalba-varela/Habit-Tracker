package com.fervillalba.habittracker

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

object Constants {
    // Database
    const val DATABASE_NAME = "habit_tracker_db"
    const val DATABASE_VERSION = 2
    const val TABLE_HABITS = "habits"
    const val TABLE_HABIT_LOGS = "habit_logs"

    // Routes
    const val ROUTE_HOME = "home"
    const val ROUTE_CREATE_HABIT = "create_habit"
    const val ROUTE_STATS = "stats"

    // UI Dimensions
    object Dimens {
        val PaddingSmall = 8.dp
        val PaddingMedium = 16.dp
        val PaddingLarge = 20.dp
        val SpacingExtraSmall = 4.dp
        val SpacingSmall = 8.dp
        val SpacingMedium = 12.dp
        val SpacingLarge = 16.dp
        val SpacingExtraLarge = 24.dp
        val SpacingHeader = 14.dp
        val SpacingItems = 10.dp
        
        val IconSizeSmall = 22.dp
        val IconSizeMedium = 32.dp
        val IconSizeLarge = 48.dp
        val IconSizeCheck = 28.dp
        
        val BoxSizeEmoji = 52.dp
        val BoxSizeEmojiItem = 44.dp
        val BoxSizeCheck = 48.dp
        
        val RadiusLarge = 20.dp
        val RadiusMedium = 14.dp
        val RadiusSmall = 12.dp
        val RadiusExtraSmall = 10.dp
        val RadiusIndicator = 3.dp
        
        val BorderWidth = 1.5.dp
        val ProgressHeight = 6.dp
        val DotSize = 3.dp

        val EmojiSizeItem = 24.sp
        val EmojiSizeDetail = 32.sp
    }
    
    // Animations
    object Animation {
        const val DurationShort = 150
        const val DurationMedium = 300
        const val DurationLong = 500
        
        const val ScalePressed = 0.95f
        const val ScaleChecked = 1.1f
        const val ScaleNormal = 1f
        const val ScaleCompletedEmoji = 0.9f
    }

    // Time
    const val MILLIS_IN_DAY = 24 * 60 * 60 * 1000L

    // Default Values
    const val DEFAULT_EMOJI = "✅"
    const val DEFAULT_COLOR_HEX = "#7F77DD"
    val EMOJI_OPTIONS = listOf("✅", "🏃", "🏃‍♀️", "📚", "💧", "🎯", "💪", "🧘", "🍎", "✍️", "🎵", "😴", "🥗")
}