package com.example.grocio.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.screens.Dashboard
import com.example.chattogether.viewmodel.AuthViewModel
import com.example.grocio.screens.LoginScreen

@Composable
fun Navigation() {
    val navController = rememberNavController()

//    NavHost(navController = navController, startDestination = Screen.Login.route) {
//        composable(route = Screen.Login.route) {
//            LoginScreen(navController) {
//                navController.navigate(Screen.Main.route) {
//                    popUpTo(Screen.Login.route) { inclusive = true } // Remove Login from backstack
//                }
//            }
//        }
//        composable(route = Screen.Main.route) {
//            Dashboard()
//        }
//    }
    NavHost(navController = navController, startDestination = Screen.Login.route) {
        composable(route = Screen.Login.route) {
            LoginScreen(navController = navController) {
                navController.navigate(Screen.Main.route) // Navigate to Dashboard on successful login
            }
        }
        composable(route = Screen.Main.route) {
            Dashboard()
        }
    }
}