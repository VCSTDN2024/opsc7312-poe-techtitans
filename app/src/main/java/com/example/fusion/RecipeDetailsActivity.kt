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

class RecipeDetailsActivity : AppCompatActivity() {

    private lateinit var viewPager: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var bottomNavigationView: BottomNavigationView
    private val apiKey = BuildConfig.API_KEY
    private val databaseReference = FirebaseDatabase.getInstance().getReference("users")

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
                        checkIfFavoriteAndSave(it)
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

    private fun checkIfFavoriteAndSave(recipeDetails: RecipeDetailsResponse) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            databaseReference.child(it).child("favorites").child(recipeId.toString())
                .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        if (snapshot.exists()) {  // Check if the recipe is marked as favorite
                            saveRecipeDetailsToFirebase(recipeDetails)
                        }
                    }

                    override fun onCancelled(error: DatabaseError) {
                        showError("Failed to check if recipe is favorite.")
                    }
                })
        }
    }

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

    private fun displayRecipeDetails(details: RecipeDetailsResponse) {
        val imageView: ImageView = findViewById(R.id.iv_recipe_image)
        Glide.with(this).load(details.image).into(imageView)
    }

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

        val tabTitles = listOf("Overview", "Ingredients", "Steps", "Nutrition")
        TabLayoutMediator(tabLayout, viewPager) { tab, position ->
            val customTab = layoutInflater.inflate(R.layout.tab_item, tabLayout, false) as LinearLayout
            val tabTextView = customTab.findViewById<TextView>(R.id.tab_title)
            tabTextView.text = tabTitles[position]
            tab.customView = customTab
        }.attach()

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                updateTabStyles(position)
            }
        })

        // Initialize the first tab with the selected background
        updateTabStyles(0)
    }

    private fun updateTabStyles(selectedPosition: Int) {
        for (i in 0 until tabLayout.tabCount) {
            val tab = tabLayout.getTabAt(i)
            val tabView = tab?.customView as? LinearLayout

            if (i == selectedPosition) {
                tabView?.setBackgroundResource(R.drawable.tab_selected_background)  // Set blue box with curved corners
                tabView?.isSelected = true
            } else {
                tabView?.background = null  // Remove background for unselected tabs
                tabView?.isSelected = false
            }
        }
    }

    private fun showError(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
}