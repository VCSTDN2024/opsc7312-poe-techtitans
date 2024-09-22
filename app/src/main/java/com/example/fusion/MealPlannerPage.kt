package com.example.fusion

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.google.android.material.bottomnavigation.BottomNavigationView

class MealPlannerPage : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_meal_planner_page)

        // Set up Bottom Navigation
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigation()

        // Set click listeners on each day card
        findViewById<CardView>(R.id.cardMonday)?.setOnClickListener { navigateToChooseTimePage("Monday") }
        findViewById<CardView>(R.id.cardTuesday)?.setOnClickListener { navigateToChooseTimePage("Tuesday") }
        findViewById<CardView>(R.id.cardWednesday)?.setOnClickListener { navigateToChooseTimePage("Wednesday") }
        findViewById<CardView>(R.id.cardThursday)?.setOnClickListener { navigateToChooseTimePage("Thursday") }
        findViewById<CardView>(R.id.cardFriday)?.setOnClickListener { navigateToChooseTimePage("Friday") }
        findViewById<CardView>(R.id.cardSaturday)?.setOnClickListener { navigateToChooseTimePage("Saturday") }
        findViewById<CardView>(R.id.cardSunday)?.setOnClickListener { navigateToChooseTimePage("Sunday") }
    }

    // Function to navigate to ChooseTimePage and pass the selected day
    private fun navigateToChooseTimePage(selectedDay: String) {
        val intent = Intent(this, ChooseTimePage::class.java)
        intent.putExtra("DAY_SELECTED", selectedDay)
        startActivity(intent)
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
}
