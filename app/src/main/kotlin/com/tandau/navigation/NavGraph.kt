package com.tandau.navigation

import androidx.compose.runtime.*
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.tandau.data.repository.UserRepository
import com.tandau.ui.screens.HomeScreen
import com.tandau.ui.screens.LoginScreen
import com.tandau.ui.screens.OnboardingScreen

sealed class Screen(val route: String) {
    object Splash : Screen("splash")
    object Login : Screen("login")
    object Onboarding : Screen("onboarding")
    object Home : Screen("home")
}

@Composable
fun TandauNavGraph(navController: NavHostController, userRepository: UserRepository) {
    var startDestination by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        val session = userRepository.getSession()
        startDestination = if (session != null) Screen.Home.route else Screen.Login.route
    }

    if (startDestination != null) {
        NavHost(navController = navController, startDestination = startDestination!!) {
            composable(Screen.Login.route) {
                LoginScreen(navController, userRepository)
            }
            composable(Screen.Onboarding.route) {
                OnboardingScreen(navController, userRepository)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController, userRepository)
            }
        }
    }
}
