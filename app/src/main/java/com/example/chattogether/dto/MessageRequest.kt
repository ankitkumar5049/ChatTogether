package com.example.chattogether.dto

data class MessageRequest(
    val inputs: String
)

data class HuggingFaceResponse(
    val generated_text: String?
)
