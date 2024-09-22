package com.example.fusion

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener

class MealPlannerMeal : AppCompatActivity() {

    private lateinit var selectedDay: String
    private lateinit var selectedMealTime: String
    private lateinit var recipeContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner_meal)

        // Get the selected day and meal time from Intent extras
        selectedDay = intent.getStringExtra("DAY_SELECTED").toString()
        selectedMealTime = intent.getStringExtra("MEAL_TIME").toString()

        // Set the meal time text
        val mealTimeText: TextView = findViewById(R.id.mealTimeText)
        mealTimeText.text = selectedMealTime

        // Initialize recipe container
        recipeContainer = findViewById(R.id.recipeContainer)

        // Load recipes from Firebase
        loadRecipesFromFirebase()

        // Back arrow functionality
        findViewById<View>(R.id.backArrow).setOnClickListener {
            onBackPressed()
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
                    recipeContainer.removeAllViews()  // Clear previous views
                    for (mealPlanSnapshot in snapshot.children) {
                        val mealPlan = mealPlanSnapshot.value as Map<*, *>
                        val day = mealPlan["day"] as? String
                        val mealTime = mealPlan["mealTime"] as? String
                        val recipeId = mealPlan["recipeId"] as? Long  // Use Long instead of Int, Firebase may store it as a Long

                        // Check if the meal plan matches the selected day and meal time
                        if (day == selectedDay && mealTime == selectedMealTime) {
                            // Inflate the new recipe_view_item.xml layout
                            val recipeView = LayoutInflater.from(this@MealPlannerMeal)
                                .inflate(R.layout.recipe_view_item, recipeContainer, false)

                            // Set the data for the recipe
                            val recipeTitle = recipeView.findViewById<TextView>(R.id.tv_recipe_title)
                            val recipeDescription = recipeView.findViewById<TextView>(R.id.tv_recipe_description)

                            // Here, set the recipe title and description dynamically
                            recipeTitle.text = "Recipe ID: $recipeId" // You can replace this with actual recipe title if you have one
                            recipeDescription.text = "Description for Recipe ID $recipeId"  // Set a description if available

                            // Add the recipe view to the container
                            recipeContainer.addView(recipeView)
                        }
                    }

                    if (recipeContainer.childCount == 0) {
                        // Show a message if no recipes are found
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
}
