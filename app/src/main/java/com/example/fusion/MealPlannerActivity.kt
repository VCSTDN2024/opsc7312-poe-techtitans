package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MealPlannerActivity : AppCompatActivity() {

    private var recipeId: Int = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meal_planner)

        // Get the recipeId from the intent
        recipeId = intent.getIntExtra("RECIPE_ID", -1)

        findViewById<Button>(R.id.btn_monday).setOnClickListener { openMealTime("Monday") }
        findViewById<Button>(R.id.btn_tuesday).setOnClickListener { openMealTime("Tuesday") }
        findViewById<Button>(R.id.btn_wednesday).setOnClickListener { openMealTime("Wednesday") }
        findViewById<Button>(R.id.btn_thursday).setOnClickListener { openMealTime("Thursday") }
        findViewById<Button>(R.id.btn_friday).setOnClickListener { openMealTime("Friday") }
        findViewById<Button>(R.id.btn_saturday).setOnClickListener { openMealTime("Saturday") }
        findViewById<Button>(R.id.btn_sunday).setOnClickListener { openMealTime("Sunday") }
    }

    // Open MealTimeActivity and pass the selected day and recipeId
    private fun openMealTime(day: String) {
        val intent = Intent(this, MealTimeActivity::class.java)
        intent.putExtra("DAY_SELECTED", day)
        intent.putExtra("RECIPE_ID", recipeId) // Pass the recipeId to MealTimeActivity
        startActivity(intent)
    }
}