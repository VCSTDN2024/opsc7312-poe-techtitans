package com.example.fusion

import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.example.fusion.api.RetrofitInstance
import com.example.fusion.model.RecipeDetailsResponse
import com.example.fusion.utils.TranslationUtil
import com.example.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Activity to display overview of a recipe including details, ingredients, steps, and nutrition.
class RecipeOverviewActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private var recipeId: Int = -1
    private val apiKey = BuildConfig.API_KEY

    // Initialize activity and layout, retrieve recipe ID from intent.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_overview)
        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)

        if (recipeId != -1) {
            loadRecipeDetailsFromAPI(recipeId)
        } else {
            showError("Invalid Recipe ID")
        }
        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.container_pass1),
            findViewById<TextView>(R.id.text_email),
            findViewById<TextView>(R.id.textView),
            findViewById<TextView>(R.id.container_pass)
        )

        val buttons = listOf(
            findViewById<Button>(R.id.button),
            findViewById<Button>(R.id.btnSignupPage2),
            findViewById<Button>(R.id.btnLoginPage2)
        )


        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
            TranslationUtil.translateButtons(this, buttons, "af")
        }
    }

    // Fetch recipe details from API and handle the response.
    private fun loadRecipeDetailsFromAPI(recipeId: Int) {
        val call = RetrofitInstance.api.getRecipeInformation(recipeId, apiKey)

        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(
                call: Call<RecipeDetailsResponse>,
                response: Response<RecipeDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    response.body()?.let {
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

    // Display the main image of the recipe.
    private fun displayRecipeDetails(details: RecipeDetailsResponse) {
        val imageView: ImageView = findViewById(R.id.iv_recipe_image)
        Glide.with(this).load(details.image).into(imageView)
    }

    // Set up ViewPager with fragments for different sections of the recipe.
    private fun setupViewPager(details: RecipeDetailsResponse) {
        val fragments = listOf(
            OverviewFragment.newInstance(details.summary),
            IngredientsFragment.newInstance(
                details.extendedIngredients.joinToString("\n") { it.original },
                recipeId
            ),
            StepsFragment.newInstance(details.instructions ?: "No instructions available"),
            NutritionFragment.newInstance(details.nutrition)
        )

        viewPager.adapter = object : FragmentStateAdapter(this) {
            override fun getItemCount(): Int = fragments.size
            override fun createFragment(position: Int): Fragment = fragments[position]
        }

        // Set tab titles and attach them to the ViewPager.
        val tabTitles = listOf("Overview", "Ingredients", "Steps", "Nutrition")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            tab.text = tabTitles[position]
        }.attach()
    }

    // Show error messages as toast.
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
