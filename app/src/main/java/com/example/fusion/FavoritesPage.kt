package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.model.Recipe
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.util.concurrent.CountDownLatch

class FavoritesPage : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView
    private lateinit var rvSearchResults: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_favorites_page)

        // Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        rvSearchResults = findViewById(R.id.recyclerView)

        // Handle window insets to ensure proper padding
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

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
        val userId = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val recipes = mutableListOf<Recipe>()

        val countDownLatch = CountDownLatch(recipeIds.size)  // Use a CountDownLatch to wait for all data fetches

        recipeIds.forEach { id ->
            val recipeRef = FirebaseDatabase.getInstance().getReference("users/$userId/recipes/$id")
            recipeRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val recipe = dataSnapshot.getValue(Recipe::class.java)
                    if (recipe != null) {
                        recipes.add(recipe)
                    }
                    countDownLatch.countDown()  // Decrease count after each fetch
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(this@FavoritesPage, "Error fetching recipe details: ${databaseError.message}", Toast.LENGTH_SHORT).show()
                    countDownLatch.countDown()  // Ensure count down even on failure to prevent deadlock
                }
            })
        }

        // Wait for all fetches to complete
        Thread {
            try {
                countDownLatch.await()  // Wait for all responses
                runOnUiThread {
                    recipeAdapter.updateData(recipes)  // Update adapter data on UI thread
                }
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }
        }.start()
    }

    private fun setupBottomNavigation() {
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