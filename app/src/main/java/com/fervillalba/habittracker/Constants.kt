package com.fervillalba.habittracker

import androidx.compose.ui.unit.dp

object Constants {
    // Database
    const val DATABASE_NAME = "habit_tracker_db"
    const val DATABASE_VERSION = 1
    const val TABLE_HABITS = "habits"
    const val TABLE_HABIT_LOGS = "habit_logs"

    // Routes
    const val ROUTE_HOME = "home"
    const val ROUTE_CREATE_HABIT = "create_habit"

    // UI Dimensions
    object Dimens {
        val PaddingSmall = 8.dp
        val PaddingMedium = 16.dp
        val SpacingSmall = 8.dp
        val SpacingMedium = 12.dp
        val SpacingLarge = 16.dp
        val IconSizeMedium = 32.dp
    }
    
    // Default Values
    const val DEFAULT_EMOJI = "✅"
    const val DEFAULT_COLOR_HEX = "#7F77DD"
    val EMOJI_OPTIONS = listOf("✅", "🏃", "📚", "💧", "🎯", "💪", "🧘", "🍎")
}