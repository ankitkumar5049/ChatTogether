package com.example.chattogether.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.chattogether.viewmodel.ChatViewModel
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatScreen(navController: NavController?, userId: String, otherUserId: String, viewModel: ChatViewModel= viewModel()) {
    val db = FirebaseFirestore.getInstance()

    var chatId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var otherUserName by remember { mutableStateOf(otherUserId) }

    // Fetch user name from Firestore
    LaunchedEffect(otherUserId) {
        db.collection("users").document(otherUserId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    otherUserName = document.getString("name") ?: ""
                }
            }
    }
    // Fetch or create chat room
    LaunchedEffect(Unit) {
        viewModel.getOrCreateChatRoom(db, userId, otherUserId) { id ->
            chatId = id
            if (id.isNotBlank()) {
                viewModel.listenForMessages(db, id) { newMessages ->
                    messages = newMessages
                }
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp)
    ) {
        Text(
            text = "Chat Screen",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.Start)
                .padding(top = 15.dp, bottom = 10.dp),
        )

        Text(
            text = "Chatting with $otherUserName",
            style = MaterialTheme.typography.titleMedium,
            fontSize = 18.sp,
            color = Color.Gray,
            modifier = Modifier.padding(bottom = 10.dp)
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            messages.forEach { msg ->
                val senderId = msg["senderId"] as? String ?: ""
                val text = msg["message"] as? String ?: ""
                ChatBubble(message = text, isSentByUser = senderId == userId)
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Button(onClick = {
                if (message.text.isNotBlank() && chatId != null) {
                    viewModel.sendMessage(db, chatId!!, userId, otherUserId, message.text)
                    message = TextFieldValue("")
                }
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: String, isSentByUser: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .wrapContentSize(if (isSentByUser) Alignment.CenterEnd else Alignment.CenterStart)
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = if (isSentByUser) Color.Blue else Color.Gray,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(10.dp)
        ) {
            Text(text = message, color = Color.White)
        }
    }
}



@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(navController = null,"","")
}
