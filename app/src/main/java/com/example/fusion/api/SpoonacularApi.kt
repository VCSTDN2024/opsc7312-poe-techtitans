package com.example.fusion.api

import com.example.fusion.model.RecipeDetailsResponse
import com.example.fusion.model.RecipeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.QueryMap

interface SpoonacularApi {

    @GET("recipes/complexSearch")
    fun searchRecipes(
        @Query("apiKey") apiKey: String,
        @QueryMap parameters: Map<String, String>
    ): Call<RecipeResponse>

    @GET("recipes/{id}/information")
    fun getRecipeInformation(
        @Path("id") id: Int,
        @Query("apiKey") apiKey: String,
        @Query("includeNutrition") includeNutrition: Boolean = true
    ): Call<RecipeDetailsResponse>
}
