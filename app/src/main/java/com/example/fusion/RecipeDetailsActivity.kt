package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.view.View
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
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private val apiKey = "a6db912098794bf4a235d7fff9bb0fc5"

    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        setupBottomNavigation()

        // Get the recipeId from the intent
        recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            getRecipeDetails(recipeId)
        } else {
            showError("Invalid Recipe ID")
        }
    }

    // Opens the Meal Planner and passes the recipeId
    fun openMealPlanner(view: View) {
        val intent = Intent(this, MealPlannerActivity::class.java)
        intent.putExtra("RECIPE_ID", recipeId) // Pass the recipeId to MealPlannerActivity
        startActivity(intent)
    }


    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_home
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.navigation_saved -> {
                    startActivity(Intent(this, FavoritesPage::class.java))
                    true
                }
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, MealPlannerPage::class.java))
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, ShoppingListPage::class.java))
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsPage::class.java))
                    true
                }
                else -> false
            }
        }
    }

    private fun getRecipeDetails(recipeId: Int) {
        val call = RetrofitInstance.api.getRecipeInformation(recipeId, apiKey, includeNutrition = true)
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
            StepsFragment.newInstance(details.instructions ?: "No instructions available"),
            NutritionFragment.newInstance(details.nutrition)  // Added NutritionFragment
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
