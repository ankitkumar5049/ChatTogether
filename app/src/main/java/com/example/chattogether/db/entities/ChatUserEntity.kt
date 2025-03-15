package com.example.chattogether.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "chat_users")
data class ChatUserEntity(
    @PrimaryKey val userId: String,
    val userName: String,
    val currentUserId: String
)
