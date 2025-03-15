package com.example.chattogether.db.repo

import com.example.chattogether.db.dao.ChatUserDao
import com.example.chattogether.db.entities.ChatUserEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ChatUserRepository(private val chatUserDao: ChatUserDao) {

    suspend fun insertChatUsers(users: List<ChatUserEntity>) {
        withContext(Dispatchers.IO) {
            chatUserDao.insertChatUsers(users)
        }
    }

    suspend fun getChatUsers(currentUserId: String): List<ChatUserEntity> {
        return withContext(Dispatchers.IO) {
            chatUserDao.getChatUsers(currentUserId)
        }
    }

    suspend fun deleteChatUsers(currentUserId: String) {
        withContext(Dispatchers.IO) {
            chatUserDao.deleteChatUsers(currentUserId)
        }
    }
}
