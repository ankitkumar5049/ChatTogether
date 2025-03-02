package com.example.chattogether.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chattogether.viewmodel.ChatViewModel
import com.google.android.gms.fido.fido2.api.common.Attachment
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChatScreen(navController: NavController?, userId: String, otherUserId: String, viewModel: ChatViewModel = viewModel()) {
    val db = FirebaseFirestore.getInstance()

    var chatId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var otherUserName by remember { mutableStateOf(otherUserId) }
    var selectedFileUri by remember { mutableStateOf<String?>(null) }
    var fileType by remember { mutableStateOf<String?>(null) }

    val context = LocalContext.current
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedFileUri = it.toString()
            fileType = if (it.toString().contains("image")) "image" else "document"
            viewModel.uploadFileToFirebaseStorage(context, uri) { downloadUrl ->
                viewModel.sendMessage(db, chatId!!, userId, otherUserId, null, downloadUrl, fileType)
            }
        }
    }

    LaunchedEffect(otherUserId) {
        db.collection("users").document(otherUserId).get()
            .addOnSuccessListener { document ->
                if (document.exists()) {
                    otherUserName = document.getString("name") ?: ""
                }
            }
    }

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

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState())
        ) {
            messages.forEach { msg ->
                val senderId = msg["senderId"] as? String ?: ""
                val text = msg["message"] as? String
                val fileUrl = msg["fileUrl"] as? String
                val type = msg["fileType"] as? String
                ChatBubble(text, fileUrl, type, senderId == userId)
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = message,
                onValueChange = { message = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("Type a message...") },
                trailingIcon = {
                    IconButton(onClick = { launcher.launch("*/*") }) {
                        Icon(imageVector = Icons.Default.Attachment, contentDescription = "Attach File")
                    }
                }
            )

//            IconButton(onClick = { launcher.launch("*/*") }) {
//                Icon(imageVector = Icons.Default.Add, contentDescription = "Attach File")
//            }

            Button(onClick = {
                if (message.text.isNotBlank() && chatId != null) {
                    viewModel.sendMessage(db, chatId!!, userId, otherUserId, message.text, null, null)
                    message = TextFieldValue("")
                }
            }) {
                Text("Send")
            }
        }
    }
}

@Composable
fun ChatBubble(message: String?, fileUrl: String?, fileType: String?, isSentByUser: Boolean) {
    val context = LocalContext.current
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
            Column {
                if (!message.isNullOrEmpty()) {
                    Text(text = message, color = Color.White)
                }
                if (!fileUrl.isNullOrEmpty()) {
                    if (fileType == "image") {
                        AsyncImage(
                            model = fileUrl,
                            contentDescription = "Sent Image",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    } else {
                        Text(
                            text = "📄 Document",
                            color = Color.Yellow,
                            fontSize = 14.sp,
                            modifier = Modifier.clickable {
                                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fileUrl))
                                startActivity(context, intent, null)
                            }
                        )
                    }
                }
            }
        }
    }
}




@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatScreen(navController = null,"","")
}
