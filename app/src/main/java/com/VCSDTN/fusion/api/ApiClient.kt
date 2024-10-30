package com.VCSDTN.fusion.api

import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object ApiClient {
    // Base URL for the API
    private const val BASE_URL = "https://fusion-api-0pgz.onrender.com/"

    // Variable to hold the authentication token for authorized requests
    var authToken: String? = null

    // Interceptor class to attach the Authorization header with the token to each request
    class TokenInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            // Get the original request
            val originalRequest = chain.request()
            val token = authToken

            // Create a new request with the Authorization header if the token exists
            val newRequest = if (token != null) {
                originalRequest.newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                originalRequest
            }

            // Proceed with the new or original request
            return chain.proceed(newRequest)
        }
    }

    // Interceptor to log HTTP request and response data (used for debugging)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    // OkHttpClient setup with the token interceptor, logging interceptor, and custom timeout settings
    private val httpClient: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(TokenInterceptor())   // Add token interceptor for authorization
        .addInterceptor(loggingInterceptor)   // Add logging interceptor for request/response logging
        .connectTimeout(60, TimeUnit.SECONDS)  // Set connection timeout to 60 seconds
        .readTimeout(60, TimeUnit.SECONDS)     // Set read timeout to 60 seconds
        .writeTimeout(60, TimeUnit.SECONDS)    // Set write timeout to 60 seconds
        .build()

    // Retrofit instance to manage API calls with the base URL, client, and Gson converter
    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)  // Set the base URL for the API
        .client(httpClient) // Use the custom OkHttpClient with interceptors and timeout settings
        .addConverterFactory(GsonConverterFactory.create()) // Add Gson converter to handle JSON
        .build()
}
