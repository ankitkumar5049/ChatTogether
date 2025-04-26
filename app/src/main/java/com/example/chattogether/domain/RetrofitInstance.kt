package com.example.chattogether.domain

import com.example.chattogether.utils.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("Authorization", "")
                .build()
            chain.proceed(request)
        }
        .build()

    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl("https://api-inference.huggingface.co/")
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val api: HuggingFaceApi by lazy {
        retrofit.create(HuggingFaceApi::class.java)
    }
}
