package com.example.chattogether.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
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
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""
    var email by remember { mutableStateOf("") }

    var chatUsers by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // (UserId, Name)
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        getUserChats(db, currentUserId) { chatRooms ->
            if (chatRooms.isNotEmpty()) {
                val usersList = chatRooms.mapNotNull { chat ->
                    val user1 = chat["user1"] as? String
                    val user2 = chat["user2"] as? String
                    val otherUserId = if (user1 == currentUserId) user2 else user1

                    if (!otherUserId.isNullOrBlank()) {
                        otherUserId to (chat["userName"] as? String ?: "Unknown User")
                    } else {
                        null
                    }
                }
                chatUsers = usersList
            } else {
                Log.d("Dashboard", "No chats found")
            }
            isLoading = false
        }

    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(text = "Chats", style = MaterialTheme.typography.headlineMedium)


        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter email to search") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier.weight(1f).padding(5.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.width(100.dp),
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


        if (isLoading) {
            CircularProgressIndicator()
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(chatUsers) { (userId, userName) ->
                    ChatListItem(userName) {
                        val chatRoute = Screen.Chats.route
                            .replace("{currentUserId}", currentUserId)
                            .replace("{otherUserId}", userId)

                        navController?.navigate(chatRoute)
                    }
                }
            }
        }
    }
}

/**
 * Chat list item UI component.
 */
@Composable
fun ChatListItem(userName: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Placeholder for user avatar
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            )

            Spacer(modifier = Modifier.width(16.dp))

            Text(text = userName, style = MaterialTheme.typography.bodyLarge)
        }
    }
}

fun getUserChats(db: FirebaseFirestore, userId: String, onResult: (List<Map<String, Any>>) -> Unit) {
    val chatRooms = mutableListOf<Map<String, Any>>()

    db.collection("chats")
        .whereEqualTo("user1", userId)
        .get()
        .addOnSuccessListener { querySnapshot1 ->
            querySnapshot1.documents.mapNotNullTo(chatRooms) { it.data }

            db.collection("chats")
                .whereEqualTo("user2", userId)
                .get()
                .addOnSuccessListener { querySnapshot2 ->
                    querySnapshot2.documents.mapNotNullTo(chatRooms) { it.data }
                    onResult(chatRooms)
                }
                .addOnFailureListener {
                    Log.e("getUserChats", "Error fetching user2 chats", it)
                    onResult(chatRooms)
                }
        }
        .addOnFailureListener {
            Log.e("getUserChats", "Error fetching user1 chats", it)
            onResult(emptyList())
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