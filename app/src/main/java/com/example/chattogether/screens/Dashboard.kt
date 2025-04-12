package com.example.chattogether.screens

import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chattogether.components.LoaderOverlay
import com.example.chattogether.navigation.Screen
import com.example.chattogether.viewmodel.DashboardViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun Dashboard(navController: NavController?, viewModel: DashboardViewModel = viewModel()) {
    val context = LocalContext.current
    val db = FirebaseFirestore.getInstance()
    val auth = FirebaseAuth.getInstance()
    val currentUserId = auth.currentUser?.uid ?: ""
    var username by remember { mutableStateOf("") }

    var chatUsers by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } // (UserId, Name)
    var isLoading by remember { mutableStateOf(true) }
    var isLocalDataChecked by remember { mutableStateOf(false) }
    var filteredUsers by remember { mutableStateOf(emptyList<Pair<String, String>>()) }

    LaunchedEffect(Unit) {
        viewModel.getChatUsersFromLocal(currentUserId) { localChats ->
            chatUsers = localChats.map { it.userId to it.userName }
            isLocalDataChecked = true
            Log.d("TAG", "Dashboard chat user: $chatUsers")
        }
    }

    fun deleteChat(userId: String) {
        viewModel.deleteChat(db, currentUserId, userId) {
            chatUsers = chatUsers.filterNot { it.first == userId }
            Toast.makeText(context, "Chat deleted", Toast.LENGTH_SHORT).show()
        }
    }

        LaunchedEffect(isLocalDataChecked) {
            if (isLocalDataChecked && chatUsers.isEmpty()) {
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

                                        // Update chat users and cache them locally
                                        chatUsers = usersList.toList()
                                        filteredUsers = chatUsers
                                        viewModel.saveChatUsersToLocal(currentUserId, chatUsers)

                                        Log.d("TAG", "Fetched from Firebase: $chatUsers")
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
            } else {
                isLoading = false
                filteredUsers = chatUsers
            }
        }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
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
            LoaderOverlay(isLoading = isLoading)
            OutlinedTextField(
                value = username,
                onValueChange = { username = it
                    filteredUsers = if (username.isNotEmpty()) {
                        chatUsers.filter { user ->
                            user.second.contains(username, ignoreCase = true)
                        }
                    } else {
                        chatUsers
                    } },
                label = { Text("Enter username to search") },
                keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Email),
                modifier = Modifier
                    .weight(1f)
                    .padding(5.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                modifier = Modifier.width(100.dp),
                onClick = {
                    if (username.isNotEmpty()) {
                        isLoading = true
                        val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
                        viewModel.searchUserByEmail(db, username, navController!!, currentUserId){ message ->
                            isLoading = false
                            if(message.isNotEmpty()){
                                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                            }
                        }

                    } else {
                        Toast.makeText(context, "Enter an username", Toast.LENGTH_SHORT).show()
                    }
                },
                enabled = !isLoading
            ) {
                Text(text ="Search")
            }
        }


            LazyColumn(modifier = Modifier.fillMaxSize()) {
                items(filteredUsers, key = { it.first }) { (userId, userName) ->
                    ChatListItem(
                        userName = userName,
                        onClick = {
                            val chatRoute = Screen.Chats.route
                                .replace("{currentUserId}", currentUserId)
                                .replace("{otherUserId}", userId)
                            navController?.navigate(chatRoute)
                        },
                        onLongPress = { deleteChat(userId) }
                    )
                }
            }

    }
}

/**
 * Chat list item UI component.
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ChatListItem(userName: String, onClick: () -> Unit, onLongPress: () -> Unit) {
    var showDialog by remember { mutableStateOf(false) }

    if (showDialog) {
        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text("Delete Chat") },
            text = { Text("Are you sure you want to delete this chat?") },
            confirmButton = {
                TextButton(onClick = {
                    onLongPress()
                    showDialog = false
                }) {
                    Text("Delete", color = Color.Red)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .combinedClickable(
                onClick = { onClick() },
                onLongClick = { showDialog = true }
            ),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(MaterialTheme.colorScheme.primary, shape = CircleShape)
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person",
                    tint = Color.White,
                    modifier = Modifier
                        .size(24.dp)
                        .align(Alignment.Center)
                )
            }
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