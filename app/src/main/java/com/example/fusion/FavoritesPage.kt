package com.example.fusion

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.model.Recipe
import com.example.fusion.model.RecipeDetailsResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.lang.reflect.Type
import java.util.concurrent.CountDownLatch

class FavoritesPage : AppCompatActivity() {

    // Declare UI components
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var recipeDetailsMap: Map<Int, RecipeDetailsResponse>
    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_page)

        // Initialize bottom navigation view and RecyclerView
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        rvSearchResults = findViewById(R.id.recyclerView)
        sharedPreferences = getSharedPreferences("favorite_recipes_prefs", Context.MODE_PRIVATE)

        // Handle window insets to ensure proper padding for bottom navigation view
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Set up bottom navigation bar
        setupBottomNavigation()

        // Set up RecyclerView with a GridLayoutManager and an empty RecipeAdapter
        rvSearchResults.layoutManager = GridLayoutManager(this, 2)  // Display items in a 2-column grid
        recipeAdapter = RecipeAdapter(this, mutableListOf())
        rvSearchResults.adapter = recipeAdapter

        // Display saved recipes from Firebase and local storage
        displaySavedRecipes()
        displayLocalSavedRecipes()
    }

    // Function to display saved recipes from Firebase
    private fun displaySavedRecipes() {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            // Show error if user is not signed in
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            return
        }

        // Get a reference to the user's saved favorites in Firebase
        val favoritesRef = FirebaseDatabase.getInstance().getReference("users/$userId/favorites")
        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipeIds = mutableListOf<String>()
                // Loop through the snapshot and add each recipe ID to the list
                snapshot.children.forEach {
                    recipeIds.add(it.key ?: "")
                }

                // Fetch detailed information for each recipe using its ID
                fetchRecipeDetails(recipeIds)
            }

            override fun onCancelled(error: DatabaseError) {
                // Show error message if there was a problem fetching the data
                Toast.makeText(this@FavoritesPage, "Error fetching favorites: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    // Function to fetch detailed recipe information for the saved recipe IDs
    private fun fetchRecipeDetails(recipeIds: List<String>) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val recipes = mutableListOf<Recipe>()

        // Use CountDownLatch to wait for all recipe details to be fetched before updating UI
        val countDownLatch = CountDownLatch(recipeIds.size)

        // Loop through each recipe ID and fetch its details from Firebase
        recipeIds.forEach { id ->
            val recipeRef = FirebaseDatabase.getInstance().getReference("users/$userId/recipes/$id")
            recipeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    // Convert the data snapshot to a Recipe object
                    val recipe = dataSnapshot.getValue(Recipe::class.java)
                    if (recipe != null) {
                        recipes.add(recipe)  // Add the recipe to the list if it's not null
                    }
                    countDownLatch.countDown()  // Decrement the count once data is fetched
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Show error message if there was a problem fetching recipe details
                    Toast.makeText(this@FavoritesPage, "Error fetching recipe details: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    countDownLatch.countDown()  // Ensure latch count decreases even on failure
                }
            })
        }

        // Use a background thread to wait for all recipe fetch operations to complete
        Thread {
            try {
                countDownLatch.await()  // Wait for all fetches to complete
                runOnUiThread {
                    recipeAdapter.updateData(recipes)  // Update the adapter data on the UI thread
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()  // Handle interruptions
            }
        }.start()
    }

    // Function to display locally saved recipes
    private fun displayLocalSavedRecipes() {
        val savedRecipesJson = sharedPreferences.getString("favorite_recipes", "[]")
        if (savedRecipesJson.isNullOrEmpty()) {
            Toast.makeText(this, "No saved recipes available", Toast.LENGTH_SHORT).show()
            return
        }
        val type: Type = object : TypeToken<List<RecipeDetailsResponse>>() {}.type
        val savedRecipes: List<RecipeDetailsResponse> = gson.fromJson(savedRecipesJson, type)
        val recipes = mutableListOf<Recipe>()

        // Convert saved RecipeDetailsResponse to Recipe objects for the list
        savedRecipes.forEach { details ->
            val recipe = Recipe(
                id = details.id,
                title = details.title,
                image = details.image,
                isSaved = true // Set to true since these are saved recipes
            )
            recipes.add(recipe)
        }

        // Update the adapter data with locally saved recipes
        recipeAdapter.updateData(recipes)

        // Store the complete details for accessing later when viewing recipe details
        recipeDetailsMap = savedRecipes.associateBy { it.id }
    }




    // Function to set up bottom navigation functionality
    private fun setupBottomNavigation() {
        // Set the currently selected item in the bottom navigation bar to "Saved"
        bottomNavigationView.selectedItemId = R.id.navigation_saved

        // Set listeners for bottom navigation item selections
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java))  // Navigate to HomePage
                    true
                }
                R.id.navigation_saved -> true  // Stay on the current page (Favorites)
                R.id.navigation_calendar -> {
                    startActivity(Intent(this, MealPlannerPage::class.java))  // Navigate to MealPlannerPage
                    true
                }
                R.id.navigation_cart -> {
                    startActivity(Intent(this, ShoppingListPage::class.java))  // Navigate to ShoppingListPage
                    true
                }
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsPage::class.java))  // Navigate to SettingsPage
                    true
                }
                else -> false  // Return false for any unhandled item selections
            }
        }
    }
}
