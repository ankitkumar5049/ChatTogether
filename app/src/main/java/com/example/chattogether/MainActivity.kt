package com.example.chattogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.screens.Dashboard
import com.example.grocio.navigation.Screen
import com.example.grocio.screens.LoginScreen
import com.example.grocio.screens.SignUpScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val navController = rememberNavController()

//            NavHost(
//                navController = navController,
//                startDestination = Screen.Login.route
//            ) {
//                composable(Screen.Login.route) { LoginScreen(navController) }
//                composable(Screen.Main.route) { Dashboard(navController) }
//            }

            NavHost(
                navController = navController,
                startDestination = Screen.Login.route
            ) {
                composable(Screen.Login.route) {
                    LoginScreen(
                        navController = navController,
                        onLoginSuccess = { navController.navigate(Screen.Main.route) },
                        onSignUpClick = { navController.navigate(Screen.SignUp.route) }
                    )
                }
                composable(Screen.SignUp.route) {
                    SignUpScreen(navController = navController)
                }
                composable(Screen.Main.route) {
                    Dashboard(navController)
                }
            }
        }
    }
}
