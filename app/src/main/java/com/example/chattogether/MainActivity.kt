package com.example.chattogether

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.chattogether.navigation.Navigation
import com.example.chattogether.screens.Dashboard
import com.example.chattogether.navigation.Screen
import com.example.chattogether.screens.ChatScreen
import com.example.chattogether.screens.LoginScreen
import com.example.chattogether.screens.SignUpScreen
import com.example.chattogether.ui.theme.ChatTogetherTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ChatTogetherTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Navigation() // Apply theme to the entire app
                }
            }
        }
    }
}
