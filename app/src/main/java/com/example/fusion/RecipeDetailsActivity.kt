package com.example.fusion

import android.content.Intent
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
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView  // Added for Bottom Navigation
    private val apiKey = "b6544cc7e3f043ba8063aaedbb84cb9e" // Replace with your actual API key

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)  // Initialize BottomNavigationView

        // Set up Bottom Navigation similar to HomePage
        setupBottomNavigation()

        val recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            getRecipeDetails(recipeId)
        } else {
            showError("Invalid Recipe ID")
        }
    }

    private fun setupBottomNavigation() {
        // Set the selected item as the appropriate one
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java))  // Navigate to home page
                    true
                }
                R.id.navigation_saved -> {
                    startActivity(Intent(this, FavoritesPage::class.java))  // Navigate to Favorites
                    true
                }
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, MealPlannerPage::class.java))  // Navigate to Calendar
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, ShoppingListPage::class.java))  // Navigate to Shopping List
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsPage::class.java))  // Navigate to Settings
                    true
                }
                else -> false
            }
        }
    }

    private fun getRecipeDetails(recipeId: Int) {
        val call = RetrofitInstance.api.getRecipeInformation(recipeId, apiKey)
        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(call: Call<RecipeDetailsResponse>, response: Response<RecipeDetailsResponse>) {
                if (response.isSuccessful) {
                    val recipeDetails = response.body()
                    recipeDetails?.let {
                        displayRecipeDetails(it)
                        setupViewPager(it)
                    }
                } else {
                    showError("Failed to load recipe details.")
                }
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                showError("Error: ${t.message}")
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
            StepsFragment.newInstance(details.instructions ?: "No instructions available")
        )

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        val tabTitles = listOf("Overview", "Ingredients", "Steps")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
