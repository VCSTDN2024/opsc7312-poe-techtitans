package com.example.fusion

import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.fusion.api.RetrofitInstance
import com.example.fusion.model.RecipeDetailsResponse
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeOverviewActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var recipeId: String? = null
    private val apiKey = "ffb55d8730b748a1ad84cfd535e3debc" // Replace with your Spoonacular API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_overview)

        // Get the recipeId from the intent
        recipeId = intent.getStringExtra("RECIPE_ID")

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        // Fetch recipe details from Spoonacular API
        if (recipeId != null) {
            loadRecipeDetailsFromAPI(recipeId!!)
        } else {
            showError("Invalid Recipe ID")
        }
    }

    private fun loadRecipeDetailsFromAPI(recipeId: String) {
        val call = RetrofitInstance.api.getRecipeInformation(recipeId.toInt(), apiKey)

        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(call: Call<RecipeDetailsResponse>, response: Response<RecipeDetailsResponse>) {
                if (response.isSuccessful) {
                    val recipeDetails = response.body()
                    recipeDetails?.let {
                        displayRecipeDetails(it)
                        setupViewPager(it)
                    }
                } else {
                    showError("Failed to fetch recipe details")
                }
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                showError("Error: ${t.localizedMessage}")
            }
        })
    }

    private fun displayRecipeDetails(details: RecipeDetailsResponse) {
        val imageView: ImageView = findViewById(R.id.iv_recipe_image)
        Glide.with(this).load(details.image).into(imageView)
    }

    private fun setupViewPager(details: RecipeDetailsResponse) {
        val fragments = listOf(
            OverviewFragment.newInstance(details.summary),
            IngredientsFragment.newInstance(details.extendedIngredients.joinToString("\n") { it.original }),
            StepsFragment.newInstance(details.instructions ?: "No instructions available"),
            NutritionFragment.newInstance(details.nutrition)
        )

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        val tabTitles = listOf("Overview", "Ingredients", "Steps", "Nutrition")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
