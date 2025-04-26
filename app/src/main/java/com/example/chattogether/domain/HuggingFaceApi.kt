package com.example.chattogether.domain

import com.example.chattogether.dto.BotResponse
import com.example.chattogether.dto.HuggingFaceRequest
import com.example.chattogether.dto.HuggingFaceResponse
import com.example.chattogether.dto.MessageRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface HuggingFaceApi {
//    @POST("models/microsoft/DialoGPT-small")
//    suspend fun getBotReply(
//        @Body request: MessageRequest
//    ): HuggingFaceResponse

    @POST("models/microsoft/DialoGPT-small")
    suspend fun queryModel(@Body request: HuggingFaceRequest): List<BotResponse>
}
