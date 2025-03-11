package com.example.chattogether.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.screens.ChatScreen
import com.example.chattogether.screens.Dashboard
import com.example.chattogether.screens.HelpScreen
import com.example.chattogether.screens.HomeScreen
import com.example.chattogether.screens.LoginScreen
import com.example.chattogether.screens.ProfileScreen
import com.example.chattogether.screens.SignUpScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()
    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route

    val hideBottomBarRoutes = listOf(Screen.Login.route, Screen.SignUp.route)

    Scaffold(
        bottomBar = {
            if (currentRoute !in hideBottomBarRoutes) {
                BottomNavigationBar(navController)
            }
        }
    ) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(navController = navController, startDestination = Screen.Login.route) {

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
                composable(route = Screen.Profile.route) {
                    ProfileScreen(onLoginClick = {
                        navController.navigate(Screen.Login.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                            launchSingleTop = true
                        }
                    })
                }
                composable(route = Screen.Help.route) {
                    HelpScreen()
                }

            }
        }
    }
}
