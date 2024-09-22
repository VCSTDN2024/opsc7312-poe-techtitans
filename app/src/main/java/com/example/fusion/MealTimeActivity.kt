package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class MealTimeActivity : AppCompatActivity() {
    private lateinit var selectedDay: String
    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_time)

        selectedDay = intent.getStringExtra("DAY_SELECTED").toString()

        // Get the recipeId from the intent
        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        findViewById<Button>(R.id.btn_breakfast).setOnClickListener { saveMealPlan("Breakfast") }
        findViewById<Button>(R.id.btn_lunch).setOnClickListener { saveMealPlan("Lunch") }
        findViewById<Button>(R.id.btn_dinner).setOnClickListener { saveMealPlan("Dinner") }
        findViewById<Button>(R.id.btn_snack).setOnClickListener { saveMealPlan("Snack") }
    }

    private fun saveMealPlan(mealTime: String) {
        val currentUser = FirebaseAuth.getInstance().currentUser

        if (currentUser != null) {
            val userId = currentUser.uid

            val mealPlan = mapOf(
                "day" to selectedDay,
                "mealTime" to mealTime,
                "recipeId" to recipeId
            )

            // Reference to users/<userId>/meal_plans in Firebase
            val database = FirebaseDatabase.getInstance("https://fusion-14429-default-rtdb.firebaseio.com/")
                .getReference("users").child(userId).child("meal_plans")

            database.push().setValue(mealPlan)
                .addOnSuccessListener {
                    Toast.makeText(this, "Meal plan saved!", Toast.LENGTH_SHORT).show()
                    val intent = Intent(this,HomePage::class.java)
                    startActivity(intent) // Go back after saving
                }
                .addOnFailureListener {
                    Toast.makeText(this, "Failed to save meal plan", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "No user logged in!", Toast.LENGTH_SHORT).show()
        }
    }
}
