package com.example.chattogether.screens

import android.widget.Toast
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.chattogether.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Dashboard(navController: NavController?) {
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val database = FirebaseDatabase.getInstance().reference.child("users") // Adjust based on your DB structure

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Enter email to search") },
            keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                if (email.isNotEmpty()) {
                    isLoading = true
                    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                    searchUserByEmail(db, email, navController!!, currentUserId)

                } else {
                    Toast.makeText(context, "Enter an email", Toast.LENGTH_SHORT).show()
                }
            },
            enabled = !isLoading
        ) {
            Text(text = if (isLoading) "Searching..." else "Search")
        }
    }
}

fun searchUserByEmail(
    db: FirebaseFirestore,
    email: String,
    navController: NavController,
    currentUserId: String
) {
    db.collection("users")
        .whereEqualTo("email", email)
        .get()
        .addOnSuccessListener { documents ->
            if (!documents.isEmpty) {
                for (document in documents) {
                    val otherUserId = document.id
                    println("User found: $otherUserId")

                    // Navigate to ChatScreen with userId
                    val chatRoute = Screen.Chats.route
                        .replace("{currentUserId}", currentUserId)
                        .replace("{otherUserId}", otherUserId)

                    navController.navigate(chatRoute)
                    return@addOnSuccessListener
                }
            } else {
                println("No user found")
            }
        }
        .addOnFailureListener { exception ->
            println("Error getting user: ${exception.message}")
        }
}



@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        Dashboard(navController = null)
    }
}