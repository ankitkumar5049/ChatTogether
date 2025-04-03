package com.example.chattogether.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chattogether.db.entities.User

@Dao
interface UserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    @Query("SELECT * FROM user_table WHERE username = :username")
    suspend fun getUserByUsername(username: String): User?

    @Query("SELECT * FROM user_table WHERE userId = :userId")
    suspend fun getUserByUserId(userId: String): User?

    @Query("UPDATE user_table SET name = :name, username = :username, dob = :dob WHERE userId = :userId")
    suspend fun updateUserDetails(userId: String, name: String, username: String, dob: String)
}
