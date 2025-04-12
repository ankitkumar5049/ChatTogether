package com.example.chattogether.navigation

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.screens.ChatScreen
import com.example.chattogether.screens.Dashboard
import com.example.chattogether.screens.HelpScreen
import com.example.chattogether.screens.LoginScreen
import com.example.chattogether.screens.ProfileScreen
import com.example.chattogether.screens.SignUpScreen
import com.example.chattogether.screens.HomeScreen
import com.example.chattogether.screens.SettingsScreen
import com.google.firebase.auth.FirebaseAuth
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.example.chattogether.screens.ChangePasswordScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(Screen.Login.route, Screen.SignUp.route)
    val auth = FirebaseAuth.getInstance()
    val isSystemDarkMode = isSystemInDarkTheme()
    var isDarkTheme by remember { mutableStateOf(isSystemDarkMode) }
    val startDestination = if (auth.currentUser != null) Screen.Main.route else Screen.Login.route

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = startDestination) {

                composable(route = Screen.Login.route) {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = {
                            navController.navigate(Screen.Main.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        },
                        onSignUpClick = {
                            navController.navigate(Screen.SignUp.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(route = Screen.SignUp.route) {
                    SignUpScreen(
                        navController = navController,
                        onLoginClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(route = Screen.Main.route) {
                    Dashboard(navController = navController)
                }

                composable(route = Screen.Chats.route) { backStackEntry ->
                    val currentUserId = backStackEntry.arguments?.getString("currentUserId") ?: ""
                    val otherUserId = backStackEntry.arguments?.getString("otherUserId") ?: ""

                    ChatScreen(
                        navController = navController,
                        userId = currentUserId,
                        otherUserId = otherUserId
                    )
                }
                composable(route = Screen.Home.route) {
                    HomeScreen()
                }
                composable("profile_screen") {
                    ProfileScreen(
                        navController = navController,
                        editing = false,
                    )
                }
                composable(route = Screen.Help.route) {
                    HelpScreen(navController)
                }

                composable(route = Screen.Setting.route) {
                    SettingsScreen(
                        navController = navController,
                        onThemeChange = {},
                        onLoginClick = {
                            navController.navigate(Screen.Login.route) {
                                popUpTo(Screen.Login.route) { inclusive = true }
                                launchSingleTop = true
                            }
                        }
                    )
                }

                composable(route = Screen.Profile.route,
                    arguments = listOf(navArgument("isEditing") { type = NavType.BoolType })) {
                        backStackEntry ->
                    val isEditing = backStackEntry.arguments?.getBoolean("isEditing") ?: false

                    ProfileScreen(
                        navController = navController,
                        editing = isEditing,
                    )
                }

                composable(Screen.ChangePassword.route) {
                    ChangePasswordScreen(navController)
                }


            }
        }
    }
}
