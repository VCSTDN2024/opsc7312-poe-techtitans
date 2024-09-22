package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.model.Recipe
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class FavoritesPage : AppCompatActivity() {
    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var rvSearchResults: RecyclerView

    private lateinit var recipeAdapter: RecipeAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_favorites_page)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        rvSearchResults = findViewById(R.id.recyclerView)

        // Setup bottom navigation
        setupBottomNavigation()

        // Setup RecyclerView
        rvSearchResults.layoutManager = GridLayoutManager(this, 2)
        recipeAdapter = RecipeAdapter(this, mutableListOf())
        rvSearchResults.adapter = recipeAdapter

        displaySavedRecipes()
    }

    private fun displaySavedRecipes(){
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId == null) {
            Toast.makeText(this, "User not signed in", Toast.LENGTH_SHORT).show()
            return
        }

        val favoritesRef = FirebaseDatabase.getInstance().getReference("users/$userId/favorites")
        favoritesRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipeIds = mutableListOf<String>()
                snapshot.children.forEach {
                    recipeIds.add(it.key ?: "")
                }

                // Now fetch the details of these recipes
                fetchRecipeDetails(recipeIds)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@FavoritesPage, "Error fetching favorites: ${error.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun fetchRecipeDetails(recipeIds: List<String>) {
        // Assuming you have a function to fetch recipe details from the IDs
        val recipes = mutableListOf<Recipe>()
        recipeIds.forEach { id ->
            // Placeholder for fetching recipe details, replace with actual call
            // For instance, you could use an API or further Firebase calls here
            val recipe = fetchRecipeById(id) // This should be an asynchronous call returning a Recipe object
            recipes.add(recipe)
        }

        // Update the adapter with the fetched recipes
        runOnUiThread {
            recipeAdapter.updateData(recipes)
        }
    }

    private fun fetchRecipeById(id: String): Recipe {
        // Placeholder function to simulate fetching a recipe by ID
        // Replace with actual fetching logic
        return Recipe(id.toInt(), "Sample Title for $id", "http://image.url/for/$id.jpg")
    }

    private fun setupBottomNavigation() {
        // Set the selected item as Home
        bottomNavigationView.selectedItemId = R.id.navigation_saved

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java))
                    true
                }
                R.id.navigation_saved -> true
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
}