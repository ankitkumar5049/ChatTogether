package com.example.chattogether.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class User(
    @PrimaryKey
    val userId: String,
    val name: String,
    val username: String,
    val dob: String,
    val password: String
)