package com.example.fusion

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.fusion.api.RetrofitInstance
import com.example.fusion.model.Recipe
import com.example.fusion.model.RecipeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : AppCompatActivity() {

    private val apiKey = "b6544cc7e3f043ba8063aaedbb84cb9e"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_page)

        val etSearch: EditText = findViewById(R.id.et_search)
        val llSearchResults: LinearLayout = findViewById(R.id.ll_search_results)
        val filterSection: LinearLayout = findViewById(R.id.filterSection)
        val btnShowFilters: Button = findViewById(R.id.btn_show_filters)

        // Automatically load recipes on app start
        loadDefaultRecipes(llSearchResults)

        // Filter radio groups
        val caloriesGroup: RadioGroup = findViewById(R.id.radioGroupCalories)
        val mealTypeGroup: RadioGroup = findViewById(R.id.radioGroupMealType)
        val ingredientGroup: RadioGroup = findViewById(R.id.radioGroupIngredients)

        // Toggle filter visibility on button click
        btnShowFilters.setOnClickListener {
            val filterScrollView: ScrollView = findViewById(R.id.filterScrollView)
            if (filterScrollView.visibility == View.GONE) {
                filterScrollView.visibility = View.VISIBLE
                Toast.makeText(this, "Filter section visible", Toast.LENGTH_SHORT).show()
            } else {
                filterScrollView.visibility = View.GONE
                Toast.makeText(this, "Filter section hidden", Toast.LENGTH_SHORT).show()
            }
        }

        // Search input handling
        etSearch.setOnEditorActionListener { _, _, _ ->
            val query = etSearch.text.toString()
            val selectedFilters = getSelectedFilters(caloriesGroup, mealTypeGroup, ingredientGroup)

            // Clear previous results
            llSearchResults.removeAllViews()

            // Check if search query is not empty
            if (query.isNotEmpty()) {
                searchRecipes(query, selectedFilters, llSearchResults)
            } else {
                Toast.makeText(this, "Please enter a search term", Toast.LENGTH_SHORT).show()
            }
            true
        }
    }

    /**
     * Captures the selected filters from the radio buttons.
     */
    private fun getSelectedFilters(caloriesGroup: RadioGroup, mealTypeGroup: RadioGroup, ingredientGroup: RadioGroup): Map<String, String> {
        val filters = mutableMapOf<String, String>()

        // Capture selected calories filter
        when (caloriesGroup.checkedRadioButtonId) {
            R.id.radioCalories500 -> filters["maxCalories"] = "500"
            R.id.radioCalories1000 -> filters["maxCalories"] = "1000"
            R.id.radioCalories1500 -> filters["maxCalories"] = "1500"
            R.id.radioCalories2000 -> filters["maxCalories"] = "2000"
        }

        // Capture selected meal type filter
        when (mealTypeGroup.checkedRadioButtonId) {
            R.id.radioBreakfast -> filters["type"] = "breakfast"
            R.id.radioLunch -> filters["type"] = "lunch"
            R.id.radioDinner -> filters["type"] = "dinner"
        }

        // Capture selected ingredient filter
        when (ingredientGroup.checkedRadioButtonId) {
            R.id.radioChicken -> filters["includeIngredients"] = "chicken"
            R.id.radioBeef -> filters["includeIngredients"] = "beef"
            R.id.radioFish -> filters["includeIngredients"] = "fish"
            R.id.radioFlour -> filters["includeIngredients"] = "flour"
            R.id.radioEggs -> filters["includeIngredients"] = "eggs"
        }

        return filters
    }

    /**
     * Modifies the API request by including the query and selected filters.
     */
    private fun searchRecipes(query: String, filters: Map<String, String>, llSearchResults: LinearLayout) {
        // Ensure SpoonacularApi has been modified to accept filters
        val call = RetrofitInstance.api.searchRecipes(query, apiKey, filters)
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.results ?: emptyList()
                    displayResults(recipes, llSearchResults)
                } else {
                    Toast.makeText(this@HomePage, "Failed to get recipes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Toast.makeText(this@HomePage, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Automatically loads default recipes on app start.
     */
    private fun loadDefaultRecipes(llSearchResults: LinearLayout) {
        val call = RetrofitInstance.api.searchRecipes("pasta", apiKey, emptyMap())
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(call: Call<RecipeResponse>, response: Response<RecipeResponse>) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.results ?: emptyList()
                    displayResults(recipes, llSearchResults)
                } else {
                    Toast.makeText(this@HomePage, "Failed to load default recipes", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Toast.makeText(this@HomePage, "Error loading default recipes: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    /**
     * Displays the results in the scroll view.
     */
    private fun displayResults(recipes: List<Recipe>, llSearchResults: LinearLayout) {
        val inflater = layoutInflater
        for (recipe in recipes) {
            val recipeView = inflater.inflate(R.layout.recipe_item, llSearchResults, false)
            val recipeTitle = recipeView.findViewById<TextView>(R.id.tv_recipe_title)
            val recipeImage = recipeView.findViewById<ImageView>(R.id.iv_recipe_image)

            recipeTitle.text = recipe.title
            Glide.with(this)
                .load(recipe.image)
                .into(recipeImage)

            llSearchResults.addView(recipeView)
        }
    }
}
