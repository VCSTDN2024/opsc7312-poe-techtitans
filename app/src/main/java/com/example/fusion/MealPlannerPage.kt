package com.example.fusion

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import com.example.fusion.utils.TranslationUtil
import com.example.fusion.utils.TranslationUtil.loadLanguagePreference
import com.google.android.material.bottomnavigation.BottomNavigationView

class MealPlannerPage : AppCompatActivity() {

    // Variable to store the BottomNavigationView
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge() // Enable edge-to-edge display for immersive UI
        setContentView(R.layout.activity_meal_planner_page)

        // Initialize the BottomNavigationView and set it up
        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        setupBottomNavigation() // Call method to configure the bottom navigation behavior

        // Set click listeners for each day's card to navigate to ChooseTimePage with the selected day
        findViewById<CardView>(R.id.cardMonday)?.setOnClickListener { navigateToChooseTimePage("Monday") }
        findViewById<CardView>(R.id.cardTuesday)?.setOnClickListener { navigateToChooseTimePage("Tuesday") }
        findViewById<CardView>(R.id.cardWednesday)?.setOnClickListener { navigateToChooseTimePage("Wednesday") }
        findViewById<CardView>(R.id.cardThursday)?.setOnClickListener { navigateToChooseTimePage("Thursday") }
        findViewById<CardView>(R.id.cardFriday)?.setOnClickListener { navigateToChooseTimePage("Friday") }
        findViewById<CardView>(R.id.cardSaturday)?.setOnClickListener { navigateToChooseTimePage("Saturday") }
        findViewById<CardView>(R.id.cardSunday)?.setOnClickListener { navigateToChooseTimePage("Sunday") }
        applyTranslations()
    }

    private fun applyTranslations() {
        val textViewsToTranslate = listOf(
            findViewById<TextView>(R.id.titleText),
            findViewById<TextView>(R.id.Monday),
            findViewById<TextView>(R.id.Tuesday),
            findViewById<TextView>(R.id.Wednesday),
            findViewById<TextView>(R.id.Thursday),
            findViewById<TextView>(R.id.Friday),
            findViewById<TextView>(R.id.Saturday),
            findViewById<TextView>(R.id.Sunday),
        )

        if (loadLanguagePreference(this) == "af") {
            // Apply translations to these text views if necessary
            TranslationUtil.translateTextViews(this, textViewsToTranslate, "af")
        }
    }

    // Function to navigate to ChooseTimePage and pass the selected day as an intent extra
    private fun navigateToChooseTimePage(selectedDay: String) {
        val intent =
            Intent(this, ChooseTimePage::class.java) // Create intent to start ChooseTimePage
        intent.putExtra("DAY_SELECTED", selectedDay) // Pass the selected day as extra data
        startActivity(intent) // Start the ChooseTimePage activity
    }

    // Method to configure the behavior of the bottom navigation bar
    private fun setupBottomNavigation() {
        // Set the default selected item in the bottom navigation to the calendar section
        bottomNavigationView.selectedItemId = R.id.navigation_calendar

        // Set listener for bottom navigation item selection
        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    startActivity(Intent(this, HomePage::class.java)) // Navigate to HomePage
                    true
                }

                R.id.navigation_saved -> {
                    startActivity(
                        Intent(
                            this,
                            FavoritesPage::class.java
                        )
                    ) // Navigate to FavoritesPage
                    true
                }

                R.id.navigation_calendar -> {
                    // Stay on the current page since it's already selected
                    true
                }

                R.id.navigation_cart -> {
                    startActivity(
                        Intent(
                            this,
                            ShoppingListPage::class.java
                        )
                    ) // Navigate to ShoppingListPage
                    true
                }

                R.id.navigation_settings -> {
                    startActivity(
                        Intent(
                            this,
                            SettingsPage::class.java
                        )
                    ) // Navigate to SettingsPage
                    true
                }

                else -> false // Return false if none of the cases match
            }
        }
    }
}
