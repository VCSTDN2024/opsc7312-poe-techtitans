package com.VCSDTN.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.VCSDTN.fusion.api.RetrofitInstance
import com.VCSDTN.fusion.model.Recipe
import com.VCSDTN.fusion.model.RecipeDetailsResponse
import com.VCSDTN.fusion.utils.TranslationUtil
import com.VCSDTN.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealPlannerMeal : AppCompatActivity() {

    // Variables to hold the selected day and meal time
    private lateinit var selectedDay: String
    private lateinit var selectedMealTime: String

    // RecyclerView for displaying recipes and its adapter
    private lateinit var rvMealRecipes: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var bottomNavigationView: BottomNavigationView

    // API key for accessing the Retrofit API
    private val apiKey = BuildConfig.API_KEY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner_meal)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Retrieve the selected day and meal time from the intent passed from the previous activity
        selectedDay = intent.getStringExtra("DAY_SELECTED").toString()
        selectedMealTime = intent.getStringExtra("MEAL_TIME").toString()

        // Set the meal time text in the TextView
        val mealTimeText: TextView = findViewById(R.id.mealTimeText)
        mealTimeText.text = selectedMealTime
        // Initialize RecyclerView and set its layout to a grid with 2 columns
        rvMealRecipes = findViewById(R.id.rv_meal_recipes)
        rvMealRecipes.layoutManager = GridLayoutManager(this, 2)
        recipeAdapter =
            RecipeAdapter(this, mutableListOf()) // Initialize the adapter with an empty list
        rvMealRecipes.adapter = recipeAdapter

        // Load the recipes for the selected day and meal time from Firebase
        loadRecipesFromFirebase()

        // Back arrow functionality to go back to the previous screen
        findViewById<ImageView>(R.id.btnBack).setOnClickListener {
            onBackPressed() // Go back when the back arrow is clicked
        }
        setupBottomNavigation()

        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.titleText),
            findViewById<TextView>(R.id.mealTimeText)
        )

        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
        }
    }

    // Function to load recipes from Firebase based on the selected day and meal time
    private fun loadRecipesFromFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser // Get the current logged-in user
        if (currentUser != null) {
            // Get the reference to the user's meal plans in the Firebase Realtime Database
            val userId = currentUser.uid
            val database =
                FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
                    .getReference("users").child(userId).child("meal_plans")

            // Add a listener to retrieve meal plans from the database
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var foundRecipes = false // Flag to check if recipes are found

                    // Iterate through the meal plans in Firebase
                    for (mealPlanSnapshot in snapshot.children) {
                        val mealPlan = mealPlanSnapshot.value as? Map<*, *>
                        val day = mealPlan?.get("day") as? String
                        val mealTime = mealPlan?.get("mealTime") as? String
                        val recipeId = mealPlan?.get("recipeId")?.toString()

                        // Check if the meal plan matches the selected day and meal time
                        if (day == selectedDay && mealTime == selectedMealTime && recipeId != null) {
                            foundRecipes = true // Recipe found for the selected day and meal time
                            loadRecipeDetailsFromAPI(recipeId) // Load the recipe details using the recipe ID
                        }
                    }

                    // If no recipes are found, display a Toast message
                    if (!foundRecipes) {
                        Toast.makeText(
                            this@MealPlannerMeal,
                            "No recipes found for $selectedMealTime on $selectedDay",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Display an error message if there's an issue with loading recipes from Firebase
                    Toast.makeText(
                        this@MealPlannerMeal,
                        "Failed to load recipes",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        } else {
            // If no user is logged in, display a Toast message
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    // Function to load recipe details from the API using Retrofit
    private fun loadRecipeDetailsFromAPI(recipeId: String) {
        // Make an API call to fetch recipe details using the recipe ID
        val call = RetrofitInstance.api.getRecipeInformation(recipeId.toInt(), apiKey)

        // Handle the API response
        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(
                call: Call<RecipeDetailsResponse>,
                response: Response<RecipeDetailsResponse>
            ) {
                if (response.isSuccessful) {
                    // If the response is successful, extract the recipe details
                    val recipeDetails = response.body()
                    recipeDetails?.let {
                        // Create a Recipe object and update the RecyclerView with the new recipe
                        val recipe = Recipe(
                            id = it.id,
                            title = it.title,
                            image = it.image
                        )
                        updateRecipeList(recipe)
                    }
                } else {
                    // If the response fails, show a Toast message
                    Toast.makeText(
                        this@MealPlannerMeal,
                        "Failed to fetch recipe details",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                // Handle any failure in the API call and display an error message
                Toast.makeText(
                    this@MealPlannerMeal,
                    "Error: ${t.localizedMessage}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }


    private fun setupBottomNavigation() {
        // Set the selected item as Home
        bottomNavigationView.selectedItemId = R.id.navigation_calendar

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java)) // Navigate to FavoritesPage
                    true
                }

                R.id.navigation_saved -> {
                    startActivity(
                        Intent(
                            this,
                            FavoritesPage::class.java
                        )
                    ) // Navigate to FavoritesPage
                    true
                }

                R.id.navigation_calendar -> true
                R.id.navigation_cart -> {
                    startActivity(
                        Intent(
                            this,
                            ShoppingListPage::class.java
                        )
                    ) // Navigate to ShoppingListPage
                    true
                }

                R.id.navigation_settings -> {
                    startActivity(
                        Intent(
                            this,
                            SettingsPage::class.java
                        )
                    ) // Navigate to SettingsPage
                    true
                }

                else -> false
            }
        }
    }

    // Function to update the recipe list displayed in the RecyclerView
    private fun updateRecipeList(recipe: Recipe) {
        // Get the current list of recipes from the adapter
        val currentList = recipeAdapter.getRecipeList().toMutableList()
        // Add the new recipe to the list
        currentList.add(recipe)
        // Update the adapter with the new recipe list
        recipeAdapter.updateData(currentList)
    }
}
