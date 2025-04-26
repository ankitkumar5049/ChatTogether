package com.example.chattogether.domain

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor

fun createOkHttpClient(): OkHttpClient {
    val logging = HttpLoggingInterceptor()
    logging.level = HttpLoggingInterceptor.Level.BASIC // Log request + response body

    return OkHttpClient.Builder()
        .addInterceptor(logging)
        .build()
}
