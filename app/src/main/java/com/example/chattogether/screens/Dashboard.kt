package com.example.chattogether.screens

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController

@Composable
fun Dashboard(navController: NavController?){


}

@Preview(showBackground = true)
@Composable
fun Dashboard() {
    MaterialTheme {
        Dashboard(navController = null)
    }
}
