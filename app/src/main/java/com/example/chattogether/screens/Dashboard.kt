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
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chattogether.navigation.Screen
import com.example.chattogether.viewmodel.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Dashboard(navController: NavController?, viewModel: DashboardViewModel = viewModel()) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""
    var email by remember { mutableStateOf("") }

    var chatUsers by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // (UserId, Name)
    var isLoading by remember { mutableStateOf(true) }

    LaunchedEffect(Unit) {
        viewModel.getUserChats(db, currentUserId) { chatRooms ->
            if (chatRooms.isNotEmpty()) {
                val usersList = mutableListOf<Pair<String, String>>()

                chatRooms.forEach { chat ->
                    val user1 = chat["user1"] as? String
                    val user2 = chat["user2"] as? String
                    val otherUserId = if (user1 == currentUserId) user2 else user1

                    if (!otherUserId.isNullOrBlank()) {
                        db.collection("users").document(otherUserId).get()
                            .addOnSuccessListener { document ->
                                val userName = document.getString("name") ?: "Unknown User"
                                usersList.add(otherUserId to userName)
                                chatUsers = usersList.toList()
                            }
                            .addOnFailureListener {
                                Log.e("Dashboard", "Error fetching user name", it)
                            }
                    }
                }
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
        verticalArrangement = Arrangement.Top
    ) {
        Text(text = "Chats",
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(top = 15.dp),
            style = MaterialTheme.typography.headlineMedium
        )

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter email to search") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.width(100.dp),
                onClick = {
                    if (email.isNotEmpty()) {
                        isLoading = true
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        viewModel.searchUserByEmail(db, email, navController!!, currentUserId)

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




@Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    MaterialTheme {
        Dashboard(navController = null)
    }
}