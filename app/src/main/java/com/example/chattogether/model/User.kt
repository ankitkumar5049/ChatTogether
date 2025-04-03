package com.example.chattogether.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val createdAt: String = "",
    val userDeleted: Int = 0
)