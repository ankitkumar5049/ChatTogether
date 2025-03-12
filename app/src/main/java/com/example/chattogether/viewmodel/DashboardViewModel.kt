package com.example.chattogether.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import com.example.chattogether.navigation.Screen
import com.google.firebase.firestore.FirebaseFirestore

class DashboardViewModel: ViewModel() {
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
        currentUserId: String,
        onComplete: (String) -> Unit
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
                        onComplete("")
                        return@addOnSuccessListener
                    }
                } else {
                    println("No user found")
                    onComplete("No User Found!")
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting user: ${exception.message}")
                onComplete("Unexpected Error!")
            }
    }

}