package com.example.chattogether.db.repo

import com.example.chattogether.db.dao.UserDao
import com.example.chattogether.db.entities.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class UserRepository(private val userDao: UserDao) {

    suspend fun insertUser(user: User) {
        withContext(Dispatchers.IO) {
            userDao.insertUser(user)
        }
    }

    suspend fun getUserByUsername(username: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByUsername(username)
        }
    }

    suspend fun getUserByUserId(userId: String): User? {
        return withContext(Dispatchers.IO) {
            userDao.getUserByUserId(userId)
        }
    }

    suspend fun updateUserDetails(userId: String, name: String, username: String, dob: String) {
        withContext(Dispatchers.IO) {
            userDao.updateUserDetails(userId, name, username, dob)
        }
    }
}
