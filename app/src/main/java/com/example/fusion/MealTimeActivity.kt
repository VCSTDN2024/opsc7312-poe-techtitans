package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MealTimeActivity : AppCompatActivity() {
    // Variable to store the selected day and recipe ID
    private lateinit var selectedDay: String
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_time)

        // Retrieve the selected day from the intent passed from the previous activity
        selectedDay = intent.getStringExtra("DAY_SELECTED").toString()

        // Retrieve the recipe ID from the intent, defaulting to -1 if not found
        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        // Set up the "Done" button click listener to save the selected meal time
        findViewById<Button>(R.id.buttonDone).setOnClickListener {
            // Get the selected meal time
            val selectedMealTime = getSelectedMealTime()
            if (selectedMealTime != null) {
                // If a meal time is selected, save the meal plan
                saveMealPlan(selectedMealTime)
            } else {
                // Display a Toast message if no meal time is selected
                Toast.makeText(this, "Please select a meal time", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Method to determine the selected meal time based on the checked CheckBox
    private fun getSelectedMealTime(): String? {
        // References to all the CheckBoxes for meal times
        val breakfastCheckBox = findViewById<CheckBox>(R.id.checkBoxBreakfast)
        val lunchCheckBox = findViewById<CheckBox>(R.id.checkBoxLunch)
        val dinnerCheckBox = findViewById<CheckBox>(R.id.checkBoxDinner)
        val snackCheckBox = findViewById<CheckBox>(R.id.checkBoxSnack)

        // Return the selected meal time based on the checked CheckBox, or null if none are selected
        return when {
            breakfastCheckBox.isChecked -> "Breakfast"
            lunchCheckBox.isChecked -> "Lunch"
            dinnerCheckBox.isChecked -> "Dinner"
            snackCheckBox.isChecked -> "Snack"
            else -> null // No meal time selected
        }
    }

    // Method to save the meal plan to Firebase
    private fun saveMealPlan(mealTime: String) {
        // Get the currently logged-in user from Firebase Authentication
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            // Retrieve the user's unique ID
            val userId = currentUser.uid

            // Create a meal plan map containing the selected day, meal time, and recipe ID
            val mealPlan = mapOf(
                "day" to selectedDay,
                "mealTime" to mealTime,
                "recipeId" to recipeId
            )

            // Reference to the 'meal_plans' node in the Firebase Realtime Database under the user's ID
            val database = FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
                .getReference("users").child(userId).child("meal_plans")

            // Push the new meal plan to the database and handle success or failure
            database.push().setValue(mealPlan)
                .addOnSuccessListener {
                    // Show a Toast message upon successful save
                    Toast.makeText(this, "Meal plan saved!", Toast.LENGTH_SHORT).show()
                    // Navigate back to the HomePage after saving
                    val intent = Intent(this, HomePage::class.java)
                    startActivity(intent)
                }
                .addOnFailureListener {
                    // Display a Toast message if saving fails
                    Toast.makeText(this, "Failed to save meal plan", Toast.LENGTH_SHORT).show()
                }
        } else {
            // Display a Toast message if no user is logged in
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
        }
    }
}
