package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.api.RetrofitInstance
import com.example.fusion.model.Recipe
import com.example.fusion.model.RecipeResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomePage : AppCompatActivity() {

    private val apiKey = BuildConfig.API_KEY

    private lateinit var etSearch: EditText
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var btnShowFilter: Button
    private lateinit var caloriesGroup: RadioGroup
    private lateinit var mealTypeGroup: RadioGroup
    private lateinit var ingredientGroup: RadioGroup
    private lateinit var filterScrollView: ScrollView
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var btnSearch: ImageButton // Correct casting to ImageButton
    private lateinit var btnClearFilters: Button

    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Set your layout
        setContentView(R.layout.activity_home_page)

        // Initialize views
        etSearch = findViewById(R.id.et_search)
        rvSearchResults = findViewById(R.id.rv_search_results)
        btnShowFilter = findViewById(R.id.btn_show_filters)
        caloriesGroup = findViewById(R.id.radioGroupCalories)
        mealTypeGroup = findViewById(R.id.radioGroupMealType)
        ingredientGroup = findViewById(R.id.radioGroupIngredients)
        filterScrollView = findViewById(R.id.filterScrollView)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        btnSearch = findViewById(R.id.btn_search) // Correct ImageButton cast
        btnClearFilters = findViewById(R.id.btn_clear_filters)

        // Setup RecyclerView
        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        recipeAdapter = RecipeAdapter(this, mutableListOf())
        rvSearchResults.adapter = recipeAdapter

        // Load default recipes
        loadDefaultRecipes()

        // Handle filter button click
        btnShowFilter.setOnClickListener {
            if (filterScrollView.visibility == View.GONE) {
                filterScrollView.visibility = View.VISIBLE
                Toast.makeText(this, "Filter section visible", Toast.LENGTH_SHORT).show()
            } else {
                filterScrollView.visibility = View.GONE
                Toast.makeText(this, "Filter section hidden", Toast.LENGTH_SHORT).show()
            }
        }

        // Handle search action in the keyboard
        etSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                true
            } else {
                false
            }
        }

        // Handle search button click
        btnSearch.setOnClickListener {
            performSearch()
        }

        // Handle clear filters button click
        btnClearFilters.setOnClickListener {
            clearFilters()
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun performSearch() {
        val query = etSearch.text.toString()
        val selectedFilters = getSelectedFilters()

        if (query.isNotEmpty() || selectedFilters.isNotEmpty()) {
            searchRecipes(query, selectedFilters)
        } else {
            Toast.makeText(this, "Please enter a search term or select a filter", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupBottomNavigation() {
        // Set the selected item as Home
        bottomNavigationView.selectedItemId = R.id.navigation_home

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> true
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

    /**
     * Captures the selected filters from the radio buttons.
     */
    private fun getSelectedFilters(): Map<String, String> {
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
     * Searches recipes based on query and filters.
     */
    private fun searchRecipes(query: String, filters: Map<String, String>) {
        val parameters = filters.toMutableMap()
        if (query.isNotEmpty()) {
            parameters["query"] = query
        }

        val call = RetrofitInstance.api.searchRecipes(apiKey, parameters)
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(
                call: Call<RecipeResponse>,
                response: Response<RecipeResponse>
            ) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.results ?: emptyList()
                    displayResults(recipes)
                } else {
                    Toast.makeText(
                        this@HomePage,
                        "Failed to get recipes: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Toast.makeText(
                    this@HomePage,
                    "Error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Updates the RecyclerView with the new list of recipes.
     */
    private fun displayResults(recipes: List<Recipe>) {
        recipeAdapter.updateData(recipes)
    }

    private fun loadDefaultRecipes() {
        val call = RetrofitInstance.api.searchRecipes(apiKey, emptyMap())
        call.enqueue(object : Callback<RecipeResponse> {
            override fun onResponse(
                call: Call<RecipeResponse>,
                response: Response<RecipeResponse>
            ) {
                if (response.isSuccessful) {
                    val recipes = response.body()?.results ?: emptyList()
                    recipeAdapter.updateData(recipes)
                } else {
                    Toast.makeText(
                        this@HomePage,
                        "Failed to load default recipes: ${response.message()}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RecipeResponse>, t: Throwable) {
                Toast.makeText(
                    this@HomePage,
                    "Error loading default recipes: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Clears all selected filters.
     */
    private fun clearFilters() {
        // Clear selections in RadioGroups
        caloriesGroup.clearCheck()
        mealTypeGroup.clearCheck()
        ingredientGroup.clearCheck()

        // Optionally, reset any variables or perform additional actions
        Toast.makeText(this, "Filters cleared", Toast.LENGTH_SHORT).show()
    }
}