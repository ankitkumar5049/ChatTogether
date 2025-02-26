package com.example.chattogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.screens.Dashboard
import com.example.chattogether.navigation.Screen
import com.example.chattogether.screens.ChatScreen
import com.example.chattogether.screens.LoginScreen
import com.example.chattogether.screens.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

            NavHost(
                navController = navController,
                startDestination = Screen.Login.route
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = { navController.navigate(Screen.Main.route){
                            popUpTo(Screen.Login.route) { inclusive = true }
                        } },
                        onSignUpClick = { navController.navigate(Screen.SignUp.route){
                            popUpTo(Screen.Login.route) { inclusive = true }
                        } }
                    )
                }
                composable(route = Screen.SignUp.route) {
                    SignUpScreen(
                        navController = navController,
                        onLoginClick = {navController.navigate(Screen.Login.route){
                            popUpTo(Screen.SignUp.route) { inclusive = true }
                        } }
                    )
                }
                composable(Screen.Main.route) {
                    Dashboard(navController)
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
    }
}
