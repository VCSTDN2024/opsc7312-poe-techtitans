package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView

class ChooseTimePage : AppCompatActivity() {
    private lateinit var selectedDay: String
//yer
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choose_time_page)

        // Get the selected day from the Intent passed from MealPlannerPage
        selectedDay = intent.getStringExtra("DAY_SELECTED").toString()

        // Back arrow functionality
        val backArrow: ImageView = findViewById(R.id.btnBack)
        backArrow.setOnClickListener {
            onBackPressed()  // Navigate back to the previous screen
        }

        // Set click listeners for each meal time option
        findViewById<androidx.cardview.widget.CardView>(R.id.breakfastCard).setOnClickListener {
            navigateToMealPlannerMeal("Breakfast")
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.lunchCard).setOnClickListener {
            navigateToMealPlannerMeal("Lunch")
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.dinnerCard).setOnClickListener {
            navigateToMealPlannerMeal("Dinner")
        }
        findViewById<androidx.cardview.widget.CardView>(R.id.snackCard).setOnClickListener {
            navigateToMealPlannerMeal("Snack")
        }

        // Setup Bottom Navigation
        setupBottomNavigation()
    }

    // Function to pass the day and meal time to MealPlannerMeal
    private fun navigateToMealPlannerMeal(mealTime: String) {
        val intent = Intent(this, MealPlannerMeal::class.java)
        intent.putExtra("DAY_SELECTED", selectedDay)  // Pass the selected day
        intent.putExtra("MEAL_TIME", mealTime)  // Pass the selected meal time
        startActivity(intent)
    }

    private fun setupBottomNavigation() {
        val bottomNavigationView: BottomNavigationView = findViewById(R.id.bottomNavigationView)
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
