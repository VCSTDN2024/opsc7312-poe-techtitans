package com.example.fusion

import android.os.Bundle
import android.text.Html
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fusion.api.RetrofitInstance
import com.example.fusion.model.RecipeDetailsResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailsActivity : AppCompatActivity() {

    private val apiKey = "b6544cc7e3f043ba8063aaedbb84cb9e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)

        if (recipeId != -1) {
            getRecipeDetails(recipeId)
        } else {
            Toast.makeText(this, "Invalid Recipe ID", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun getRecipeDetails(recipeId: Int) {
        val call = RetrofitInstance.api.getRecipeInformation(recipeId, apiKey)
        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(
                call: Call<RecipeDetailsResponse>,
                response: Response<RecipeDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    val recipeDetails = response.body()
                    recipeDetails?.let {
                        displayRecipeDetails(it)
                    }
                } else {
                    Toast.makeText(
                        this@RecipeDetailsActivity,
                        "Failed to get recipe details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                Toast.makeText(
                    this@RecipeDetailsActivity,
                    "Error: ${t.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun displayRecipeDetails(details: RecipeDetailsResponse) {
        val imageView: ImageView = findViewById(R.id.iv_recipe_image)
        val titleView: TextView = findViewById(R.id.tv_recipe_title)
        val summaryView: TextView = findViewById(R.id.tv_recipe_summary)
        val ingredientsView: TextView = findViewById(R.id.tv_ingredients)
        val instructionsView: TextView = findViewById(R.id.tv_instructions)

        titleView.text = details.title
        Glide.with(this).load(details.image).into(imageView)
        summaryView.text = Html.fromHtml(details.summary)

        // Display ingredients
        val ingredientsText = details.extendedIngredients.joinToString(separator = "\n") { it.original }
        ingredientsView.text = ingredientsText

        // Display instructions
        if (!details.instructions.isNullOrEmpty()) {
            instructionsView.text = Html.fromHtml(details.instructions)
        } else {
            instructionsView.text = "No instructions available."
        }
    }
}
