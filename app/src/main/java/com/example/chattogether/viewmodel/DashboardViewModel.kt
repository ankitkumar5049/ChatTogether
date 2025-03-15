package com.example.chattogether.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.example.chattogether.base.BaseViewModel
import com.example.chattogether.db.UserDatabase
import com.example.chattogether.db.entities.ChatUserEntity
import com.example.chattogether.db.repo.ChatUserRepository
import com.example.chattogether.navigation.Screen
import com.example.chattogether.utils.Constant
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch

class DashboardViewModel(application: Application): BaseViewModel(application) {

    private val chatUserDao = UserDatabase.getDatabase(application).chatUserDao()
    private val repository = ChatUserRepository(chatUserDao)
    fun getUserChats(db: FirebaseFirestore, userId: String, onResult: (List<Map<String, Any>>) -> Unit) {
        val chatRooms = mutableListOf<Map<String, Any>>()

        db.collection(Constant.CHATS)
            .whereEqualTo("user1", userId)
            .get()
            .addOnSuccessListener { querySnapshot1 ->
                querySnapshot1.documents.mapNotNullTo(chatRooms) { it.data }

                db.collection(Constant.CHATS)
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
        username_: String,
        navController: NavController,
        currentUserId: String,
        onComplete: (String) -> Unit
    ) {
        var username = username_
        if(username_.takeLast(4)!=".com") {
            username = username_ + Constant.MAIL_EXTENSION
        }
        db.collection(Constant.USERS)
            .whereEqualTo("username", username)
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

    fun saveChatUsersToLocal(uid: String, chatUsers: List<Pair<String, String>>) {
        viewModelScope.launch {
            // Clear old data for this user
            repository.deleteChatUsers(uid)

            // Insert new data
            val chatUserEntities = chatUsers.map { ChatUserEntity(it.first, it.second, uid) }
            repository.insertChatUsers(chatUserEntities)
        }
    }

    fun getChatUsersFromLocal(uid: String, onResult: (List<ChatUserEntity>) -> Unit) {
        viewModelScope.launch {
            val users = repository.getChatUsers(uid)
            onResult(users)
        }
    }


}