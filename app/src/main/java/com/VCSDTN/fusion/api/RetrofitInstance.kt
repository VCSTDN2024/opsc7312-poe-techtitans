package com.VCSDTN.fusion.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance {
    // Base URL for the Spoonacular API
    private const val BASE_URL = "https://api.spoonacular.com/"

    // Lazily initialized Retrofit instance for Spoonacular API
    val api: SpoonacularApi by lazy {
        // Configure Retrofit builder with base URL and JSON converter
        Retrofit.Builder()
            .baseUrl(BASE_URL)  // Set the base URL for API calls
            .addConverterFactory(GsonConverterFactory.create())  // Add Gson converter to handle JSON responses
            .build()  // Build the Retrofit instance
            .create(SpoonacularApi::class.java)  // Create and return the API implementation for Spoonacular
    }
}
