package com.example.fusion.api

import com.example.fusion.model.AddIngredientsRequest
import com.example.fusion.model.AddIngredientsResponse
import com.example.fusion.model.ShoppingListItem
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {
    @POST("shopping-list/items")
    suspend fun addIngredientsToShoppingList(
        @Body requestBody: AddIngredientsRequest
    ): Response<AddIngredientsResponse>

    @GET("shopping-list")
    suspend fun getShoppingList(): Response<List<ShoppingListItem>>
}
