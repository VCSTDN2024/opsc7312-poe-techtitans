package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fusion.api.RetrofitInstance
import com.example.fusion.model.Recipe
import com.example.fusion.model.RecipeDetailsResponse
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MealPlannerMeal : AppCompatActivity() {

    private lateinit var selectedDay: String
    private lateinit var selectedMealTime: String
    private lateinit var rvMealRecipes: RecyclerView
    private lateinit var recipeAdapter: RecipeAdapter
    private val apiKey = BuildConfig.API_KEY
    private lateinit var bottomNavigationView: BottomNavigationView // Declare it here



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner_meal)

        // Set up Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigation()
        // Get the selected day and meal time from the Intent
        selectedDay = intent.getStringExtra("DAY_SELECTED").toString()
        selectedMealTime = intent.getStringExtra("MEAL_TIME").toString()

        // Set the meal time text
        val mealTimeText: TextView = findViewById(R.id.mealTimeText)
        mealTimeText.text = selectedMealTime

        // Initialize RecyclerView
        rvMealRecipes = findViewById(R.id.rv_meal_recipes)
        rvMealRecipes.layoutManager = GridLayoutManager(this, 2)
        recipeAdapter = RecipeAdapter(this, mutableListOf())
        rvMealRecipes.adapter = recipeAdapter

        // Load recipes from Firebase
        loadRecipesFromFirebase()

        // Back arrow functionality
        findViewById<ImageView>(R.id.backArrow).setOnClickListener {
            onBackPressed()
        }
    }
    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_calendar

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
                    // Stay on this page
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
    private fun loadRecipesFromFirebase() {
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser != null) {
            val userId = currentUser.uid
            val database = FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
                .getReference("users").child(userId).child("meal_plans")

            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var foundRecipes = false
                    for (mealPlanSnapshot in snapshot.children) {
                        val mealPlan = mealPlanSnapshot.value as? Map<*, *>
                        val day = mealPlan?.get("day") as? String
                        val mealTime = mealPlan?.get("mealTime") as? String
                        val recipeId = mealPlan?.get("recipeId")?.toString()

                        if (day == selectedDay && mealTime == selectedMealTime && recipeId != null) {
                            foundRecipes = true
                            loadRecipeDetailsFromAPI(recipeId)
                        }
                    }

                    if (!foundRecipes) {
                        Toast.makeText(this@MealPlannerMeal, "No recipes found for $selectedMealTime on $selectedDay", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MealPlannerMeal, "Failed to load recipes", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadRecipeDetailsFromAPI(recipeId: String) {
        val call = RetrofitInstance.api.getRecipeInformation(recipeId.toInt(), apiKey)

        call.enqueue(object : Callback<RecipeDetailsResponse> {
            override fun onResponse(call: Call<RecipeDetailsResponse>, response: Response<RecipeDetailsResponse>) {
                if (response.isSuccessful) {
                    val recipeDetails = response.body()
                    recipeDetails?.let {
                        val recipe = Recipe(
                            id = it.id,
                            title = it.title,
                            image = it.image
                        )
                        updateRecipeList(recipe)
                    }
                } else {
                    Toast.makeText(this@MealPlannerMeal, "Failed to fetch recipe details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<RecipeDetailsResponse>, t: Throwable) {
                Toast.makeText(this@MealPlannerMeal, "Error: ${t.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateRecipeList(recipe: Recipe) {
        val currentList = recipeAdapter.getRecipeList().toMutableList()
        currentList.add(recipe)
        recipeAdapter.updateData(currentList)
    }
}