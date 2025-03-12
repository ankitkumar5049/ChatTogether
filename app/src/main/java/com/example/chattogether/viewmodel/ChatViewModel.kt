package com.example.chattogether.viewmodel

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class ChatViewModel: ViewModel() {

    fun getOrCreateChatRoom(
        db: FirebaseFirestore, user1Id: String, user2Id: String,
        onResult: (String) -> Unit
    ) {
        val chatId = if (user1Id < user2Id) "$user1Id$user2Id" else "$user2Id$user1Id"

        db.collection("chats").document(chatId).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    // Create a new chat
                    val chatData = mapOf("user1" to user1Id, "user2" to user2Id)
                    db.collection("chats").document(chatId).set(chatData)
                        .addOnSuccessListener { onResult(chatId) }
                        .addOnFailureListener { onResult("") }
                } else {
                    // Chat already exists
                    onResult(chatId)
                }
            }
            .addOnFailureListener { onResult("") }
    }

    fun sendMessage(
        db: FirebaseFirestore, chatId: String, senderId: String, receiverId: String,
        message: String?, fileUrl: String?, fileType: String?
    ) {
        val messageData = mutableMapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "timestamp" to FieldValue.serverTimestamp()
        )

        // Add text message only if provided
        if (!message.isNullOrBlank()) {
            messageData["message"] = message
        }

        // Add file details if an image or doc is attached
        if (!fileUrl.isNullOrBlank()) {
            messageData["fileUrl"] = fileUrl
            messageData["fileType"] = fileType!!
        }

        db.collection("chats").document(chatId).collection("messages").add(messageData)
            .addOnSuccessListener { println("Message sent!") }
            .addOnFailureListener { println("Failed to send message!") }
    }

    fun uploadFileToFirebaseStorage(context: Context, uri: Uri, onUploadSuccess: (String) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference.child("uploads/${UUID.randomUUID()}")

        storageRef.putFile(uri)
            .addOnSuccessListener {
                storageRef.downloadUrl.addOnSuccessListener { downloadUrl ->
                    onUploadSuccess(downloadUrl.toString())
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "File upload failed", Toast.LENGTH_SHORT).show()
            }
    }


    fun listenForMessages(db: FirebaseFirestore, chatId: String, onMessageReceived: (List<Map<String, Any>>) -> Unit) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error: ${error.message}")
                    return@addSnapshotListener
                }

//                val messages = snapshot?.documents?.map { it.data!! } ?: emptyList()
                val messages = snapshot?.documents?.map { document ->
                    val data = document.data ?: emptyMap()
                    val timestamp = data["timestamp"] as? com.google.firebase.Timestamp
                    val formattedTime = timestamp?.toDate()?.let { formatTimestamp(it) } ?: "Unknown"

                    data + ("formattedTime" to formattedTime) // Add formatted timestamp to map
                } ?: emptyList()
                onMessageReceived(messages)
            }
    }

    fun formatTimestamp(date: Date): String {
        val sdf = SimpleDateFormat("hh:mm a", Locale.getDefault()) // Format: 12:42 PM
        return sdf.format(date)
    }



}