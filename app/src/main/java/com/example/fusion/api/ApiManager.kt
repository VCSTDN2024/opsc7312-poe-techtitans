package com.example.fusion.api

import androidx.test.espresso.idling.CountingIdlingResource
import com.example.fusion.model.Recipe
import com.example.fusion.model.RecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiManager {

    companion object {
        // Idling resource to track async operations (e.g., network requests)
        val idlingResource = CountingIdlingResource("ApiCall")
    }

    // Fetch recipes example using Retrofit
    fun fetchRecipes(query: String, parameters: Map<String, String>, callback: (List<Recipe>) -> Unit) {
        // Increment the idling resource count when async operation starts
        idlingResource.increment()

        val call = RetrofitInstance.api.searchRecipes(query, parameters)
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                // Handle successful response
                callback(response.body()?.results ?: emptyList())

                // Decrement the idling resource count when the async operation finishes
                idlingResource.decrement()
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                // Handle failure
                idlingResource.decrement()
            }
        })
    }
}
