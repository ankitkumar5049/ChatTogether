package com.example.chattogether.navigation

sealed class Screen(val route: String) {
     object Login: Screen("login_screen")
     object Main: Screen("main_screen")
     object SignUp: Screen("signup_screen")
     object Chats : Screen("chat_screen/{currentUserId}/{otherUserId}")
}

