package com.example.camera2api

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

private const val BASE_URL = ""

class ApiServices {

    private var interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    private val okHttpClient by lazy {
        OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }


    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
    val api: ApiServices by lazy {
        retrofit.create(ApiServices::class.java)
    }
}