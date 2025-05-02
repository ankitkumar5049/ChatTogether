package com.example.chattogether.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Attachment
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.chattogether.utils.AppSession
import com.example.chattogether.viewmodel.ChatViewModel
import com.google.firebase.firestore.FirebaseFirestore
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Scaffold
import com.example.chattogether.components.CustomTopBar
import com.example.chattogether.utils.AESHelper

@Composable
fun ChatScreen(navController: NavController?, userId: String, otherUserId: String, viewModel: ChatViewModel = viewModel()) {
    val db = FirebaseFirestore.getInstance()

    var chatId by remember { mutableStateOf<String?>(null) }
    var message by remember { mutableStateOf(TextFieldValue("")) }
    var messages by remember { mutableStateOf<List<Map<String, Any>>>(emptyList()) }
    var otherUserName by remember { mutableStateOf(otherUserId) }
    var selectedFileUri by remember { mutableStateOf<String?>(null) }
    var fileType by remember { mutableStateOf<String?>(null) }
    val listState = rememberLazyListState()

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

    LaunchedEffect(messages) {
        listState.animateScrollToItem(0)
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

    Scaffold(
        topBar =
        {
            CustomTopBar(
                text = "Let's Chat",
                onMenuClick = {}
            ) {

            }

        },
    ) { paddingValues ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(10.dp)
        ) {

            Text(
                text = "Chatting with $otherUserName",
                style = MaterialTheme.typography.titleMedium,
                fontSize = 18.sp,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 10.dp)
            )

            LazyColumn(
                state = listState,
                reverseLayout = true,
                modifier = Modifier.weight(1f)
            ) {
                items(messages.reversed()) { msg -> // Reverse to maintain order
                    val senderId = msg["senderId"] as? String ?: ""
                    val text = msg["message"] as? String
                    val fileUrl = msg["fileUrl"] as? String
                    val type = msg["fileType"] as? String
                    val time = msg["formattedTime"] as? String ?: ""
                    val decryptedMsg = AESHelper.decrypt(text!!)
                    ChatBubble(decryptedMsg, fileUrl, type, senderId == userId, time)
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
                            Icon(
                                imageVector = Icons.Default.Attachment,
                                contentDescription = "Attach File"
                            )
                        }
                    }
                )

                Spacer(modifier = Modifier.width(8.dp))

                Button(onClick = {
                    if (message.text.isNotBlank() && chatId != null) {
                        val encryptText = AESHelper.encrypt(message.text)
                        viewModel.sendMessage(
                            db,
                            chatId!!,
                            userId,
                            otherUserId,
                            encryptText,
                            null,
                            null
                        )
                        message = TextFieldValue("")
                    }
                }) {
                    Text("Send")
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: String?,
    fileUrl: String?,
    fileType: String?,
    isSentByUser: Boolean,
    timestamp: String // Pass timestamp as a parameter
) {
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(5.dp)
            .wrapContentSize(if (isSentByUser) Alignment.CenterEnd else Alignment.CenterStart)
    ) {
        Column(
            horizontalAlignment = if (isSentByUser) Alignment.End else Alignment.Start
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

            // Timestamp
            Text(
                text = timestamp,
                fontSize = 12.sp,
                color = Color.LightGray,
                modifier = Modifier.padding(top = 4.dp, start = 8.dp, end = 8.dp)
            )
        }
    }
}





@Preview(showBackground = true)
@Composable
fun ChatScreenPreview() {
    ChatBubble("Hii","","", true, "12:12 Am")
}
