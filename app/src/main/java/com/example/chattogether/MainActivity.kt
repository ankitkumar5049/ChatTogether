package com.example.chattogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.navigation.Navigation
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
            Navigation()
        }
    }
}
