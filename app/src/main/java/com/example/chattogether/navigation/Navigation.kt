package com.example.chattogether.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.screens.ChatScreen
import com.example.chattogether.screens.Dashboard
import com.example.chattogether.screens.LoginScreen
import com.example.chattogether.screens.SignUpScreen


@Composable
fun Navigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Screen.Login.route) {

        composable(route = Screen.Login.route) {
            LoginScreen(
                navController = navController,
                onLoginSuccess = {
                    navController.navigate(Screen.Main.route) {
                        popUpTo(Screen.Login.route) { inclusive = true } // Removes Login from back stack
                        launchSingleTop = true // Prevents duplicate instances
                    }
                                 },
                onSignUpClick = {
                    navController.navigate(Screen.SignUp.route){
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
                    navController.navigate(Screen.Login.route)
                    {
                        popUpTo(Screen.Login.route) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(route = Screen.Main.route) {
            Dashboard(
                navController = navController
            )
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


    }
}