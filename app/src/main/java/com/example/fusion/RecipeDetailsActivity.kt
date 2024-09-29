package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
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
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

// Main activity for displaying detailed recipe information.
class RecipeDetailsActivity : AppCompatActivity() {

    // Late initialization of UI components for managing views and navigation.
    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private val apiKey = BuildConfig.API_KEY
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

    private var recipeId: Int = -1  // Placeholder for the recipe ID, default -1 indicating uninitialized.

    // Initial setup function, loads the view components and initializes data fetching.
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_recipe_details)

        // Linking views by ID.
        viewPager = findViewById(R.id.view_pager)
        tabLayout = findViewById(R.id.tab_layout)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Setting up bottom navigation with listeners.
        setupBottomNavigation()

        // Retrieve and validate the recipe ID from the intent.
        recipeId = intent.getIntExtra("RECIPE_ID", -1)
        if (recipeId != -1) {
            getRecipeDetails(recipeId)  // Fetch recipe details if ID is valid.
        } else {
            showError("Invalid Recipe ID")  // Error handling for invalid ID.
        }
    }

    // Event handler to start the Meal Planner Activity and pass the current recipe ID.
    fun openMealPlanner(view: View) {
        val intent = Intent(this, MealPlannerActivity::class.java)
        intent.putExtra("RECIPE_ID", recipeId)  // Attaching the recipe ID to the intent.
        startActivity(intent)
    }

    // Configuration of the bottom navigation view, setting default selected item and item selection listener.
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

    // Fetches the detailed information of the recipe from the server using Retrofit API calls.
    private fun getRecipeDetails(recipeId: Int) {
        val call = RetrofitInstance.api.getRecipeInformation(recipeId, apiKey, includeNutrition = true)
        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(call: Call<RecipeDetailsResponse>, response: Response<RecipeDetailsResponse>) {
                if (response.isSuccessful) {
                    response.body()?.let {
                        checkIfFavoriteAndSave(it)  // Check if the recipe should be saved.
                        displayRecipeDetails(it)  // Update UI with the recipe details.
                        setupViewPager(it)  // Setup tabs and viewpager.
                    }
                } else {
                    showError("Failed to load recipe details.")
                }
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                showError("Error: ${t.message}")  // Display network or other errors.
            }
        })
    }

    // Checks if the recipe is marked as favorite and saves it in Firebase.
    private fun checkIfFavoriteAndSave(recipeDetails: RecipeDetailsResponse) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            databaseReference.child(it).child("favorites").child(recipeId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {  // Check if the recipe is already marked as favorite.
                            saveRecipeDetailsToFirebase(recipeDetails)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showError("Failed to check if recipe is favorite.")
                    }
                })
        }
    }

    // Saves the recipe details to Firebase under the user's account.
    private fun saveRecipeDetailsToFirebase(recipeDetails: RecipeDetailsResponse) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            val recipeRef = databaseReference.child(it).child("recipes").child(recipeId.toString())
            recipeRef.setValue(recipeDetails)
                .addOnSuccessListener {
                    Toast.makeText(this, "Recipe details saved to Firebase.", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save recipe details to Firebase.", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Updates the UI elements to display the recipe details fetched from the server.
    private fun displayRecipeDetails(details: RecipeDetailsResponse) {
        val recipeNameTextView: TextView = findViewById(R.id.textView13)
        recipeNameTextView.text = details.title  // Setting the recipe title to the TextView.

        val imageView: ImageView = findViewById(R.id.iv_recipe_image)
        Glide.with(this).load(details.image).into(imageView)  // Using Glide to load and display the recipe image.
    }

    // Configures the ViewPager with fragments for recipe overview, ingredients, steps, and nutrition.
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

        // Setting custom views for tabs and managing their selection.
        val tabTitles = listOf("Overview", "Ingredients", "Steps", "Nutrition")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTab = layoutInflater.inflate(R.layout.tab_item, tabLayout, false) as LinearLayout
            val tabTextView = customTab.findViewById<TextView>(R.id.tab_title)
            tabTextView.text = tabTitles[position]
            tab.customView = customTab
        }.attach()

        // Handling page changes to update tab styles.
        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabStyles(position)
            }
        })

        // Set the initial style for the selected tab.
        updateTabStyles(0)
    }

    // Applies styling changes to the tabs based on the currently selected tab.
    private fun updateTabStyles(selectedPosition: Int) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val tabView = tab?.customView as? LinearLayout

            if (i == selectedPosition) {
                tabView?.setBackgroundResource(R.drawable.tab_selected_background)  // Apply the selected background to the current tab.
                tabView?.isSelected = true
            } else {
                tabView?.background = null  // Remove background for unselected tabs.
                tabView?.isSelected = false
            }
        }
    }

    // Displays a toast message for various error states.
    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}
