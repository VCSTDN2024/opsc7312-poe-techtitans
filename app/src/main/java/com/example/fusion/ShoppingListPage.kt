package com.example.fusion

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import com.google.android.material.bottomnavigation.BottomNavigationView

class ShoppingListPage : AppCompatActivity() {

    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_shopping_list_page)

        // Initialize bottom navigation view
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Handle window insets to ensure proper padding
        ViewCompat.setOnApplyWindowInsetsListener(bottomNavigationView) { view, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            view.updatePadding(bottom = systemBars.bottom)
            insets
        }

        // Setup bottom navigation
        setupBottomNavigation()
    }

    private fun setupBottomNavigation() {
        bottomNavigationView.selectedItemId = R.id.navigation_cart

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
                R.id.navigation_cart -> true
                R.id.navigation_settings -> {
                    startActivity(Intent(this, SettingsPage::class.java))
                    true
                }
                else -> false
            }
        }
    }
}