package com.example.chattogether.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chattogether.db.entities.ChatUserEntity

@Dao
interface ChatUserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChatUsers(users: List<ChatUserEntity>)

    @Query("SELECT * FROM chat_users WHERE currentUserId = :currentUserId")
    suspend fun getChatUsers(currentUserId: String): List<ChatUserEntity>

    @Query("DELETE FROM chat_users WHERE currentUserId = :currentUserId")
    suspend fun deleteChatUsers(currentUserId: String)
}
