package com.fervillalba.habittracker.presentation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.fervillalba.habittracker.presentation.create.CreateHabitScreen
import com.fervillalba.habittracker.presentation.home.HomeScreen
import com.fervillalba.habittracker.presentation.stats.StatsScreen

sealed class Screen(val route: String) {
    object Home : Screen("home")
    object CreateHabit : Screen("create_habit")
    object Stats : Screen("stats")
}

@Composable
fun NavGraph(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onNavigateToCreate = {
                    navController.navigate(Screen.CreateHabit.route)
                },
                onNavigateToStats = {
                    navController.navigate(Screen.Stats.route)
                }
            )
        }
        composable(Screen.CreateHabit.route) {
            CreateHabitScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
        composable(Screen.Stats.route) {
            StatsScreen()
        }
    }
}