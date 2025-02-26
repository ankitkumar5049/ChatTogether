package com.example.chattogether.viewmodel

import androidx.lifecycle.ViewModel
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query

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
        db: FirebaseFirestore, chatId: String, senderId: String, receiverId: String, message: String
    ) {
        val messageData = mapOf(
            "senderId" to senderId,
            "receiverId" to receiverId,
            "message" to message,
            "timestamp" to FieldValue.serverTimestamp()
        )

        db.collection("chats").document(chatId).collection("messages").add(messageData)
            .addOnSuccessListener { println("Message sent!") }
            .addOnFailureListener { println("Failed to send message!") }
    }

    fun listenForMessages(db: FirebaseFirestore, chatId: String, onMessageReceived: (List<Map<String, Any>>) -> Unit) {
        db.collection("chats").document(chatId).collection("messages")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    println("Error: ${error.message}")
                    return@addSnapshotListener
                }

                val messages = snapshot?.documents?.map { it.data!! } ?: emptyList()
                onMessageReceived(messages)
            }
    }



}