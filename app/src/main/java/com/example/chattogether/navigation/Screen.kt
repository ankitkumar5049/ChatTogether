package com.example.chattogether.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Help
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String) {
     object Login: Screen("login_screen")
     object Main: Screen("main_screen")
     object SignUp: Screen("signup_screen")
     object Profile: Screen("profile_screen")
     object Help: Screen("help_screen")
     object Home: Screen("home_screen")
     object Chats : Screen("chat_screen/{currentUserId}/{otherUserId}")
}

sealed class BottomNavItem(val route: String, val title: String, val icon: ImageVector) {
     object Home : BottomNavItem("home_screen", "Home", Icons.Default.Home)
     object ChatsScreen : BottomNavItem("main_screen", "Chats", Icons.Default.Chat)
     object Profile : BottomNavItem("profile_screen", "Profile", Icons.Default.Person)
     object Help : BottomNavItem("help_screen", "Help", Icons.Default.Help)
}


